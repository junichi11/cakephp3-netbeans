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

import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.cake3.commands.CakePHP3FrameworkCommandSupport;
import org.netbeans.modules.php.cake3.editor.CakePHP3EditorExtender;
import org.netbeans.modules.php.cake3.modules.CakePHPModuleFactory;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
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
        // #25
        if (phpModule == null) {
            return false;
        }
        return CakePHP3Preferences.isEnabled(phpModule);
    }

    @Override
    public ImportantFilesImplementation getConfigurationFiles2(PhpModule phpModule) {
        return new ConfigurationFiles(phpModule);
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
        return new CakePHP3ActionsExtender(phpModule);
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        // TODO
        return null;
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        return new CakePHP3FrameworkCommandSupport(phpModule);
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
        CakePHPModuleFactory factory = CakePHPModuleFactory.getInstance();
        factory.remove(phpModule);
    }

}
