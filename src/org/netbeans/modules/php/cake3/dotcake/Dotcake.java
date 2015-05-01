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
