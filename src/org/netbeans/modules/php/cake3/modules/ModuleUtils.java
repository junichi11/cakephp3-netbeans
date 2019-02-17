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

import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.openide.filesystems.FileObject;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public final class ModuleUtils {

    private ModuleUtils() {
    }

    /**
     * Check whether a category is Template.
     *
     * @param category category
     * @return {@code true} if a category is TEMPLATE, TEMPLATE_CELL, ELEMENT,
     * EMAIL, LAYOUT, PAGES, otherwise {@code false}
     */
    public static boolean isTemplate(Category category) {
        return category == CakePHP3Module.Category.TEMPLATE
                || category == CakePHP3Module.Category.TEMPLATE_CELL
                || category == CakePHP3Module.Category.ELEMENT
                || category == CakePHP3Module.Category.ERROR
                || category == CakePHP3Module.Category.LAYOUT
                || category == CakePHP3Module.Category.EMAIL
                || category == CakePHP3Module.Category.PAGES;
    }

    /**
     * Convert to a common name to a class name for a category. e.g. Some ->
     * SomeComponent
     *
     * @param commonName a common name
     * @param category a category
     * @return a class name if a class name exists, otherwise the common name
     */
    public static String toClassName(String commonName, Category category) {
        if (StringUtils.isEmpty(commonName)) {
            return ""; // NOI18N
        }
        String suffix;
        switch (category) {
            case BEHAVIOR: // fallthrough
            case COMPONENT:
            case CONTROLLER:
            case FIXTURE:
            case HELPER:
            case SHELL:
            case TABLE:
            case TASK:
            case TEST_CASE:
            case VIEW:
            case VIEW_CELL:
                suffix = category.getSuffix();
                break;
            default:
                return commonName;
        }
        return commonName.concat(suffix);
    }

    /**
     * Convert a class name to a common name. e.g. SomeComponent -> Some.
     *
     * @param className a class name
     * @param category a category
     * @return a common name
     */
    public static String toCommonName(String className, Category category) {
        switch (category) {
            case BEHAVIOR: // fallthrough
            case COMPONENT:
            case CONTROLLER:
            case FIXTURE:
            case HELPER:
            case SHELL:
            case TABLE:
            case TASK:
            case TEST_CASE:
            case VIEW:
            case VIEW_CELL:
                String regex = category.getSuffix().concat("$"); // NOI18N
                return className.replaceAll(regex, ""); // NOI18N
            default:
                return className;
        }
    }

    /**
     * Check whether a file is a child of specified directory.
     *
     * @param parent a directory
     * @param child a file or directory
     * @return {@code true} if the file or directory is a child of the specified
     * directory, otherwise {@code false}
     */
    public static boolean isChild(FileObject parent, FileObject child) {
        if (parent == null || !parent.isFolder() || child == null) {
            return false;
        }
        String childPath = child.getPath();
        return isChild(parent, childPath);
    }

    /**
     * Check whether a path is a child of specified directory.
     *
     * @param parent a directory
     * @param childPath a file path of FileObject
     * @return {@code true} if the path is a child the directory, otherwise
     * {@code false}
     */
    public static boolean isChild(FileObject parent, String childPath) {
        if (parent == null || !parent.isFolder() || StringUtils.isEmpty(childPath)) {
            return false;
        }
        String parentPath = parent.getPath();
        return childPath.startsWith(parentPath);
    }

    /**
     * Append a specified plugin name to a base name.
     *
     * @param pluginName a plugin name e.g. DebugKit
     * @param baseName a base name e.g. Toolbar
     * @return PluginName.basename e.g. DebugKit.Toolbar
     */
    public static String appendPluignName(String pluginName, String baseName) {
        if (StringUtils.isEmpty(baseName)) {
            return baseName;
        }
        return StringUtils.isEmpty(pluginName) ? baseName : pluginName + "." + baseName; // NOI18N
    }

    /**
     * Split to a plugin name and a file name by dot.
     *
     * @param name
     * @return Pair of the plugin name and the others. If a plugin name doesn't
     * exit, the first value is empty string. If a name is empty or null, both
     * the first and second values are empty string.
     */
    public static Pair<String, String> pluginSplit(String name) {
        return split(name, ".", false); // NOI18N
    }

    /**
     * Split to a class name and a method name by double colons.
     *
     * @param name
     * @return Pair of the class name and the others. If a method name doesn't
     * exit, the second value is empty string. If a name is empty or null, both
     * the first and second values are empty string.
     */
    public static Pair<String, String> cellMethodSplit(String name) {
        return split(name, "::", true); // NOI18N
    }

    /**
     * Split to two values by specified text.
     *
     * @param name target string
     * @param split string
     * @param first whether the name be set to the first value if name can't be
     * split
     * @return Pair of two strings. If a class name doesn't exit, the first
     * value is empty string. If a name is empty or null, both the first and
     * second values are empty string.
     */
    private static Pair<String, String> split(String name, String split, boolean first) {
        if (name == null) {
            return Pair.of("", ""); // NOI18N
        }
        if (split != null) {
            int indexOf = name.indexOf(split);
            if (indexOf != -1) {
                return Pair.of(name.substring(0, indexOf), name.substring(indexOf + split.length()));
            }
        }
        if (first) {
            return Pair.of(name, ""); // NOI18N
        }
        return Pair.of("", name); // NOI18N
    }

}
