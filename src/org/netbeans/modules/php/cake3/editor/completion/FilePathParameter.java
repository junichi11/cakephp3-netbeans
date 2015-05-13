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
package org.netbeans.modules.php.cake3.editor.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.modules.ModuleInfo;
import org.netbeans.modules.php.cake3.modules.ModuleUtils;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;

public class FilePathParameter extends Parameter {

    FilePathParameter(int position, String className, String methodName, FileObject fileObject) {
        super(position, className, methodName, fileObject);
    }

    @Override
    public List<CompletionItem> getCompletionItems(String filter, int caretOffset) {
        Category category = getCategory(getPosition(), getClassName(), getMethodName());
        if (category == Category.UNKNOWN) {
            return Collections.emptyList();
        }
        FileObject currentFile = getFileObject();
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(currentFile);
        ModuleInfo info = cakeModule.createModuleInfo(currentFile);

        List<String> elements = new ArrayList<>();

        // with extension e.g. cake.css (this isn't plugin)
        String pluginName = ""; // NOI18N
        int indexOfDot = filter.indexOf("."); // NOI18N

        // add plugin names
        if (indexOfDot == -1) {
            Set<String> allPluginNames = cakeModule.getAllPluginNames();
            for (String name : allPluginNames) {
                if (name.startsWith(filter)) {
                    elements.add(name.concat(".")); // NOI18N
                }
            }
        }

        // exists plugin?
        if (indexOfDot > 0) {
            String tmpPluginName = filter.substring(0, indexOfDot);
            List<FileObject> plugins = cakeModule.getDirectories(Base.PLUGIN, null, tmpPluginName);
            if (!plugins.isEmpty()) {
                pluginName = tmpPluginName;
                filter = filter.substring(indexOfDot + 1);
            }
        }

        // has subpath?
        int lastIndexOfSlash = filter.lastIndexOf("/"); // NOI18N
        String subpath = ""; // NOI18N
        if (lastIndexOfSlash >= 0) {
            if (lastIndexOfSlash == 0) {
                subpath = "/"; // NOI18N
            } else {
                subpath = filter.substring(0, lastIndexOfSlash);
            }
            filter = filter.substring(lastIndexOfSlash + 1);
        }

        elements.addAll(getElements(category, cakeModule, info, subpath, filter, pluginName));
        if (!elements.isEmpty()) {
            return getCompletionItems(elements, filter, caretOffset);
        }
        return Collections.emptyList();
    }

    private List<String> getElements(Category category, CakePHP3Module cakeModule, ModuleInfo info, String subpath, String filter, String pluginName) {
        switch (category) {
            case JS: // fallthrough
            case CSS:
            case IMG:
                return getAssets(cakeModule, info, subpath, filter, pluginName, category);
            case ELEMENT: // fallthrough
            case LAYOUT:
            case TEMPLATE:
                return getTemplates(cakeModule, info, subpath, filter, pluginName, category);
            default:
                break;
        }

        return Collections.emptyList();
    }

    private static Category getCategory(int position, String className, String methodName) {
        if (position < 0 || StringUtils.isEmpty(methodName)) {
            return Category.UNKNOWN;
        }
        List<String> elements = getElements(position, className, methodName);
        for (String element : elements) {
            element = element.toUpperCase();
            Category category = Category.valueOf(element);
            if (category != null) {
                return category;
            }
        }
        return Category.UNKNOWN;
    }

    private List<String> getAssets(CakePHP3Module cakeModule, ModuleInfo info, String subpath, String filter, String pluginName, Category category) {
        Base base = info.getBase();
        if (StringUtils.isEmpty(pluginName)) {
            if (base == Base.PLUGIN) {
                pluginName = info.getPluginName();
            }
        } else {
            if (base != Base.PLUGIN) {
                base = Base.PLUGIN;
            }
        }
        if (startsWithSlash(subpath)) {
            category = Category.WEBROOT;
        }
        List<FileObject> directories = cakeModule.getDirectories(base, category, pluginName);
        List<String> elements = new ArrayList<>();
        for (FileObject directory : directories) {
            FileObject baseDirectory = directory.getFileObject(subpath);
            if (baseDirectory != null) {
                for (FileObject child : baseDirectory.getChildren()) {
                    String fullName = child.getNameExt();
                    if (fullName.startsWith(filter)) {
                        if (child.isFolder()) {
                            fullName = fullName.concat("/"); // NOI18N
                        }
                        elements.add(fullName);
                    }
                }
            }
        }
        return elements;
    }

    private List<String> getTemplates(CakePHP3Module cakeModule, ModuleInfo info, String subpath, String filter, String pluginName, Category category) {
        Base base = info.getBase();
        if (StringUtils.isEmpty(pluginName)) {
            if (base == Base.PLUGIN) {
                pluginName = info.getPluginName();
            }
        } else {
            if (base != Base.PLUGIN) {
                base = Base.PLUGIN;
            }
        }

        // e.g. $this->render('/Common/index');
        if (startsWithSlash(subpath)) {
            category = Category.TEMPLATE;
        } else {
            if (getMethodName().equals("extend") && getClassName().equals("View")) { // NOI18N
                category = info.getCategory();
            }

            if (category == Category.TEMPLATE) {
                // controller id
                if (info.getCategory() == Category.CONTROLLER) {
                    FileObject controller = getFileObject();
                    if (controller != null) {
                        String controllerId = ModuleUtils.toCommonName(controller.getName(), Category.CONTROLLER);
                        subpath = controllerId + "/" + subpath; // NOI18N
                    }
                }

                // extend method
                if (info.getCategory() == Category.TEMPLATE) {
                    List<FileObject> directories = cakeModule.getDirectories(base, category, pluginName);
                    boolean found = false;
                    for (FileObject directory : directories) {
                        FileObject[] children = directory.getChildren();
                        for (FileObject child : children) {
                            if (!child.isFolder()) {
                                continue;
                            }
                            if (ModuleUtils.isChild(child, getFileObject())) {
                                subpath = child.getName() + "/" + subpath; // NOI18N
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                }
            }
        }

        List<FileObject> directories = cakeModule.getDirectories(base, category, pluginName);
        List<String> elements = new ArrayList<>();
        for (FileObject directory : directories) {
            FileObject baseDirectory = directory.getFileObject(subpath);
            if (baseDirectory != null) {
                for (FileObject child : baseDirectory.getChildren()) {
                    String name = child.getName();
                    if (name.startsWith(filter)) {
                        if (child.isFolder()) {
                            name = name.concat("/"); // NOI18N
                        }
                        elements.add(name);
                    }
                }
            }
        }
        return elements;
    }

    private List<CompletionItem> getCompletionItems(List<String> elements, String filter, int startOffset) {
        List<CompletionItem> items = new ArrayList<>();
        for (String element : elements) {
            items.add(new CakePHP3CompletionItem(element, filter, startOffset));
        }
        return items;
    }

    private static boolean startsWithSlash(String text) {
        if (text == null) {
            return false;
        }
        return text.startsWith("/"); // NOI18N
    }

}
