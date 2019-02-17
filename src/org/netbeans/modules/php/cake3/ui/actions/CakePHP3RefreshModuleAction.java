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

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3ModuleFactory;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePHP3RefreshModuleAction extends CakePHP3BaseAction {

    private static final long serialVersionUID = -8188380453371163703L;

    @Override
    protected String getFullName() {
        return Bundle.CakePHPBaseAction_fullName(getPureName());
    }

    @NbBundle.Messages({
        "CakePHP3RefreshModuleAction.name=Refresh"
    })
    @Override
    protected String getPureName() {
        return Bundle.CakePHP3RefreshModuleAction_name();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        if (!CakePHP3Module.isCakePHP(phpModule)) {
            return;
        }
        CakePHP3ModuleFactory.getInstance().remove(phpModule);
    }

}
