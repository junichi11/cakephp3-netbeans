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

import org.netbeans.modules.php.cake3.ui.actions.gotos.CakePHP3SmartGoToAction;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;

/**
 *
 * @author junichi11
 */
public class CakePHP3GoToViewAction extends GoToViewAction {

    private static final long serialVersionUID = -8407813132091607464L;

    @Override
    public boolean goToView() {
        CakePHP3SmartGoToAction action = new CakePHP3SmartGoToAction();
        action.actionPerformed(null);
        return true;
    }

}
