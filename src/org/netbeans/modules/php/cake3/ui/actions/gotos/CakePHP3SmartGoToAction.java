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
package org.netbeans.modules.php.cake3.ui.actions.gotos;

import java.util.List;
import org.netbeans.api.editor.EditorActionRegistration;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatus;

@EditorActionRegistration(name = "cake3-smart-goto")
public class CakePHP3SmartGoToAction extends CakePHP3GoToAction {

    private static final long serialVersionUID = -3287999601900817083L;

    @Override
    protected List<GoToItem> getGoToItems(CakePHP3GoToStatus status) {
        return status.getSmart();
    }

    @Override
    protected String getPopupTitle() {
        return "Smart Go To"; // NOI18N
    }

}
