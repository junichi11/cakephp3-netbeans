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
package org.netbeans.modules.php.cake3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
import org.netbeans.modules.php.cake3.modules.CakePHPModule.Category;
import org.netbeans.modules.php.cake3.ui.actions.CakePHP3BaseAction;
import org.netbeans.modules.php.cake3.ui.actions.CakePHP3GoToActionAction;
import org.netbeans.modules.php.cake3.ui.actions.CakePHP3GoToViewAction;
import org.netbeans.modules.php.cake3.ui.actions.CakePHP3RefreshModuleAction;
import org.netbeans.modules.php.cake3.ui.actions.CakePHP3RunCommandAction;
import org.netbeans.modules.php.cake3.ui.actions.CakeServerAction;
import org.netbeans.modules.php.cake3.ui.actions.OpenDotcakeAction;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.netbeans.modules.php.spi.framework.actions.RunCommandAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePHP3ActionsExtender extends PhpModuleActionsExtender {

    private final PhpModule phpModule;

    public CakePHP3ActionsExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    @NbBundle.Messages({
        "# {0} - version",
        "CakePHP3ActionsExtender.menuName=CakePHP{0}"
    })
    @Override
    public String getMenuName() {
        CakePHPModule cakeModule = CakePHPModule.forPhpModule(phpModule);
        int majorVersion = cakeModule.getVersion().getMajor();
        assert majorVersion >= 3;
        return Bundle.CakePHP3ActionsExtender_menuName(majorVersion);
    }

    @Override
    public List<? extends Action> getActions() {
        List<CakePHP3BaseAction> defaultActions = Arrays.asList(
                new CakeServerAction(),
                new CakePHP3RefreshModuleAction()
        );
        List<CakePHP3BaseAction> actions = new ArrayList<>(defaultActions);
        CakePHPModule cakeModule = CakePHPModule.forPhpModule(phpModule);
        Dotcake dotcake = cakeModule.getDotcake();
        if (dotcake != null) {
            actions.add(new OpenDotcakeAction());
        }
        return actions;
    }

    @Override
    public GoToViewAction getGoToViewAction(FileObject fo, int offset) {
        return new CakePHP3GoToViewAction();
    }

    @Override
    public GoToActionAction getGoToActionAction(FileObject fo, int offset) {
        return new CakePHP3GoToActionAction();
    }

    @Override
    public boolean isActionWithView(FileObject fo) {
        CakePHPModule cakeModule = CakePHPModule.forFileObject(fo);
        CakePHPModule.Category category = cakeModule.getCategory(fo);
        return category == Category.CONTROLLER;
    }

    @Override
    public boolean isViewWithAction(FileObject fo) {
        CakePHPModule cakeModule = CakePHPModule.forFileObject(fo);
        return cakeModule.isTemplateFile(fo);
    }

    @Override
    public RunCommandAction getRunCommandAction() {
        return CakePHP3RunCommandAction.getInstance();
    }

}
