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
package org.netbeans.modules.php.cake3.dotcake;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.dotcake.Dotcake.BuildPathCategory;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public final class DotcakeSupport {

    private DotcakeSupport() {
    }

    /**
     * Get CakePHP core directory.
     *
     * @param dotcake Dotcake
     * @return core directory
     */
    public static FileObject getCoreDirectory(Dotcake dotcake) {
        if (dotcake == null) {
            return null;
        }

        File dotcakeFile = dotcake.getDotcakeFile();
        if (dotcakeFile == null) {
            return null;
        }

        FileObject dotcakeFileObject = FileUtil.toFileObject(dotcakeFile);
        String cakePath = dotcake.getCake();
        if (StringUtils.isEmpty(cakePath)) {
            return null;
        }
        return dotcakeFileObject.getParent().getFileObject(cakePath);
    }

    /**
     * Get plugins directories.
     *
     * @param dotcake Dotcake
     * @return pluigns directories
     */
    public static List<FileObject> getPluignsDirectories(Dotcake dotcake) {
        return getDirectories(dotcake, BuildPathCategory.PLUGINS);
    }

    /**
     * Get directories from dotcake information
     *
     * @param dotcake
     * @param category
     * @return directories for a category
     */
    public static List<FileObject> getDirectories(Dotcake dotcake, Category category) {
        BuildPathCategory buildPathCategory = getBuildPathCategory(category);
        return getDirectories(dotcake, buildPathCategory);
    }

    private static List<FileObject> getDirectories(Dotcake dotcake, BuildPathCategory buildPathCategory) {
        if (dotcake == null || buildPathCategory == BuildPathCategory.UNKNOWN) {
            return Collections.emptyList();
        }

        // get .cake file
        File dotcakeFile = dotcake.getDotcakeFile();
        if (dotcakeFile == null) {
            return Collections.emptyList();
        }

        // get directories
        FileObject dotcakeFileObject = FileUtil.toFileObject(dotcakeFile);
        List<String> buildPaths = dotcake.getBuildPaths(buildPathCategory);
        ArrayList<FileObject> directories = new ArrayList<>(buildPaths.size());
        for (String path : buildPaths) {
            FileObject fileObject = dotcakeFileObject.getParent().getFileObject(path);
            if (fileObject != null && fileObject.isFolder()) {
                directories.add(fileObject);
            }
        }
        return directories;
    }

    /**
     * Change {@link FILE_TYPE} to {@link BuildPathCategory}.
     *
     * @param category
     * @return BuildPathCategory
     */
    private static BuildPathCategory getBuildPathCategory(Category category) {
        // XXX more?
        switch (category) {
            case ENTITY:
                return BuildPathCategory.ENTITIES;
            case TABLE:
                return BuildPathCategory.TABLES;
            case BEHAVIOR:
                return BuildPathCategory.BEHAVIORS;
            case CONTROLLER:
                return BuildPathCategory.CONTROLLERS;
            case COMPONENT:
                return BuildPathCategory.COMPONENTS;
            case TEMPLATE:
                return BuildPathCategory.TEMPLATES;
            case HELPER:
                return BuildPathCategory.HELPERS;
            case CONSOLE:
                return BuildPathCategory.CONSOLES;
            default:
                return BuildPathCategory.UNKNOWN;
        }
    }

}
