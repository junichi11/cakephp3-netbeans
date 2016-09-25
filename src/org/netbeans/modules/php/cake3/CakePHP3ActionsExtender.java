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
package org.netbeans.modules.php.cake3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
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
        "CakePHP3ActionsExtender.menuName=CakePHP3"
    })
    @Override
    public String getMenuName() {
        return Bundle.CakePHP3ActionsExtender_menuName();
    }

    @Override
    public List<? extends Action> getActions() {
        List<CakePHP3BaseAction> defaultActions = Arrays.asList(
                new CakeServerAction(),
                new CakePHP3RefreshModuleAction()
        );
        List<CakePHP3BaseAction> actions = new ArrayList<>(defaultActions);
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
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
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fo);
        CakePHP3Module.Category category = cakeModule.getCategory(fo);
        return category == Category.CONTROLLER;
    }

    @Override
    public boolean isViewWithAction(FileObject fo) {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fo);
        return cakeModule.isTemplateFile(fo);
    }

    @Override
    public RunCommandAction getRunCommandAction() {
        return CakePHP3RunCommandAction.getInstance();
    }

}
