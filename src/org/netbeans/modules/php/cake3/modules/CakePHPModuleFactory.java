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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger LOGGER = Logger.getLogger(CakePHPModuleFactory.class.getName());

    private CakePHPModuleFactory() {
    }

    public static CakePHPModuleFactory getInstance() {
        return INSTANCE;
    }

    public CakePHPModule create(PhpModule phpModule) {
        CakePHPModule module = MODULES.get(phpModule);
        if (module == null) {
            Dotcake dotcake = createDotcake(phpModule);
            CakeVersion version = createVersion(dotcake, phpModule);
            CakePHPModuleImpl impl;
            switch (version.getMajor()) {
                case 3:
                    impl = new CakePHP3ModuleDefault(phpModule);
                    break;
                case 4:
                    impl = new CakePHP4ModuleDefault(phpModule);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unsupported version: {0}", version.getVersionNumber()); // NOI18N
                    impl = new CakePHP4ModuleDefault(phpModule);
                    break;
            }
            impl.dotcake(dotcake);
            module = new CakePHPModule(impl, version);
            MODULES.put(phpModule, module);
        }
        return module;
    }

    public void remove(PhpModule phpModule) {
        MODULES.remove(phpModule);
    }

    private CakeVersion createVersion(Dotcake  dotcake, PhpModule phpModule) {
        FileObject coreDirecotry = getCoreDirecotry(dotcake, phpModule);
        FileObject versionFile = null;
        if (coreDirecotry != null) {
            versionFile = coreDirecotry.getFileObject("VERSION.txt"); // NOI18N
        }
        return CakeVersion.create(versionFile);
    }

    @CheckForNull
    private FileObject getCoreDirecotry(Dotcake dotcake, PhpModule phpModule) {
        // Dotcake support
        if (dotcake != null) {
            FileObject coreDirectory = DotcakeSupport.getCoreDirectory(dotcake);
            if (coreDirectory != null) {
                return coreDirectory;
            }
        }

        // default
        FileObject rootDirectory = getRootDirectory(phpModule);
        if (rootDirectory == null) {
            return null;
        }
        return rootDirectory.getFileObject("vendor/cakephp/cakephp"); // NOI18N
    }

    @CheckForNull
    private FileObject getRootDirectory(PhpModule phpModule) {
        if (phpModule == null) {
            return null;
        }
        String rootPath = CakePHP3Preferences.getRootPath(phpModule);
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory != null) {
            return sourceDirectory.getFileObject(rootPath);
        }
        return null;
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
