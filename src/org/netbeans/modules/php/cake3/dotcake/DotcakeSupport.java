/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
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
