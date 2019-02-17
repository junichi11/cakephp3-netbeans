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
package org.netbeans.modules.php.cake3.commands;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.cake3.options.CakePHP3Options;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class CakePHP3ProjectGenerator {

    private final String composerPath;
    private static final String CREATE_PROJECT_COMMAND = "create-project"; // NOI18N
    private static final String NO_INTERACTION_PARAM = "--no-interaction"; // NOI18N
    private static final String PREFER_DIST_PARAM = "--prefer-dist"; // NOI18N
    private static final String CAKEPHP_APP = "cakephp/app"; // NOI18N

    private CakePHP3ProjectGenerator(String composerPath) {
        this.composerPath = composerPath;
    }

    /**
     * Get default CakePHP3ProjectGenerator.
     *
     * @return
     * @throws InvalidPhpExecutableException
     */
    @NbBundle.Messages({
        "CakePHP3ProjectGenerator.invalid.composer=Is not set composer path. Please set it to Options."
    })
    public static CakePHP3ProjectGenerator getDefault() throws InvalidPhpExecutableException {
        CakePHP3Options options = CakePHP3Options.getInstance();
        String composerPath = options.getComposerPath();
        if (!StringUtils.isEmpty(composerPath)) {
            return new CakePHP3ProjectGenerator(composerPath);
        }
        throw new InvalidPhpExecutableException(Bundle.CakePHP3ProjectGenerator_invalid_composer());
    }

    /**
     * Generate project via Composer. Use composer create-project command.
     *
     * @param projectDirectory project directory
     * @param projectName project name
     */
    public void generate(File projectDirectory, String projectName) {
        File workDir = projectDirectory.getParentFile();
        PhpExecutable phpExecutable = new PhpExecutable(composerPath)
                .workDir(workDir)
                .additionalParameters(Arrays.asList(CREATE_PROJECT_COMMAND, NO_INTERACTION_PARAM, PREFER_DIST_PARAM, CAKEPHP_APP, projectName)); // NOI18N
        Future<Integer> result = phpExecutable.run();
        if (result != null) {
            try {
                result.get();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException ex) {
                UiUtils.processExecutionException(ex);
            }
        }
    }

}
