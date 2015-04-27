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

import java.io.File;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.cake3.editor.CakePHP3EditorExtender;
import org.netbeans.modules.php.cake3.modules.CakePHP3ModuleFactory;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePHP3FrameworkProvider extends PhpFrameworkProvider {

    private static final CakePHP3FrameworkProvider INSTANCE = new CakePHP3FrameworkProvider();
    @StaticResource
    private static final String ICON_PATH = "org/netbeans/modules/php/cake3/resources/badge_icon_8.png"; // NOI18N
    private final BadgeIcon badgeIcon;

    @NbBundle.Messages({
        "CakePHP3FrameworkProvider.name=CakePHP3",
        "CakePHP3FrameworkProvider.description=CakePHP3"
    })
    private CakePHP3FrameworkProvider() {
        super("cakephp3", // NOI18N
                Bundle.CakePHP3FrameworkProvider_name(),
                Bundle.CakePHP3FrameworkProvider_description());
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                CakePHP3FrameworkProvider.class.getResource("/" + ICON_PATH) // NOI18N
        );
    }

    @PhpFrameworkProvider.Registration(position = 499)
    public static CakePHP3FrameworkProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        return CakePHP3Preferences.isEnabled(phpModule);
    }

    @Override
    public File[] getConfigurationFiles(PhpModule phpModule) {
        // TODO
        // XXX this method will be deprecated in the next stable version
        return new File[0];
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        // TODO
        return null;
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        return new PhpModuleProperties();
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule phpModule) {
        return new CakePHP3ActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        // TODO
        return null;
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        // TODO
        return null;
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new CakePHP3ModuleCustomizerExtender(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new CakePHP3EditorExtender(phpModule);
    }

    @Override
    public void phpModuleClosed(PhpModule phpModule) {
        // release CakePHP3Module
        CakePHP3ModuleFactory factory = CakePHP3ModuleFactory.getInstance();
        factory.remove(phpModule);
    }

}
