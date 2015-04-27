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
package org.netbeans.modules.php.cake3.modules;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.netbeans.modules.php.cake3.utils.CakePHPCodeUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.InfixExpression;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public abstract class CakePHP3ModuleImpl {

    private static final Logger LOGGER = Logger.getLogger(CakePHP3ModuleImpl.class.getName());
    protected static final String DEFAULT_CTP_EXT = "ctp"; // NOI18N
    protected static final String DEFAULT_NAMESPACE = "App"; // NOI18N
    protected static final String DEFAULT_WEBROOT = "webroot"; // NOI18N
    protected static final String DEFAULT_WWW_ROOT = "webroot"; // NOI18N
    protected static final String DEFAULT_IMAGE_BASE_URL = "img"; // NOI18N
    protected static final String DEFAULT_CSS_BASE_URL = "css"; // NOI18N
    protected static final String DEFAULT_JS_BASE_URL = "js"; // NOI18N
    protected static final String DEFAULT_SRC_DIR_NAME = "src"; // NOI18N
    // !!!Don't change the order of list!!!
    private static final List<Category> CATEGORIES = Arrays.asList(
            Category.CONFIG,
            Category.COMPONENT, Category.CONTROLLER,
            Category.ENTITY, Category.TABLE, Category.BEHAVIOR, Category.MODEL,
            Category.HELPER,
            Category.VIEW_CELL, Category.VIEW,
            Category.ELEMENT, Category.EMAIL, Category.ERROR, Category.LAYOUT, Category.PAGES,
            Category.TEMPLATE_CELL, Category.TEMPLATE,
            Category.FIXTURE, Category.TEST_CASE, Category.TEST,
            Category.TASK, Category.SHELL,
            Category.CONSOLE, Category.LOCALE, Category.DIR,
            Category.CSS, Category.IMG, Category.JS, Category.WEBROOT
    );
    private final PhpModule phpModule;

    CakePHP3ModuleImpl(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    /**
     * Get an extension of a template file for CakePHP.
     *
     * @return an extension of template, default is ctp
     */
    public String getCtpExt() {
        if (phpModule != null) {
            String ctpExt = CakePHP3Preferences.getCtpExt(phpModule);
            if (!StringUtils.isEmpty(ctpExt)) {
                return ctpExt;
            }
        }
        return DEFAULT_CTP_EXT;
    }

    /**
     * Get a namespace for an app.
     *
     * @return a namespace, default namespace is App
     */
    public String getAppNamespace() {
        if (phpModule == null) {
            return DEFAULT_NAMESPACE;
        }
        String namespace = CakePHP3Preferences.getNamespace(phpModule);
        if (!StringUtils.isEmpty(namespace)) {
            return namespace;
        }
        return DEFAULT_NAMESPACE;
    }

    /**
     * Get a category for a file.
     *
     * @param fileObject a file
     * @return Category
     */
    public Category getCategory(FileObject fileObject) {
        if (fileObject == null) {
            return Category.UNKNOWN;
        }

        Base base = getBase(fileObject);
        if (base == Base.UNKNOWN || base == Base.VENDOR) {
            return Category.UNKNOWN;
        }

        String path = fileObject.getPath();
        // plugin
        String pluginName = ""; // NOI18N
        if (base == Base.PLUGIN) {
            pluginName = getPluginName(fileObject);
        }
        for (Category category : CATEGORIES) {
            Category c = getCategory(path, base, category, pluginName);
            if (c != Category.UNKNOWN) {
                return c;
            }
        }

        return Category.UNKNOWN;
    }

    /**
     * Get a category for a file.
     *
     * @param path a path
     * @param base Base
     * @param category Category
     * @param pluginName a plugin name
     * @return Category
     */
    private Category getCategory(String path, Base base, Category category, String pluginName) {
        List<FileObject> directories = getDirectories(base, category, pluginName);
        for (FileObject directory : directories) {
            String categoryPath = directory.getPath();
            if (path.startsWith(categoryPath + "/")) { // NOI18N
                return category;
            }
        }
        return Category.UNKNOWN;
    }

    /**
     * Get a Base for a file.
     *
     * @param fileObject a file
     * @return a Base
     */
    public Base getBase(FileObject fileObject) {
        if (fileObject == null) {
            return Base.UNKNOWN;
        }

        String path = fileObject.getPath();
        for (Base base : Arrays.asList(Base.CORE, Base.VENDOR, Base.PLUGIN, Base.APP)) {
            List<FileObject> directories = getDirectories(base);
            for (FileObject directory : directories) {
                if (ModuleUtils.isChild(directory, path)) {
                    if (base != Base.VENDOR) {
                        return base;
                    }

                    // vendor plugin
                    List<Pair<String, FileObject>> vendorPlugins = getVendorPlugins();
                    for (Pair<String, FileObject> vendorPlugin : vendorPlugins) {
                        FileObject pluginDirectory = vendorPlugin.second();
                        if (ModuleUtils.isChild(pluginDirectory, path)) {
                            return Base.PLUGIN;
                        }
                    }

                    return base;
                }
            }
        }
        return Base.UNKNOWN;
    }

    /**
     * Get the root directory for CakePHP.
     *
     * @return the root directory for CakePHP if it exists, otherwise
     * {@code null}
     */
    @CheckForNull
    public FileObject getRootDirectory() {
        if (phpModule == null) {
            return null;
        }
        String rootPath = CakePHP3Preferences.getRootPath(phpModule);
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory != null) {
            return sourceDirectory.getFileObject(rootPath);
        }
        return null;
    }

    protected String getAppWebroot() {
        return getAppWWWRoot();
    }

    protected String getAppWWWRoot() {
        if (phpModule == null) {
            return ""; // NOI18N
        }
        String wwwRootPath = CakePHP3Preferences.getWWWRootPath(phpModule);
        if (StringUtils.isEmpty(wwwRootPath)) {
            wwwRootPath = DEFAULT_WWW_ROOT;
        }
        return wwwRootPath;
    }

    protected String getAppImageBaseUrl() {
        if (phpModule == null) {
            return ""; // NOI18N
        }
        String imageUrl = CakePHP3Preferences.getImageUrl(phpModule);
        if (StringUtils.isEmpty(imageUrl)) {
            imageUrl = DEFAULT_IMAGE_BASE_URL;
        }
        return imageUrl;
    }

    protected String getAppCssBaseUrl() {
        if (phpModule == null) {
            return ""; // NOI18N
        }
        String cssUrl = CakePHP3Preferences.getCssUrl(phpModule);
        if (StringUtils.isEmpty(cssUrl)) {
            cssUrl = DEFAULT_CSS_BASE_URL;
        }
        return cssUrl;
    }

    protected String getAppJsBaseUrl() {
        if (phpModule == null) {
            return ""; // NOI18N
        }
        String jsUrl = CakePHP3Preferences.getJsUrl(phpModule);
        if (StringUtils.isEmpty(jsUrl)) {
            jsUrl = DEFAULT_JS_BASE_URL;
        }
        return jsUrl;
    }

    protected String getSrcDirName() {
        if (phpModule == null) {
            return ""; // NOI18N
        }
        String srcName = CakePHP3Preferences.getSrcName(phpModule);
        if (StringUtils.isEmpty(srcName)) {
            srcName = DEFAULT_SRC_DIR_NAME;
        }
        return srcName;
    }

    /**
     * Get vendor plugins.
     *
     * @return plugins
     */
    protected List<Pair<String, FileObject>> getVendorPlugins() {
        // XXX use cache?
        List<FileObject> directories = getDirectories(Base.VENDOR);
        List<Pair<String, FileObject>> vendorPlugins = new ArrayList<>();
        for (FileObject directory : directories) {
            FileObject cakephpPlugins = directory.getFileObject("cakephp-plugins.php"); // NOI18N
            if (cakephpPlugins == null) {
                continue;
            }
            final PluginsVisitor pluginsVisitor = new PluginsVisitor();
            scan(cakephpPlugins, pluginsVisitor);
            List<Pair<String, String>> plugins = pluginsVisitor.getPlugins();
            FileObject rootDirectory = getRootDirectory();
            if (rootDirectory != null) {
                for (Pair<String, String> plugin : plugins) {
                    String path = plugin.second();
                    FileObject pluginDirectory = rootDirectory.getFileObject(path);
                    if (pluginDirectory == null) {
                        continue;
                    }
                    vendorPlugins.add(Pair.of(plugin.first(), pluginDirectory));
                }
            }
        }
        return vendorPlugins;
    }

    private void scan(FileObject cakephpPlugins, final PluginsVisitor visitor) {
        try {
            ParserManager.parse(Collections.singleton(Source.create(cakephpPlugins)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult result = (ParserResult) resultIterator.getParserResult();
                    if (result == null) {
                        return;
                    }
                    visitor.scan(Utils.getRoot(result));
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    /**
     * Get all plugin names. It also contains vendor plugins.
     *
     * @return plugin names
     */
    public Set<String> getAllPluginNames() {
        Set<String> names = new HashSet<>();
        List<FileObject> directories = getDirectories(Base.PLUGIN);
        for (FileObject directory : directories) {
            FileObject[] children = directory.getChildren();
            for (FileObject child : children) {
                if (!child.isFolder()) {
                    continue;
                }
                names.add(child.getName());
            }
        }

        // vendor plugins
        List<Pair<String, FileObject>> vendorPlugins = getVendorPlugins();
        for (Pair<String, FileObject> vendorPlugin : vendorPlugins) {
            names.add(vendorPlugin.first());
        }
        return names;
    }

    /**
     * Get a file for a specific Base, Category, and Plugin.
     *
     * @param base Base
     * @param category Category
     * @param relativePath a relative path
     * @param pluginName a plugin name
     * @return a file if it exists, otherwise {@code null}
     */
    @CheckForNull
    public FileObject getFile(Base base, Category category, String relativePath, String pluginName) {
        List<FileObject> directories = getDirectories(base, category, pluginName);
        for (FileObject directory : directories) {
            FileObject target = directory.getFileObject(relativePath);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    /**
     * Get a plugin name for a file. Check whether the file is in plugin.
     *
     * @param target a file
     * @return a plugin name if a file is in pluign, otherwise empty string
     */
    public String getPluginName(FileObject target) {
        Base base = getBase(target);
        if (target == null || base != Base.PLUGIN) {
            return ""; // NOI18N
        }
        // plugins
        String targetPath = target.getPath();
        List<FileObject> directories = getDirectories(base);
        for (FileObject directory : directories) {
            for (FileObject children : directory.getChildren()) {
                if (!children.isFolder()) {
                    continue;
                }
                String path = children.getPath();
                if (targetPath.startsWith(path)) {
                    return children.getName();
                }
            }
        }

        // vendor plugins
        List<Pair<String, FileObject>> vendorPlugins = getVendorPlugins();
        for (Pair<String, FileObject> vendorPlugin : vendorPlugins) {
            FileObject parent = vendorPlugin.second();
            if (ModuleUtils.isChild(parent, target)) {
                return vendorPlugin.first();
            }
        }
        return ""; // NOI18N
    }

    /**
     * Convert a common name to a PHP file name. e.g. Pages ->
     * PagesController.php. If the category is template, append your ctp ext to
     * the tail.
     *
     * @param category Category
     * @param name a common name
     * @return a php file name
     */
    public String toPhpFileName(Category category, String name) {
        switch (category) {
            case CONTROLLER:
                return name + "Controller.php"; // NOI18N
            case COMPONENT:
                return name + "Component.php"; // NOI18N
            case HELPER:
                return name + "Helper.php"; // NOI18N
            case TABLE:
                return name + "Table.php"; // NOI18N
            case BEHAVIOR:
                return name + "Behavior.php"; // NOI18N
            case VIEW_CELL:
                return name + "Cell.php"; // NOI18N
            case ELEMENT: // fallthrough
            case EMAIL:
            case ERROR:
            case LAYOUT:
            case PAGES:
            case TEMPLATE:
            case TEMPLATE_CELL:
                return name + "." + getCtpExt(); // NOI18N
            default:
                return name + ".php"; // NOI18N
        }
    }

    /**
     * Get a namespace for a directory or file.
     *
     * @param fileObject a directroy or file
     * @return a namespace if it exists, otherwise empty string
     */
    public String getNamespace(FileObject fileObject) {
        if (fileObject == null) {
            return ""; // NOI18N
        }
        Base base = getBase(fileObject);
        StringBuilder sb = new StringBuilder();
        String pluginName = null;
        if (base == Base.APP) {
            sb.append(getAppNamespace()).append("\\"); // NOI18N
        } else if (base == Base.CORE) {
            sb.append("Cake\\"); // NOI18N
        } else if (base == Base.PLUGIN) {
            pluginName = getPluginName(fileObject);
            if (StringUtils.isEmpty(pluginName)) {
                return sb.toString();
            }
            sb.append(pluginName).append("\\"); // NOI18N
        } else {
            return sb.toString();
        }

        // XXX not default directory structure
        FileObject srcDir = getSrcDir(base, pluginName);
        if (srcDir == null) {
            return ""; // NOI18N
        }
        FileObject target = fileObject;
        if (!fileObject.isFolder()) {
            target = fileObject.getParent();
            if (target == null) {
                return ""; // NOI18N
            }
        }

        String relativePath = FileUtil.getRelativePath(srcDir, target);
        if (relativePath == null) {
            return "";
        }
        sb.append(relativePath.replace("/", "\\")); // NOI18N
        return sb.toString();
    }

    private FileObject getSrcDir(Base base, String pluginName) {
        List<FileObject> directories = getDirectories(base, null, pluginName);
        String srcDirName = getSrcDirName();
        if (base == Base.CORE || base == Base.PLUGIN) {
            srcDirName = DEFAULT_SRC_DIR_NAME;
        }
        for (FileObject directory : directories) {
            FileObject srcDir = directory.getFileObject(srcDirName);
            if (srcDir != null) {
                return srcDir;
            }
        }
        return null;
    }

    public ModuleInfo createModuleInfo(FileObject fileObject) {
        Base base = getBase(fileObject);
        Category category = getCategory(fileObject);
        String pluginName = null;
        if (base == Base.PLUGIN) {
            pluginName = getPluginName(fileObject);
        }
        return new ModuleInfoImpl(fileObject, base, category, pluginName);
    }

    /**
     * Check whether a file is template one.
     *
     * @param fileObject
     * @return {@code true} if a file is template, otherwise {@code false}
     */
    public abstract boolean isTemplateFile(FileObject fileObject);

    /**
     * Get directories for {@link Base}
     *
     * @param base Base
     * @return directories for {@link Base}
     */
    public abstract List<FileObject> getDirectories(Base base);

    /**
     * Get directories for a specific category, base and plugin.
     *
     * @param base Base
     * @param category Category
     * @param pluginName a plugin name
     * @return directories
     */
    public abstract List<FileObject> getDirectories(Base base, Category category, String pluginName);

    /**
     * Get a controller for a template file.
     *
     * @param template a template file
     * @return a file if the controller exists, otherwise {@code null}
     */
    @CheckForNull
    public abstract FileObject getController(FileObject template);

    /**
     * Get a view cell for a template file.
     *
     * @param template a template file
     * @return a file if the view cell exists, otherwise {@code null}
     */
    @CheckForNull
    public abstract FileObject getViewCell(FileObject template);

    /**
     * Get a template file for a controller.
     *
     * @param relativePath a relative path for a template file from a controller
     * directory.
     * @param controller a controller file
     * @return a template file if it exists, otherwise {@code null}
     */
    @CheckForNull
    public abstract FileObject getTemplate(String relativePath, FileObject controller);

    /**
     * Get an entity file for a table.
     *
     * @param table a table file
     * @return a entity file if it exists, otherwise {@code null}
     */
    @CheckForNull
    public abstract FileObject getEntity(FileObject table);

    //~ inner class
    private static class PluginsVisitor extends DefaultVisitor {

        private final List<Pair<String, String>> plugins = Collections.synchronizedList(new ArrayList<Pair<String, String>>());

        @Override
        public void visit(ArrayCreation node) {
            List<ArrayElement> elements = node.getElements();
            for (ArrayElement element : elements) {
                Expression key = element.getKey();
                if (key instanceof Scalar) {
                    String pluginsKey = CakePHPCodeUtils.getStringValue(key);
                    if (!"plugins".equals(pluginsKey)) { // NOI18N
                        continue;
                    }

                    String pluginName;
                    String pluginPath = ""; // NOI18N
                    Expression value = element.getValue();
                    if (!(value instanceof ArrayCreation)) {
                        continue;
                    }
                    ArrayCreation ac = (ArrayCreation) value;
                    List<ArrayElement> pluginElements = ac.getElements();
                    for (ArrayElement pluginElement : pluginElements) {
                        Expression pluginKey = pluginElement.getKey();
                        if (pluginKey instanceof Scalar) {
                            pluginName = CakePHPCodeUtils.getStringValue(pluginKey);
                            if (StringUtils.isEmpty(pluginName)) {
                                continue;
                            }
                            Expression pluginValue = pluginElement.getValue();
                            if (pluginValue instanceof Scalar) {
                                pluginPath = CakePHPCodeUtils.getStringValue(pluginValue);
                            } else if (pluginValue instanceof InfixExpression) {
                                InfixExpression ie = (InfixExpression) pluginValue;
                                Expression right = ie.getRight();
                                if (right instanceof Scalar) {
                                    pluginPath = CakePHPCodeUtils.getStringValue(right);
                                }
                            }
                            if (!StringUtils.isEmpty(pluginName) && !StringUtils.isEmpty(pluginPath)) {
                                plugins.add(Pair.of(pluginName, pluginPath));
                            }
                        }
                    }
                }
            }
            super.visit(node);
        }

        public List<Pair<String, String>> getPlugins() {
            return plugins;
        }
    }

    private static class ModuleInfoImpl implements ModuleInfo {

        private final FileObject fileObject;
        private final String pluginName;
        private final Base base;
        private final Category category;

        private ModuleInfoImpl(FileObject fileObject, Base base, Category category, String pluginName) {
            this.fileObject = fileObject;
            this.base = base;
            this.category = category;
            this.pluginName = pluginName;
        }

        @Override
        public FileObject getFileObject() {
            return fileObject;
        }

        @Override
        public Base getBase() {
            if (base == null) {
                return Base.UNKNOWN;
            }
            return base;
        }

        @Override
        public Category getCategory() {
            if (category == null) {
                return Category.UNKNOWN;
            }
            return category;
        }

        @Override
        public String getPluginName() {
            return pluginName;
        }

    }

}
