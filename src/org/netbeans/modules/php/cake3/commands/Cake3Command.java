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

import java.lang.ref.WeakReference;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;

/**
 *
 * @author junichi11
 */
public class Cake3Command extends FrameworkCommand {

    private final WeakReference<PhpModule> phpModule;

    public Cake3Command(PhpModule phpModule, String command, String description, String displayName) {
        super(command, description, displayName);
        assert phpModule != null;
        this.phpModule = new WeakReference<>(phpModule);
    }

    public Cake3Command(PhpModule phpModule, String[] command, String description, String displayName) {
        super(command, description, displayName);
        assert phpModule != null;
        this.phpModule = new WeakReference<>(phpModule);
    }

    @Override
    protected String getHelpInternal() {
        PhpModule module = phpModule.get();
        if (module == null) {
            return ""; // NOI18N
        }
        try {
            return Cake3Script.forPhpModule(module, false).getHelp(module, getCommands());
        } catch (InvalidPhpExecutableException ex) {
            UiUtils.invalidScriptProvided(ex.getLocalizedMessage(), UiUtils.FRAMEWORKS_AND_TOOLS_OPTIONS_PATH);
        }
        return ""; // NOI18N
    }

}
