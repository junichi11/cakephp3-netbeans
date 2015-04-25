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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.editor.visitors.ControllerVisitor;
import org.netbeans.modules.php.cake3.editor.visitors.TemplateVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.modules.ModuleUtils;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Pair;

class TemplateStatus extends CakePHP3GoToStatus {

    private final List<GoToItem> controllers = new ArrayList<>();
    private final List<GoToItem> helpers = new ArrayList<>();
    private final Set<GoToItem> templates = new HashSet<>();
    private final Set<GoToItem> elements = new HashSet<>();
    private final Set<GoToItem> extendz = new HashSet<>();
    private final Set<GoToItem> templateCells = new HashSet<>();
    private final Set<GoToItem> viewCells = new HashSet<>(); // XXX sort
    private boolean isTemplateCell = false;
    private static final String DEFAULT_CELL_METHOD = "display"; // NOI18N

    public TemplateStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
        clear();
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        Category category = cakeModule.getCategory(fileObject);
        isTemplateCell = category == Category.TEMPLATE_CELL;

        FileObject target = null;
        FileObject controller = cakeModule.getController(fileObject);
        if (controller != null) {
            target = controller;
            addControllers(controller, fileObject.getName());
        }

        FileObject viewCell = cakeModule.getViewCell(fileObject);
        if (viewCell != null) {
            target = viewCell;
            addViewCells(viewCell, fileObject.getName());
        }

        if (target != null) {
            ControllerVisitor controllerVisitor = new ControllerVisitor(phpModule, true);
            try {
                scan(controllerVisitor, target);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
            addTemplates(controllerVisitor.getAllTemplateNames(), target);
            addHelpers(controllerVisitor.getHelpers());
        }

        // template
        TemplateVisitor templateVisitor = new TemplateVisitor(phpModule);
        try {
            scan(templateVisitor, fileObject);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        addExtends(templateVisitor.getExtends(), phpModule, fileObject);
        addElements(templateVisitor.getElements(), phpModule, fileObject);
        addCells(templateVisitor.getCells(), phpModule, fileObject);

        // TODO app view
    }

    private void addControllers(FileObject controller, String templateName) {
        int offset = getOffset(controller, templateName);
        controllers.add(GoToItemFactory.create(Category.CONTROLLER, controller, DEFAULT_OFFSET));
        if (offset > 0) {
            controllers.add(GoToItemFactory.create(Category.CONTROLLER, controller, offset, templateName));
        }
    }

    private void addViewCells(FileObject viewCell, String templateName) {
        int offset = getOffset(viewCell, templateName);
        viewCells.add(GoToItemFactory.create(Category.VIEW_CELL, viewCell, DEFAULT_OFFSET));
        if (offset > 0) {
            viewCells.add(GoToItemFactory.create(Category.VIEW_CELL, viewCell, offset, templateName));
        }
    }

    private int getOffset(FileObject controller, String templateName) {
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        Collection<PhpClass> phpClasses = editorSupport.getClasses(controller);
        int offset = DEFAULT_OFFSET;
        for (PhpClass phpClass : phpClasses) {
            Collection<PhpClass.Method> methods = phpClass.getMethods();
            for (PhpClass.Method method : methods) {
                if (templateName.equals(method.getName())) {
                    offset = method.getOffset();
                }
            }
            // only the first php class
            break;
        }
        return offset;
    }

    private void addTemplates(List<String> templateNames, FileObject controller) {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(controller);
        for (String name : templateNames) {
            FileObject template = getTemplate(name, controller);
            if (template == null) {
                continue;
            }
            Category category = cakeModule.getCategory(template);
            if (category == Category.TEMPLATE_CELL) {
                templateCells.add(GoToItemFactory.create(category, template, DEFAULT_OFFSET));
            } else {
                templates.add(GoToItemFactory.create(category, template, DEFAULT_OFFSET));
            }
        }
    }

    private void addElements(Set<String> elementPaths, PhpModule phpModule, FileObject fileObject) {
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        Base base = cakeModule.getBase(fileObject);
        for (String elementPath : elementPaths) {
            Pair<String, String> pluginElement = ModuleUtils.pluginSplit(elementPath);
            String pluginName = pluginElement.first();
            String relativePath = cakeModule.toPhpFileName(Category.ELEMENT, pluginElement.second());
            if (!StringUtils.isEmpty(pluginName)) {
                base = Base.PLUGIN;
            }
            FileObject element = cakeModule.getFile(base, Category.ELEMENT, relativePath, pluginName);
            // fallback core
            if (element == null && StringUtils.isEmpty(pluginName)) {
                element = cakeModule.getFile(Base.CORE, Category.ELEMENT, elementPath, pluginName);
            }
            if (element == null) {
                continue;
            }
            elements.add(GoToItemFactory.create(Category.ELEMENT, element, DEFAULT_OFFSET));
        }
    }

    private void addExtends(Set<String> extendsPaths, PhpModule phpModule, FileObject fileObject) {
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        Base base = cakeModule.getBase(fileObject);
        String currentPluginName = null;
        if (base == Base.PLUGIN) {
            currentPluginName = cakeModule.getPluginName(fileObject);
        }
        List<FileObject> templateDirectories = cakeModule.getDirectories(base, Category.TEMPLATE, currentPluginName);
        for (String extendsPath : extendsPaths) {
            Pair<String, String> pluginElement = ModuleUtils.pluginSplit(extendsPath);
            String pluginName = pluginElement.first();
            String path = pluginElement.second();

            // not from template root directory
            StringBuilder sb = new StringBuilder();
            if (!path.startsWith("/")) { // NOI18N
                for (FileObject templateDirectory : templateDirectories) {
                    String tdPath = templateDirectory.getPath();
                    String currentFilePath = fileObject.getPath();
                    String controllerId = ""; // NOI18N
                    if (currentFilePath.startsWith(tdPath)) {
                        controllerId = currentFilePath.replace(tdPath + "/", ""); // NOI18N
                        int indexOfSlash = controllerId.indexOf("/"); // NOI18N
                        if (indexOfSlash != -1) {
                            controllerId = controllerId.substring(0, indexOfSlash);
                            sb.append(controllerId).append("/"); // NOI18N
                        }
                    }
                }
            }

            sb.append(cakeModule.toPhpFileName(Category.TEMPLATE, path));
            String relativePath = sb.toString();
            if (!StringUtils.isEmpty(pluginName)) {
                base = Base.PLUGIN;
            }
            FileObject template = cakeModule.getFile(base, Category.TEMPLATE, relativePath, pluginName);
            // fallback core
            if (template == null && StringUtils.isEmpty(pluginName)) {
                template = cakeModule.getFile(Base.CORE, Category.TEMPLATE, extendsPath, pluginName);
            }
            if (template == null) {
                continue;
            }
            extendz.add(GoToItemFactory.create(Category.TEMPLATE, template, DEFAULT_OFFSET));
        }
    }

    private void addCells(Set<String> cells, PhpModule phpModule, FileObject fileObject) {
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        Base base = cakeModule.getBase(fileObject);
        for (String cell : cells) {
            Pair<String, String> pluginSplit = ModuleUtils.pluginSplit(cell);
            String pluginName = pluginSplit.first();
            String cellName = pluginSplit.second();
            Pair<String, String> cellMethodSplit = ModuleUtils.cellMethodSplit(cellName);
            cellName = cellMethodSplit.first();
            String cellMethod = cellMethodSplit.second();
            if (cellMethod.isEmpty()) {
                // default
                cellMethod = DEFAULT_CELL_METHOD;
            }

            // view
            String viewCellRelativePath = cakeModule.toPhpFileName(Category.VIEW_CELL, cellName);
            FileObject viewCell = cakeModule.getFile(base, Category.VIEW_CELL, viewCellRelativePath, pluginName);
            if (viewCell != null) {
                // XXX offset : a method exists
                viewCells.add(GoToItemFactory.create(Category.VIEW_CELL, viewCell, DEFAULT_OFFSET));
            }

            // template
            String templateCellRelativePath = cellName + "/" + cakeModule.toPhpFileName(Category.TEMPLATE_CELL, cellMethod); // NOI18N
            FileObject templateCell = cakeModule.getFile(base, Category.TEMPLATE_CELL, templateCellRelativePath, pluginName);
            if (templateCell != null) {
                templateCells.add(GoToItemFactory.create(Category.TEMPLATE_CELL, templateCell, DEFAULT_OFFSET));
            }
        }

    }

    private FileObject getTemplate(String name, FileObject controller) {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(controller);
        String relativePath = cakeModule.toPhpFileName(Category.TEMPLATE, name);
        return cakeModule.getTemplate(relativePath, controller);
    }

    private void addHelpers(List<Pair<String, PhpClass>> helperClasses) {
        for (Pair<String, PhpClass> helper : helperClasses) {
            PhpClass phpClass = helper.second();
            FileObject file = phpClass.getFile();
            if (file == null) {
                continue;
            }
            helpers.add(GoToItemFactory.create(Category.HELPER, file, DEFAULT_OFFSET));
        }
    }

    @Override
    public List<GoToItem> getSmart() {
        if (isTemplateCell) {
            List<GoToItem> items = new ArrayList<>(viewCells);
            items.addAll(templates);
            items.addAll(extendz);
            items.addAll(elements);
            items.addAll(templateCells);
            items.addAll(helpers);
            return items;
        }
        List<GoToItem> items = new ArrayList<>(controllers);
        items.addAll(templates);
        items.addAll(extendz);
        items.addAll(elements);
        items.addAll(templateCells);
        items.addAll(viewCells);
        items.addAll(helpers);
        return items;
    }

    @Override
    public List<GoToItem> getTemplates() {
        List<GoToItem> items = new ArrayList<>(templates);
        items.addAll(extendz);
        items.addAll(elements);
        items.addAll(templateCells);
        return items;
    }

    @Override
    public List<GoToItem> getViewCells() {
        List<GoToItem> items = new ArrayList<>(viewCells);
        return items;
    }

    private void clear() {
        controllers.clear();
        helpers.clear();
        templates.clear();
        elements.clear();
        extendz.clear();
        templateCells.clear();
        viewCells.clear();
    }

}
