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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.input.LineProcessor;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.cake3.CakeVersion;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.InputOutput;
import org.xml.sax.SAXException;

/**
 *
 * @author junichi11
 */
public final class Cake3Script {

    public static final String SCRIPT_NAME = "cake"; // NOI18N
    public static final String SCRIPT_NAME_BAT = SCRIPT_NAME + ".bat"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + ".php"; // NOI18N

    // commands
    private static final String COMMAND_LIST_COMMAND = "command_list"; // NOI18N
    private static final String SERVER_COMMAND = "server"; // NOI18N

    // params
    private static final String HELP_PARAM = "--help"; // NOI18N
    private static final String XML_PARAM = "--xml"; // NOI18N

    private static final List<String> COMMAND_LIST_XML_COMMAND = Arrays.asList(COMMAND_LIST_COMMAND, XML_PARAM);
    // get command list via help command since 3.5.0
    private static final List<String> COMMAND_LIST_XML_COMMAND_350 = Arrays.asList(HELP_PARAM, XML_PARAM);
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
            // #35 the bat file outputs an empty line
            cakeScriptPath = String.format(cakeScriptPathFormat, SCRIPT_NAME_LONG);
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

    public String getHelp(PhpModule phpModule, String[] params) {
        assert phpModule != null;

        List<String> allParams;
        allParams = new ArrayList<>();
        // #116 cakephp-netbeans
//        allParams.addAll(getAppParam(phpModule));
        allParams.addAll(Arrays.asList(params));
        allParams.add(HELP_PARAM);

        HelpLineProcessor lineProcessor = new HelpLineProcessor();
        Future<Integer> result = createPhpExecutable(phpModule)
                .displayName(getDisplayName(phpModule, allParams.get(0)))
                .additionalParameters(getAllParams(allParams))
                .run(getSilentDescriptor(), getOutProcessorFactory(lineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (CancellationException ex) {
            // canceled
        } catch (ExecutionException ex) {
            UiUtils.processExecutionException(ex, getOptionsPath());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return lineProcessor.getHelp();
    }

    public List<FrameworkCommand> getCommands(PhpModule phpModule) {
        List<FrameworkCommand> freshCommands = getFrameworkCommandsInternalXml(phpModule);
        if (freshCommands != null) {
            return freshCommands;
        }
        // XXX some error => rerun command with console
        runCommand(phpModule, Collections.singletonList(HELP_PARAM), null);
        return Collections.emptyList();
    }

    @NbBundle.Messages({
        "Cake3Script.redirect.xml.error=error is occurred when xml file is created for command list."
    })
    private List<FrameworkCommand> getFrameworkCommandsInternalXml(PhpModule phpModule) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile("nb-cake-commands-", ".xml"); // NOI18N
            tmpFile.deleteOnExit();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return null;
        }

        if (!redirectToFile(phpModule, tmpFile, getCommandListParams(phpModule))) {
            LOGGER.log(Level.WARNING, Bundle.Cake3Script_redirect_xml_error());
            return null;
        }
        List<Cake3CommandItem> commandsItem = new ArrayList<>();
        try {
            CakePHP3CommandXmlParser.parse(tmpFile, commandsItem);
        } catch (SAXException ex) {
            // incorrect xml provided by cakephp?
            LOGGER.log(Level.INFO, null, ex);
        }
        if (commandsItem.isEmpty()) {
            // error
            tmpFile.delete();
            return null;
        }
        // parse each command

        List<FrameworkCommand> commands = new ArrayList<>();
        for (Cake3CommandItem item : commandsItem) {
            ArrayList<String> commandParams = new ArrayList<>();
//            commandParams.addAll(appParam);
            commandParams.addAll(Arrays.asList(item.getCommand(), HELP_PARAM, "xml")); // NOI18N
            if (!redirectToFile(phpModule, tmpFile, commandParams)) {
                commands.add(new Cake3Command(phpModule,
                        item.getCommand(), item.getDescription(), item.getDisplayName()));
                continue;
            }
            List<Cake3CommandItem> mainCommandsItem = new ArrayList<>();
            try {
                CakePHP3CommandXmlParser.parse(tmpFile, mainCommandsItem);
            } catch (SAXException ex) {
                LOGGER.log(Level.WARNING, "Xml file Error:{0}", ex.getMessage());
                commands.add(new Cake3Command(phpModule,
                        item.getCommand(), item.getDescription(), item.getDisplayName()));
                continue;
            }
            if (mainCommandsItem.isEmpty()) {
                tmpFile.delete();
                return null;
            }
            // add main command
            Cake3CommandItem main = mainCommandsItem.get(0);
            String mainCommand = main.getCommand();
            String provider = item.getDescription();
            commands.add(new Cake3Command(phpModule,
                    mainCommand, "[" + provider + "] " + main.getDescription(), main.getDisplayName())); // NOI18N

            // add subcommands
            List<Cake3CommandItem> subcommands = main.getSubcommands();
            for (Cake3CommandItem subcommand : subcommands) {
                String[] command = {mainCommand, subcommand.getCommand()};
                commands.add(new Cake3Command(phpModule,
                        command, "[" + provider + "] " + subcommand.getDescription(), main.getCommand() + " " + subcommand.getDisplayName())); // NOI18N
            }
        }
        tmpFile.delete();
        return commands;
    }

    private List<String> getCommandListParams(PhpModule phpModule) {
        List<String> params = new ArrayList<>();
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        CakeVersion version = cakeModule.getVersion();
        if (version != null) {
            if (version.getMajor() >= 3 && version.getMinor() >= 5) {
                params.addAll(COMMAND_LIST_XML_COMMAND_350);
            } else {
                params.addAll(COMMAND_LIST_XML_COMMAND);
            }
        }
        return params;
    }

    @NbBundle.Messages({
        "# {0} - exitValue",
        "Cake3Script.redirect.error=exitValue:{0} There may be some errors when redirect command result to file"
    })
    private boolean redirectToFile(PhpModule phpModule, File file, List<String> commands) {
        Future<Integer> result = createPhpExecutable(phpModule)
                .fileOutput(file, "UTF-8", true) // NOI18N
                .warnUser(false)
                .additionalParameters(commands)
                .run(getSilentDescriptor());
        try {
            if (result == null) {
                // error
                return false;
            }
            // CakePHP 3.x uses exit() in cake script, so, return value is not 0
            Integer exitValue = result.get();
            if (exitValue != 0) {
                if (exitValue != 1) {
                    LOGGER.log(Level.WARNING, Bundle.Cake3Script_redirect_error(exitValue));
                    return false;
                }
            }
        } catch (CancellationException | ExecutionException ex) {
            return false;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    private ExecutionDescriptor getSilentDescriptor() {
        return new ExecutionDescriptor()
                .inputOutput(InputOutput.NULL);
    }

    private PhpExecutable createPhpExecutable(PhpModule phpModule) {
        CakePHP3Module module = CakePHP3Module.forPhpModule(phpModule);
        List<FileObject> directories = module.getDirectories(Base.APP);
        PhpExecutable phpExecutable = new PhpExecutable(cakePath)
                .viaAutodetection(true);
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
        "Cake3Script.command.title={0} ({1})"
    })
    private String getDisplayName(PhpModule phpModule, String command) {
        return Bundle.Cake3Script_command_title(phpModule.getDisplayName(), command);
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

    private ExecutionDescriptor.InputProcessorFactory2 getOutProcessorFactory(final LineProcessor lineProcessor) {
        if (lineProcessor == null) {
            // prevent to show results twice in output window
            return null;
        }
        return new ExecutionDescriptor.InputProcessorFactory2() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
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

    private static class HelpLineProcessor implements LineProcessor {

        private final StringBuilder sb = new StringBuilder();

        @Override
        public void processLine(String line) {
            sb.append(line);
            sb.append("\n"); // NOI18N
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        public String getHelp() {
            return sb.toString();
        }
    }
}
