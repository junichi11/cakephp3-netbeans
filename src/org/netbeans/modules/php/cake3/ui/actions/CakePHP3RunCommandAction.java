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
import org.netbeans.modules.php.cake3.CakePHP3FrameworkProvider;
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
import org.netbeans.modules.php.spi.framework.actions.RunCommandAction;

/**
 *
 * @author junichi11
 */
public final class CakePHP3RunCommandAction extends RunCommandAction {

    private static final CakePHP3RunCommandAction INSTANCE = new CakePHP3RunCommandAction();
    private static final long serialVersionUID = 3541330098499478637L;

    public static CakePHP3RunCommandAction getInstance() {
        return INSTANCE;
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        if (!CakePHPModule.isCakePHP(phpModule)) {
            return;
        }
        CakePHP3FrameworkProvider.getInstance().getFrameworkCommandSupport(phpModule).openPanel();
    }

    @Override
    protected String getFullName() {
        return Bundle.CakePHPBaseAction_fullName(getPureName());
    }

}
