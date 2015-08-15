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
