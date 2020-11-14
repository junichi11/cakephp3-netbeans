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
package org.netbeans.modules.php.cake3.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.netbeans.modules.php.cake3.dotcake.DotcakeSupport;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.utils.Inflector;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Pair;

public class CakePHP3ModuleDefault extends CakePHP3ModuleImpl {

    CakePHP3ModuleDefault(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public boolean isTemplateFile(FileObject fileObject) {
        String ext = fileObject.getExt();
        if (DEFAULT_CTP_EXT.equals(ext)) {
            return true;
        }
        
        if(DEFAULT_PHP_EXT.equals(ext)){
            String path = fileObject.getPath();
            if(path.contains("/templates/")){
                return true;
            }
        }
        
        Category category = getCategory(fileObject);
        if (!ModuleUtils.isTemplate(category)) {
            return false;
        }
        return getCtpExt().equals(ext); // NOI18N
    }

    @Override
    public FileObject getController(FileObject template) {
        return getControllerOrViewCell(template, true);
    }

    @Override
    public FileObject getController(FileObject template, boolean forceApp) {
        return getControllerOrViewCell(template, true, true);
    }

    @Override
    public FileObject getViewCell(FileObject template) {
        return getControllerOrViewCell(template, false);
    }

    private FileObject getControllerOrViewCell(FileObject template, boolean isController) {
        return getControllerOrViewCell(template, isController, false);
    }

    private FileObject getControllerOrViewCell(FileObject template, boolean isController, boolean forceApp) {
        if (template == null || template.isFolder()) {
            return null;
        }
        Category category = getCategory(template);
        if (isController) {
            if (category != Category.TEMPLATE) {
                return null;
            }
        } else {
            if (category != Category.TEMPLATE_CELL) {
                return null;
            }
        }

        Base base = getBase(template);
        // plugin
        String pluginName = ""; // NOI18N
        if (base == Base.PLUGIN) {
            pluginName = getPluginName(template);
        }

        List<FileObject> directories = getDirectories(base, category, pluginName);
        for (FileObject directory : directories) {
            String relativeFilePath = FileUtil.getRelativePath(directory, template);
            if (StringUtils.isEmpty(relativeFilePath)) {
                continue;
            }
            String relativeSubpath = relativeFilePath.replace(template.getNameExt(), ""); // NOI18N
            if (relativeSubpath.endsWith("/")) { // NOI18N
                relativeSubpath = relativeSubpath.substring(0, relativeSubpath.length() - 1);
            }
            Category c = category == Category.TEMPLATE ? Category.CONTROLLER : Category.VIEW_CELL;
            String controllerFilePath = toPhpFileName(c, relativeSubpath);
            if (forceApp) {
                base = Base.APP;
                pluginName = null;
            }
            FileObject controllerOrViewCell = getFile(base, c, controllerFilePath, pluginName);
            if (controllerOrViewCell != null) {
                return controllerOrViewCell;
            }
        }

        return null;
    }

    @Override
    public FileObject getTemplate(String relativePath, FileObject controllerOrViewCell, String themeName) {
        if (controllerOrViewCell.isFolder()) {
            return null;
        }
        boolean isTheme = !StringUtils.isEmpty(themeName);
        Base base;
        if (isTheme) {
            base = Base.PLUGIN;
        } else {
            base = getBase(controllerOrViewCell);
        }

        Category category = getCategory(controllerOrViewCell);
        if (category != Category.CONTROLLER && category != Category.VIEW_CELL) {
            return null;
        }

        String pluginName = null;
        if (base == Base.PLUGIN) {
            if (isTheme) {
                pluginName = themeName;
            } else {
                pluginName = getPluginName(controllerOrViewCell);
            }
        }

        String name = controllerOrViewCell.getName();
        StringBuilder sb = new StringBuilder();
        if (!relativePath.startsWith("/")) {
            String directoryName = ModuleUtils.toCommonName(name, category);
            sb.append(directoryName);
            sb.append("/"); // NOI18N
        }
        sb.append(relativePath);
        Category c = category == Category.CONTROLLER ? Category.TEMPLATE : Category.TEMPLATE_CELL;
        return getFile(base, c, sb.toString(), pluginName);

        // XXX fallback with only relative path?
    }

    @Override
    public FileObject getEntity(FileObject table) {
        String pluginName = null;
        CakePHP3Module.Base base = getBase(table);
        if (base == Base.PLUGIN) {
            pluginName = getPluginName(table);
        }
        String name = ModuleUtils.toCommonName(table.getName(), Category.TABLE);
        Inflector inflector = Inflector.getInstance();
        name = inflector.singularize(name);
        String relativePath = toPhpFileName(Category.ENTITY, name);
        return getFile(base, Category.ENTITY, relativePath, pluginName);
    }

    @Override
    public List<FileObject> getDirectories(CakePHP3Module.Base base) {
        switch (base) {
            case APP:
                FileObject rootDirectory = getRootDirectory();
                if (rootDirectory != null) {
                    return Collections.singletonList(rootDirectory);
                }
                break;
            case CORE:
                FileObject coreDirecotry = getCoreDirecotry();
                if (coreDirecotry != null) {
                    return Collections.singletonList(coreDirecotry);
                }
                break;
            case PLUGIN:
                return getPluignDirectories();
            case VENDOR:
                FileObject vendorDirecotry = getVendorDirecotry();
                if (vendorDirecotry != null) {
                    return Collections.singletonList(vendorDirecotry);
                }
                break;
            default:
                break;
        }
        return Collections.emptyList();
    }

    @CheckForNull
    private FileObject getCoreDirecotry() {
        // Dotcake support
        Dotcake dotcake = getDotcake();
        if (dotcake != null) {
            FileObject coreDirectory = DotcakeSupport.getCoreDirectory(dotcake);
            if (coreDirectory != null) {
                return coreDirectory;
            }
        }

        // default
        FileObject rootDirectory = getRootDirectory();
        if (rootDirectory == null) {
            return null;
        }
        return rootDirectory.getFileObject("vendor/cakephp/cakephp"); // NOI18N
    }

    @CheckForNull
    private FileObject getVendorDirecotry() {
        FileObject rootDirectory = getRootDirectory();
        if (rootDirectory == null) {
            return null;
        }
        return rootDirectory.getFileObject("vendor"); // NOI18N
    }

    private List<FileObject> getPluignDirectories() {
        // custom directories from .cake file
        List<FileObject> plugins = new ArrayList<>();
        Dotcake dotcake = getDotcake();
        if (dotcake != null) {
            plugins.addAll(DotcakeSupport.getPluignsDirectories(dotcake));
        }

        // default
        FileObject rootDirectory = getRootDirectory();
        if (rootDirectory == null) {
            return plugins;
        }
        FileObject defaultPlugins = rootDirectory.getFileObject("plugins"); // NOI18N
        if (defaultPlugins != null) {
            if (!plugins.contains(defaultPlugins)) {
                plugins.add(defaultPlugins);
            }
        }
        return plugins;
    }

    @Override
    public List<FileObject> getDirectories(Base base, Category category, String pluginName) {
        if (base == Base.PLUGIN) {
            if (StringUtils.isEmpty(pluginName)) {
                return getDirectories(base);
            }
            if (category == null) {
                List<FileObject> directories = getDirectories(base);
                for (FileObject directory : directories) {
                    FileObject plugin = directory.getFileObject(pluginName);
                    if (plugin != null) {
                        return Collections.singletonList(plugin);
                    }
                }
                for (Pair<String, FileObject> vendorPlugin : getVendorPlugins()) {
                    String pName = vendorPlugin.first();
                    if (pluginName.equals(pName)) {
                        return Collections.singletonList(vendorPlugin.second());
                    }
                }
                if (!StringUtils.isEmpty(pluginName)) {
                    return Collections.emptyList();
                }
            }
        }
        if (category == null) {
            return getDirectories(base);
        }
        switch (base) {
            case APP:
                return getAppDirectories(category);
            case PLUGIN:
                return getPluginDirectories(category, pluginName);
            case CORE:
                return getCoreDirectories(category);
            case VENDOR:
                FileObject vendorDirecotry = getVendorDirecotry();
                if (vendorDirecotry != null) {
                    return Collections.singletonList(vendorDirecotry);
                } else {
                    return Collections.emptyList();
                }
            default:
                return Collections.emptyList();
        }
    }

    private List<FileObject> getAppDirectories(Category category) {
        List<FileObject> targets = new ArrayList<>();

        // Dotcake
        Dotcake dotcake = getDotcake();
        if (dotcake != null) {
            List<FileObject> directories = DotcakeSupport.getDirectories(dotcake, category);
            targets.addAll(directories);
        }

        // default
        List<FileObject> appDirectories = getDirectories(Base.APP);
        for (FileObject directory : appDirectories) {
            FileObject target = getDirectory(directory, Base.APP, category);
            if (target != null) {
                if (!targets.contains(target)) {
                    targets.add(target);
                }
            }
        }

        return targets;
    }

    private List<FileObject> getPluginDirectories(Category category, String name) {
        List<FileObject> directories = getDirectories(Base.PLUGIN);
        List<FileObject> targets = new ArrayList<>();
        for (FileObject directory : directories) {
            FileObject baseDirectory = directory.getFileObject(name);
            if (baseDirectory == null) {
                continue;
            }
            FileObject target = getDirectory(baseDirectory, Base.PLUGIN, category);
            if (target != null) {
                targets.add(target);
            }
        }

        // vendor plugins
        List<Pair<String, FileObject>> vendorPlugins = getVendorPlugins();
        for (Pair<String, FileObject> vendorPlugin : vendorPlugins) {
            String pluginName = vendorPlugin.first();
            if (pluginName.equals(name)) {
                FileObject baseDirectory = vendorPlugin.second();
                FileObject target = getDirectory(baseDirectory, Base.PLUGIN, category);
                if (target != null && !targets.contains(target)) {
                    targets.add(target);
                    break;
                }
            }
        }

        // TODO Dotcake?
        return targets;
    }

    private List<FileObject> getCoreDirectories(Category category) {
        FileObject coreDirecotry = getCoreDirecotry();
        if (coreDirecotry == null) {
            return Collections.emptyList();
        }
        FileObject directory = getDirectory(coreDirecotry, Base.CORE, category);
        if (directory != null) {
            return Collections.singletonList(directory);
        }
        return Collections.emptyList();
    }

    private FileObject getDirectory(FileObject baseDirectory, Base base, Category category) {
        if (baseDirectory == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        switch (category) {
            case BEHAVIOR:
                sb.append(getSrcDirName(base));
                if (base == Base.CORE) {
                    sb.append("/ORM/Behavior"); // NOI18N
                } else {
                    sb.append("/Model/Behavior"); // NOI18N
                }
                break;
            case CONFIG:
                sb.append("config"); // NOI18N
                break;
            case CONSOLE:
                sb.append(getSrcDirName(base));
                sb.append("/Console"); // NOI18N
                break;
            case CONTROLLER:
                sb.append(getSrcDirName(base));
                sb.append("/Controller"); // NOI18N
                break;
            case COMPONENT:
                sb.append(getSrcDirName(base));
                sb.append("/Controller/Component"); // NOI18N
                break;
            case CSS:
                sb.append(getWWWRootPath(base));
                sb.append("/");
                sb.append(getCssPath(base));
                break;
            case DIR:
                sb.append(getSrcDirName(base));
                break;
            case ELEMENT:
                sb.append(getSrcDirName(base));
                sb.append("/Template/Element"); // NOI18N
                break;
            case EMAIL:
                sb.append(getSrcDirName(base));
                sb.append("/Template/Email"); // NOI18N
                break;
            case ENTITY:
                sb.append(getSrcDirName(base));
                sb.append("/Model/Entity"); // NOI18N
                break;
            case ERROR:
                sb.append(getSrcDirName(base));
                sb.append("/Template/Error"); // NOI18N
                break;
            case FIXTURE:
                sb.append("tests/Fixture"); // NOI18N
                break;
            case FORM:
                sb.append(getSrcDirName(base));
                sb.append("/Form"); // NOI18N
                break;
            case HELPER:
                sb.append(getSrcDirName(base));
                sb.append("/View/Helper"); // NOI18N
                break;
            case IMG:
                sb.append(getWWWRootPath(base));
                sb.append("/"); // NOI18N
                sb.append(getImagePath(base));
                break;
            case JS:
                sb.append(getWWWRootPath(base));
                sb.append("/"); // NOI18N
                sb.append(getJsPath(base));
                break;
            case LAYOUT:
                sb.append(getSrcDirName(base));
                sb.append("/Template/Layout"); // NOI18N
                break;
            case LOCALE:
                sb.append(getSrcDirName(base));
                sb.append("/Locale"); // NOI18N
                break;
            case MODEL:
                sb.append(getSrcDirName(base));
                sb.append("/Model"); // NOI18N
                break;
            case PAGES:
                sb.append(getSrcDirName(base));
                sb.append("/Template/Pages"); // NOI18N
                break;
            case SHELL:
                sb.append(getSrcDirName(base));
                sb.append("/Shell"); // NOI18N
                break;
            case TABLE:
                sb.append(getSrcDirName(base));
                sb.append("/Model/Table"); // NOI18N
                break;
            case TASK:
                sb.append(getSrcDirName(base));
                sb.append("/Shell/Task"); // NOI18N
                break;
            case TEMPLATE:
                sb.append(getSrcDirName(base));
                sb.append("/Template"); // NOI18N
                break;
            case TEMPLATE_CELL:
                sb.append(getSrcDirName(base));
                sb.append("/Template/Cell"); // NOI18N
                break;
            case TEST:
                sb.append("tests"); // NOI18N
                break;
            case TEST_CASE:
                sb.append("tests/TestCase"); // NOI18N
                break;
            case VIEW:
                sb.append(getSrcDirName(base));
                sb.append("/View"); // NOI18N
                break;
            case VIEW_CELL:
                sb.append(getSrcDirName(base));
                sb.append("/View/Cell"); // NOI18N
                break;
            case WEBROOT:
                sb.append(getWWWRootPath(base));
                break;
            default:
                throw new AssertionError();
        }
        String relativePath = sb.toString();
        return baseDirectory.getFileObject(relativePath);
    }

    private String getSrcDirName(Base base) {
        switch (base) {
            case APP:
                return getSrcDirName();
            default:
                return DEFAULT_SRC_DIR_NAME;
        }
    }

    private String getWWWRootPath(Base base) {
        switch (base) {
            case APP:
                return getAppWWWRoot();
            default:
                return DEFAULT_WWW_ROOT;
        }
    }

    private String getCssPath(Base base) {
        switch (base) {
            case APP:
                return getAppCssBaseUrl();
            default:
                return DEFAULT_CSS_BASE_URL;
        }
    }

    private String getJsPath(Base base) {
        switch (base) {
            case APP:
                return getAppJsBaseUrl();
            default:
                return DEFAULT_JS_BASE_URL;
        }
    }

    private String getImagePath(Base base) {
        switch (base) {
            case APP:
                return getAppImageBaseUrl();
            default:
                return DEFAULT_IMAGE_BASE_URL;
        }
    }

}
