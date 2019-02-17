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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.modules.php.cake3.utils.JsonSimpleSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Parse .cake file.
 * <pre>
 * format of .cake file
 * {
 *     "cake": "..\/lib\/",
 *     "build_path": {
 *         "models": [
 *             ".\/Model\/"
 *         ],
 *         ...,
 *         "plugins": [
 *             ".\/Plugin\/",
 *             "..\/plugins\/"
 *         ]
 *     }
 * }
 * </pre>
 * @link https://github.com/dotcake/dotcake
 * @author junichi11
 */
public final class Dotcake {

    private String cake;
    private Map<String, List<String>> build_path;
    private File dotcakeFile;
    public static final String DOTCAKE_NAME = ".cake"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(Dotcake.class.getName());

    public enum BuildPathCategory {

        BEHAVIORS,
        COMPONENTS,
        CONTROLLERS,
        CONSOLES,
        HELPERS,
        LOCALES,
        PLUGINS,
        SHELLS,
        TABLES,
        ENTITIES,
        TASKS,
        TEMPLATES,
        VIEWS,
        UNKNOWN;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    public Dotcake() {
    }

    private Dotcake setDotcakeFile(File dotcakeFile) {
        this.dotcakeFile = dotcakeFile;
        return this;
    }

    public File getDotcakeFile() {
        return dotcakeFile;
    }

    /**
     * Create an instance from json file.
     *
     * @param dotcakeFile .cake file
     * @return {@code null} if .cake file has some problem, {@code Dotcake}
     * instance otherwise.
     */
    @CheckForNull
    public static Dotcake fromJson(FileObject dotcakeFile) {
        if (!isDotcake(dotcakeFile)) {
            return null;
        }
        try (InputStream inputStream = new BufferedInputStream(dotcakeFile.getInputStream())) {
            try (InputStreamReader reader = new InputStreamReader(inputStream, CakePHP3Constants.UTF8)) {
                Dotcake dotcake = JsonSimpleSupport.fromJson(reader, Dotcake.class);
                if (dotcake == null) {
                    return null;
                }
                dotcake.setDotcakeFile(FileUtil.toFile(dotcakeFile));
                return dotcake;
            }
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    /**
     * Check whether file is .cake.
     *
     * @param file
     * @return {@code true} if file is .cake, {@code false} otherwise.
     */
    public static boolean isDotcake(FileObject file) {
        return file != null && !file.isFolder() && file.getNameExt().equals(DOTCAKE_NAME);
    }

    public String getCake() {
        return cake;
    }

    public Map<String, List<String>> getBuildPath() {
        return new HashMap<>(build_path);
    }

    /**
     * Get build paths for BuildPathCategory.
     *
     * @param category BuildPathCategory
     * @return paths
     */
    public List<String> getBuildPaths(BuildPathCategory category) {
        if (category == null) {
            return Collections.emptyList();
        }
        String name = category.toString();
        if (StringUtils.isEmpty(name) || build_path == null) {
            return Collections.emptyList();
        }
        List<String> paths = build_path.get(name);
        if (paths == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(paths);
    }
}
