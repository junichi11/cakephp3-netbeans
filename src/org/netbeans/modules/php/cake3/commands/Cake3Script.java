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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author junichi11
 */
public final class Cake3Script {

    public static final String SCRIPT_NAME = "cake"; // NOI18N
    public static final String SCRIPT_NAME_BAT = SCRIPT_NAME + ".bat"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + ".php"; // NOI18N

    private static final String SERVER_COMMAND = "server"; // NOI18N
    private static final List<String> DEFAULT_PARAMS = Collections.emptyList();
    private static final Logger LOGGER = Logger.getLogger(Cake3Script.class.getName());

    private final String cakePath;

    private Cake3Script(String cakePath) {
        this.cakePath = cakePath;
    }

    /**
     * Get the project specific, <b>valid only</b> Cake script. If not found,
     * {@code null} is returned.
     *
     * @param phpModule PHP module for which Cake script is taken
     * @param warn {@code true} if user is warned when the Cake script is not
     * valid
     * @return Cake console script or {@code null} if the script is not valid
     */
    @NbBundle.Messages({
        "# {0} - error message",
        "CakeScript.script.invalid=<html>Project's Cake script is not valid.<br>({0})"
    })
    public static Cake3Script forPhpModule(PhpModule phpModule, boolean warn) throws InvalidPhpExecutableException {
        String console = null;
        FileObject script = getScript(phpModule);
        if (script != null) {
            console = FileUtil.toFile(script).getAbsolutePath();
        }
        String error = validate(console);
        if (error == null) {
            return new Cake3Script(console);
        }
        if (warn) {
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    Bundle.CakeScript_script_invalid(error),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
        }
        throw new InvalidPhpExecutableException(error);
    }

    private static FileObject getScript(PhpModule phpModule) {
        CakePHP3Module module = CakePHP3Module.forPhpModule(phpModule);
        if (module == null) {
            return null;
        }
        List<FileObject> directories = module.getDirectories(Base.APP);
        String cakeScriptPathFormat = "bin/%s"; // NOI18N
        String cakeScriptPath;
        if (Utilities.isWindows()) {
            cakeScriptPath = String.format(cakeScriptPathFormat, SCRIPT_NAME_BAT);
        } else {
            cakeScriptPath = String.format(cakeScriptPathFormat, SCRIPT_NAME);
        }
        for (FileObject directory : directories) {
            return directory.getFileObject(cakeScriptPath);
        }
        LOGGER.log(Level.WARNING, "Not found {0}", cakeScriptPath); // NOI18N
        return null;
    }

    @NbBundle.Messages("CakeScript.script.label=Cake script")
    public static String validate(String command) {
        return PhpExecutableValidator.validateCommand(command, Bundle.CakeScript_script_label());
    }

    /**
     * Run built-in server.
     *
     * @param phpModule PhpModule
     */
    public Future<Integer> server(PhpModule phpModule) {
        return runCommand(phpModule, Arrays.asList(SERVER_COMMAND), null);
    }

    public Future<Integer> runCommand(PhpModule phpModule, List<String> parameters, Runnable postExecution) {
        LineProcessor lineProcessor = null;
        if (parameters.contains(SERVER_COMMAND)) {
            lineProcessor = new ServerLineProcessor();
        }
        return createPhpExecutable(phpModule)
                .displayName(getDisplayName(phpModule, parameters.get(0)))
                .additionalParameters(getAllParams(parameters))
                .run(getDescriptor(postExecution), getOutProcessorFactory(lineProcessor));
    }

    private PhpExecutable createPhpExecutable(PhpModule phpModule) {
        CakePHP3Module module = CakePHP3Module.forPhpModule(phpModule);
        List<FileObject> directories = module.getDirectories(Base.APP);
        PhpExecutable phpExecutable = new PhpExecutable(cakePath)
                .viaPhpInterpreter(false)
                .viaAutodetection(false);
        if (!directories.isEmpty()) {
            File workDir = FileUtil.toFile(directories.get(0));
            phpExecutable = phpExecutable.workDir(workDir);
        }
        return phpExecutable;
    }

    private List<String> getAllParams(List<String> params) {
        List<String> allParams = new ArrayList<>();
        allParams.addAll(DEFAULT_PARAMS);
        allParams.addAll(params);
        return allParams;
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "# {1} - command",
        "CakeScript.command.title={0} ({1})"
    })
    private String getDisplayName(PhpModule phpModule, String command) {
        return Bundle.CakeScript_command_title(phpModule.getDisplayName(), command);
    }

    private ExecutionDescriptor getDescriptor(Runnable postExecution) {
        ExecutionDescriptor executionDescriptor = PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR
                .optionsPath(getOptionsPath())
                .inputVisible(true);
        if (postExecution != null) {
            executionDescriptor = executionDescriptor.postExecution(postExecution);
        }
        return executionDescriptor;
    }

    private ExecutionDescriptor.InputProcessorFactory getOutProcessorFactory(final LineProcessor lineProcessor) {
        return new ExecutionDescriptor.InputProcessorFactory() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                if (lineProcessor == null) {
                    return defaultProcessor;
                }
                return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
            }
        };
    }

    private static String getOptionsPath() {
        return UiUtils.FRAMEWORKS_AND_TOOLS_OPTIONS_PATH;
    }

    private static class ServerLineProcessor implements LineProcessor {

        private static final Pattern LOCALHOST_PATTERN = Pattern.compile("\\A.*(?<localhost>http://localhost:\\d{4}/).*\\z"); // NOI18N

        @Override
        public void processLine(String line) {
            Matcher matcher = LOCALHOST_PATTERN.matcher(line);
            if (matcher.find()) {
                String localhostUrl = matcher.group("localhost"); // NOI18N
                try {
                    URL url = new URL(localhostUrl);
                    // TODO add options?
                    HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

    }
}
