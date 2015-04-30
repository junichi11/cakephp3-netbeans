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
package org.netbeans.modules.php.cake3.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.openide.filesystems.FileObject;
import org.openide.loaders.CreateFromTemplateAttributesProvider;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = CreateFromTemplateAttributesProvider.class)
public class CakePHP3TemplateAttributesProvider implements CreateFromTemplateAttributesProvider {

    private static final String NAMESPACE = "namespace"; // NOI18N

    @Override
    public Map<String, ?> attributesFor(DataObject template, DataFolder target, String name) {
        FileObject targetDirectory = target.getPrimaryFile();
        if (targetDirectory == null) {
            return Collections.emptyMap();
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(targetDirectory);
        if (phpModule == null) {
            return Collections.emptyMap();
        }
        if (!CakePHP3Module.isCakePHP(phpModule)) {
            return Collections.emptyMap();
        }
        Map<String, String> attributes = new HashMap<>();
        FileObject primaryFile = template.getPrimaryFile();
        if (primaryFile == null) {
            return Collections.emptyMap();
        }

        FileObject parent = primaryFile.getParent();
        if (parent == null) {
            return Collections.emptyMap();
        }
        if (parent.isFolder() && parent.getNameExt().equals(CakePHP3Constants.CAKEPHP3_FRAMEWORK)) {
            CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
            String namespace = cakeModule.getNamespace(targetDirectory);
            attributes.put(NAMESPACE, namespace);
        }

        return attributes;
    }

}
