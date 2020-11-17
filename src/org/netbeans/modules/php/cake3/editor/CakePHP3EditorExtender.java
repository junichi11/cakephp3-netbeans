/*
 * Copyright 2019 junichi11.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netbeans.modules.php.cake3.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.editor.visitors.ControllerVisitor;
import org.netbeans.modules.php.cake3.editor.visitors.HelperVisitor;
import org.netbeans.modules.php.cake3.editor.visitors.ViewVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
import org.netbeans.modules.php.cake3.modules.CakePHPModule.Base;
import org.netbeans.modules.php.cake3.modules.CakePHPModule.Category;
import org.netbeans.modules.php.cake3.modules.ModuleUtils;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public class CakePHP3EditorExtender extends EditorExtender {

    private final PhpModule phpModule;
    private Category category = Category.UNKNOWN;
    private static final Logger LOGGER = Logger.getLogger(CakePHP3EditorExtender.class.getName());

    public CakePHP3EditorExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        // Enabled PhpBaseElement is just PhpVariable
        CakePHPModule cakeModule = CakePHPModule.forPhpModule(phpModule);
        String ext = fo.getExt();
        if (!ext.equals("php") && !ext.equals(cakeModule.getCtpExt())) { // NOI18N
            return Collections.emptyList();
        }
        if (cakeModule.isTemplateFile(fo)) {
            category = Category.TEMPLATE;
        } else {
            category = cakeModule.getCategory(fo);
        }

        if (!isEnabledCategory(category)) {
            return Collections.emptyList();
        }

        List<PhpBaseElement> elements = new ArrayList<>();

        Base base = cakeModule.getBase(fo);
        if (base == Base.UNKNOWN) {
            return elements;
        }

        // plugin
        String pluginName = null;
        if (base == Base.PLUGIN) {
            pluginName = cakeModule.getPluginName(fo);
        }

        // XXX should not be parsed
        parseAppController(cakeModule, base, pluginName, elements);

        parseAppView(cakeModule, base, pluginName, elements);

        parseCurrentFile(fo, elements);

        return elements;
    }

    private void parseAppController(CakePHPModule cakeModule, Base base, String pluginName, List<PhpBaseElement> elements) {
        FileObject appController = cakeModule.getFile(base, Category.CONTROLLER, "AppController.php", pluginName); // NOI18N
        if (appController != null) {
            Set<PhpClass> phpClasses = parseFields(appController, Category.CONTROLLER);
            for (PhpClass phpClass : phpClasses) {
                elements.add(new PhpVariable("$this", phpClass, null, 0)); // NOI18N
            }
        }
    }

    private void parseAppView(CakePHPModule cakeModule, Base base, String pluginName, List<PhpBaseElement> elements) {
        if (ModuleUtils.isTemplate(category)) {
            FileObject appView = cakeModule.getFile(base, Category.VIEW, "AppView.php", pluginName); // NOI18N
            if (appView != null) {
                Set<PhpClass> phpClasses = parseView(appView);
                for (PhpClass phpClass : phpClasses) {
                    elements.add(new PhpVariable("$this", phpClass, null, 0)); // NOI18N
                }
            }
        }
    }

    private void parseCurrentFile(FileObject fo, List<PhpBaseElement> elements) {
        Set<PhpClass> phpClasses = parseFields(fo, category);
        for (PhpClass phpClass : phpClasses) {
            elements.add(new PhpVariable("$this", phpClass)); // NOI18N
        }
    }

    private Set<PhpClass> parseFields(FileObject fo, final Category category) {
        FileObject target = fo;
        PhpClass phpClass = getPhpClass();
        if (phpClass == null) {
            return Collections.emptySet();
        }
        String fileName = fo.getName();
        // if category is template, get a controller file for a template file
        if (!"AppController".equals(fileName) && category == Category.TEMPLATE) {
            CakePHPModule cakeModule = CakePHPModule.forPhpModule(phpModule);
            target = cakeModule.getController(fo);
            if (target == null) {
                return Collections.singleton(phpClass);
            }
        }

        final Set<Pair<String, PhpClass>> phpClasses = new HashSet<>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(target)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    PHPParseResult result = (PHPParseResult) resultIterator.getParserResult();
                    if (result == null) {
                        return;
                    }
                    if (category == Category.CONTROLLER) {
                        ControllerVisitor controllerVisitor = new ControllerVisitor(phpModule);
                        controllerVisitor.scan(Utils.getRoot(result));
                        phpClasses.addAll(controllerVisitor.getPhpClasses());
                    } else if (ModuleUtils.isTemplate(category)) {
                        ControllerVisitor controllerVisitor = new ControllerVisitor(phpModule, true);
                        controllerVisitor.scan(Utils.getRoot(result));
                        phpClasses.addAll(controllerVisitor.getPhpClasses());
                    } else if (category == Category.HELPER) {
                        HelperVisitor helperVisitor = new HelperVisitor(phpModule);
                        helperVisitor.scan(Utils.getRoot(result));
                        phpClasses.addAll(helperVisitor.getPhpClasses());
                    }
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        for (Pair<String, PhpClass> clazz : phpClasses) {
            phpClass.addField(clazz.first(), clazz.second(), clazz.second().getFile(), 0);
        }
        return Collections.singleton(phpClass);
    }

    private Set<PhpClass> parseView(FileObject appView) {
        if (!ModuleUtils.isTemplate(category)) {
            return Collections.emptySet();
        }

        FileObject target = appView;
        PhpClass phpClass = getPhpClass();
        if (phpClass == null) {
            return Collections.emptySet();
        }

        final Set<Pair<String, PhpClass>> phpClasses = new HashSet<>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(target)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult result = (ParserResult) resultIterator.getParserResult();
                    if (result == null) {
                        return;
                    }
                    ViewVisitor visitor = new ViewVisitor(phpModule);
                    visitor.scan(Utils.getRoot(result));
                    phpClasses.addAll(visitor.getPhpClasses());
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        for (Pair<String, PhpClass> clazz : phpClasses) {
            phpClass.addField(clazz.first(), clazz.second(), clazz.second().getFile(), 0);
        }
        return Collections.singleton(phpClass);
    }

    @CheckForNull
    private PhpClass getPhpClass() {
        String fullyQualifiedName;
        String className;
        switch (category) {
            case CONTROLLER:
                // XXX use AppController?
                className = "Controller"; // NOI18N
                fullyQualifiedName = "\\Cake\\Controller\\Controller"; // NOI18N
                break;
            case COMPONENT:
                className = "Component"; // NOI18N
                fullyQualifiedName = "\\Cake\\Controller\\Component"; // NOI18N
                break;
            case HELPER:
                className = "Helper"; // NOI18N
                fullyQualifiedName = "\\Cake\\View\\Helper"; // NOI18N
                break;
            case TEMPLATE: // fallthrough
            case TEMPLATE_CELL:
            case ELEMENT:
            case EMAIL:
            case ERROR:
            case PAGES:
            case LAYOUT:
                // XXX use AppView?
                className = "View"; // NOI18N
                fullyQualifiedName = "\\Cake\\View\\View"; // NOI18N
                break;
            default:
                return null;
        }
        return new PhpClass(className, fullyQualifiedName);
    }

    private boolean isEnabledCategory(Category category) {
        return category == Category.CONTROLLER
                || category == Category.COMPONENT
                || category == Category.TEMPLATE
                || category == Category.TEMPLATE_CELL
                || category == Category.ELEMENT
                || category == Category.EMAIL
                || category == Category.ERROR
                || category == Category.LAYOUT
                || category == Category.PAGES
                || category == Category.HELPER;
    }

}
