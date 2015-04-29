/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.cake3.ui.actions.gotos.status;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.editor.visitors.ControllerVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Pair;

/**
 * Behavior for a current controller/view cell file of Go To Action.
 *
 * <ul>
 * <li>Table</li>
 * <li>Template</li>
 * <li>Helper</li>
 * <li>Component</li>
 * <li>TestCase</li>
 * </ul>
 *
 * @author junichi11
 */
class ControllerStatus extends CakePHP3GoToStatus {

    private final List<GoToItem> components = new ArrayList<>();
    private final List<GoToItem> helpers = new ArrayList<>();
    private final List<GoToItem> tables = new ArrayList<>();
    private final List<GoToItem> templates = new ArrayList<>();
    private final List<GoToItem> allTemplates = new ArrayList<>();
    private String themeName = ""; // NOI18N

    public ControllerStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpmodule, FileObject fileObject, int offset) {
        clear();
        ControllerVisitor visitor = new ControllerVisitor(phpmodule, fileObject, false, offset);
        try {
            scan(visitor, fileObject);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        themeName = visitor.getThemeName();
        addItems(Category.COMPONENT, visitor.getComponents());
        addItems(Category.HELPER, visitor.getHelpers());
        addItems(Category.TABLE, visitor.getTables());
        addTemplates(visitor, fileObject);
        addAllTemplates(visitor, fileObject);
    }

    private void addItems(Category category, List<Pair<String, PhpClass>> phpClasses) {
        for (Pair<String, PhpClass> clazz : phpClasses) {
            PhpClass phpClass = clazz.second();
            FileObject file = phpClass.getFile();
            if (file == null) {
                continue;
            }
            switch (category) {
                case COMPONENT:
                    components.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                case HELPER:
                    helpers.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                case TABLE:
                    tables.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                default:
                    break;
            }
        }
    }

    protected void addTemplates(ControllerVisitor visitor, FileObject controller) {
        List<String> names = visitor.getTemplateNames();
        if (names.isEmpty() || controller == null) {
            return;
        }
        List<String> themeNames = visitor.getThemeNames();
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(controller);
        for (String name : names) {
            // theme
            if (!StringUtils.isEmpty(themeName)) {
                FileObject themeTemplate = getTemplate(name, controller, themeName);
                if (themeTemplate != null) {
                    templates.add(GoToItemFactory.create(cakeModule.getCategory(themeTemplate), themeTemplate, DEFAULT_OFFSET));
                }
            }
            for (String tn : themeNames) {
                if (tn.equals(themeName)) {
                    continue;
                }
                FileObject themeTemplate = getTemplate(name, controller, tn);
                if (themeTemplate != null) {
                    templates.add(GoToItemFactory.create(cakeModule.getCategory(themeTemplate), themeTemplate, DEFAULT_OFFSET));
                }
            }

            FileObject template = getTemplate(name, controller);
            if (template == null) {
                return;
            }
            templates.add(GoToItemFactory.create(cakeModule.getCategory(template), template, DEFAULT_OFFSET));
        }
    }

    protected void addAllTemplates(ControllerVisitor visitor, FileObject controller) {
        List<String> names = visitor.getAllTemplateNames();
        if (names.isEmpty() || controller == null) {
            return;
        }
        List<String> themeNames = visitor.getAllThemeNames();
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(controller);
        for (String name : names) {
            // theme
            if (!StringUtils.isEmpty(themeName)) {
                FileObject themeTemplate = getTemplate(name, controller, themeName);
                if (themeTemplate != null) {
                    allTemplates.add(GoToItemFactory.create(cakeModule.getCategory(themeTemplate), themeTemplate, DEFAULT_OFFSET));
                }
            }
            for (String tn : themeNames) {
                if (tn.equals(themeName)) {
                    continue;
                }
                FileObject themeTemplate = getTemplate(name, controller, tn);
                if (themeTemplate != null) {
                    allTemplates.add(GoToItemFactory.create(cakeModule.getCategory(themeTemplate), themeTemplate, DEFAULT_OFFSET));
                }
            }

            FileObject template = getTemplate(name, controller);
            if (template == null) {
                continue;
            }
            allTemplates.add(GoToItemFactory.create(cakeModule.getCategory(template), template, DEFAULT_OFFSET));
        }
    }

    private FileObject getTemplate(String name, FileObject controller) {
        return getTemplate(name, controller, ""); // NOI18N
    }

    private FileObject getTemplate(String name, FileObject controller, String themeName) {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(controller);
        String relativePath = cakeModule.toPhpFileName(Category.TEMPLATE, name);
        return cakeModule.getTemplate(relativePath, controller, themeName);
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> items = new ArrayList<>(templates);
        if (templates.isEmpty()) {
            items.addAll(getTemplates());
        }
        items.addAll(getTables());
        items.addAll(getEntities());
        items.addAll(getTestCases());
        items.addAll(getComponents());
        items.addAll(getHelpers());
        return items;
    }

    @Override
    public List<GoToItem> getComponents() {
        return new ArrayList<>(components);
    }

    @Override
    public List<GoToItem> getHelpers() {
        return new ArrayList<>(helpers);
    }

    @Override
    public List<GoToItem> getTables() {
        return new ArrayList<>(tables);
    }

    @Override
    public List<GoToItem> getTemplates() {
        return new ArrayList<>(allTemplates);
    }

    private void clear() {
        components.clear();
        helpers.clear();
        tables.clear();
        templates.clear();
        allTemplates.clear();
    }

}
