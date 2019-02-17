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
package org.netbeans.modules.php.cake3.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.templates.CreateDescriptor;
import org.netbeans.api.templates.CreateFromTemplateAttributes;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = CreateFromTemplateAttributes.class)
public class CakePHP3TemplateAttributesProvider implements CreateFromTemplateAttributes {

    private static final String NAMESPACE = "namespace"; // NOI18N

    @Override
    public Map<String, ?> attributesFor(CreateDescriptor desc) {
        // target
        FileObject targetDirectory = desc.getTarget();
        PhpModule phpModule = PhpModule.Factory.forFileObject(targetDirectory);
        if (phpModule == null) {
            return Collections.emptyMap();
        }
        if (!CakePHP3Module.isCakePHP(phpModule)) {
            return Collections.emptyMap();
        }

        // template
        Map<String, String> attributes = new HashMap<>();
        FileObject template = desc.getTemplate();
        FileObject parent = template.getParent();
        if (parent == null) {
            return Collections.emptyMap();
        }

        // set attributes
        if (parent.isFolder() && parent.getNameExt().equals(CakePHP3Constants.CAKEPHP3_FRAMEWORK)) {
            CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
            String namespace = cakeModule.getNamespace(targetDirectory);
            attributes.put(NAMESPACE, namespace);
        }

        return attributes;
    }

}
