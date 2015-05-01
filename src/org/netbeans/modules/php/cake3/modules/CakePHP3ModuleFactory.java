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
package org.netbeans.modules.php.cake3.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.CakeVersion;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class CakePHP3ModuleFactory {

    public static final CakePHP3Module DUMMY_MODULE = new CakePHP3Module(new CakePHP3ModuleDummy(), CakeVersion.create(null));
    private static final CakePHP3ModuleFactory INSTANCE = new CakePHP3ModuleFactory();
    private static final Map<PhpModule, CakePHP3Module> MODULES = Collections.synchronizedMap(new HashMap<PhpModule, CakePHP3Module>());

    private CakePHP3ModuleFactory() {
    }

    public static CakePHP3ModuleFactory getInstance() {
        return INSTANCE;
    }

    public CakePHP3Module create(PhpModule phpModule) {
        CakePHP3Module module = MODULES.get(phpModule);
        if (module == null) {
            CakePHP3ModuleDefault impl = new CakePHP3ModuleDefault(phpModule);
            // add Dotcake
            Dotcake dotcake = createDotcake(phpModule);
            impl.dotcake(dotcake);
            CakeVersion version = impl.createVersion();
            module = new CakePHP3Module(impl, version);
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
