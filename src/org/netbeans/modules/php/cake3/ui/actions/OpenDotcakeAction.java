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

import java.io.File;
import javax.swing.SwingUtilities;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class OpenDotcakeAction extends CakePHP3BaseAction {

    private static final long serialVersionUID = -516324482137680128L;

    @Override
    protected String getFullName() {
        return Bundle.CakePHPBaseAction_fullName(getPureName());
    }

    @NbBundle.Messages({
        "OpenDotcakeAction.displayName=Open .cake"
    })
    @Override
    protected String getPureName() {
        return Bundle.OpenDotcakeAction_displayName();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        Dotcake dotcake = cakeModule.getDotcake();
        if (dotcake == null) {
            return;
        }
        File file = dotcake.getDotcakeFile();
        if (file == null) {
            return;
        }
        final FileObject dotcakeFile = FileUtil.toFileObject(file);
        if (dotcakeFile == null) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiUtils.open(dotcakeFile, 0);
            }
        });
    }

}
