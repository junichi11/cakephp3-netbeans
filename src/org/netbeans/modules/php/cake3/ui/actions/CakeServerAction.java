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
package org.netbeans.modules.php.cake3.ui.actions;

import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.commands.Cake3Script;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakeServerAction extends CakePHP3BaseAction {

    private static final long serialVersionUID = -7115541061573607385L;

    @Override
    protected String getFullName() {
        return Bundle.CakePHPBaseAction_fullName(getPureName());
    }

    @NbBundle.Messages({
        "CakeServerAction.displayName=Run server"
    })
    @Override
    protected String getPureName() {
        return Bundle.CakeServerAction_displayName();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // via shortcut
        if (!CakePHP3Module.isCakePHP(phpModule)) {
            return;
        }
        try {
            Cake3Script cakeScript = Cake3Script.forPhpModule(phpModule, true); // show an error dialog
            cakeScript.server(phpModule);
        } catch (InvalidPhpExecutableException ex) {
            // ignore since an error dialog is shown
        }
    }

}
