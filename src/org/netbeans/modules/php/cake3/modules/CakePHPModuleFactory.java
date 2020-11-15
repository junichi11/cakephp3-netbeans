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
package org.netbeans.modules.php.cake3.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.CakeVersion;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.netbeans.modules.php.cake3.dotcake.DotcakeSupport;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class CakePHPModuleFactory {

    public static final CakePHPModule DUMMY_MODULE = new CakePHPModule(new CakePHPModuleDummy(), CakeVersion.create(null));
    private static final CakePHPModuleFactory INSTANCE = new CakePHPModuleFactory();
    private static final Map<PhpModule, CakePHPModule> MODULES = Collections.synchronizedMap(new HashMap<PhpModule, CakePHPModule>());

    private CakePHPModuleFactory() {
    }

    public static CakePHPModuleFactory getInstance() {
        return INSTANCE;
    }

    public CakePHPModule create(PhpModule phpModule) {
        CakePHPModule module = MODULES.get(phpModule);
        if (module == null) {
            CakePHP3ModuleDefault impl = new CakePHP3ModuleDefault(phpModule);
            // add Dotcake
            Dotcake dotcake = createDotcake(phpModule);
            impl.dotcake(dotcake);
            CakeVersion version = impl.createVersion();
            module = new CakePHPModule(impl, version);
            MODULES.put(phpModule, module);
        }
        return module;
    }

    public void remove(PhpModule phpModule) {
        MODULES.remove(phpModule);
    }

    @CheckForNull
    private Dotcake createDotcake(PhpModule phpModule) {
        String dotcakePath = CakePHP3Preferences.getDotcakePath(phpModule);
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return null;
        }
        if (!StringUtils.isEmpty(dotcakePath)) {
            FileObject dotcakeFile = sourceDirectory.getFileObject(dotcakePath);
            return Dotcake.fromJson(dotcakeFile);
        }
        return null;
    }

}
