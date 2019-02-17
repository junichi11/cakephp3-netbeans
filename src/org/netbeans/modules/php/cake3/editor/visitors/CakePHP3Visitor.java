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
package org.netbeans.modules.php.cake3.editor.visitors;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public class CakePHP3Visitor extends DefaultVisitor {

    private final PhpModule phpModule;
    private static final Logger LOGGER = Logger.getLogger(CakePHP3Visitor.class.getName());

    public CakePHP3Visitor(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    @CheckForNull
    public Pair<String, PhpClass> createPhpClass(Category category, String aliasName, String entityName) {
        if (StringUtils.isEmpty(aliasName)) {
            return null;
        }

        if (StringUtils.isEmpty(entityName)) {
            entityName = aliasName;
        }
        boolean isSame = aliasName.equals(entityName);
        // plugin?
        String pluginName = null;
        int dotPosition = entityName.indexOf("."); // NOI18N
        if (dotPosition > 0) {
            pluginName = entityName.substring(0, dotPosition);
            entityName = entityName.substring(dotPosition + 1);
            if (isSame) {
                aliasName = entityName;
            }
        }

        //get entity file
        FileObject entityFile = getEntityFile(category, entityName, pluginName);
        if (entityFile == null) {
            return null;
        }

        return createPhpClass(aliasName, entityFile);
    }

    @CheckForNull
    private Pair<String, PhpClass> createPhpClass(String aliasName, FileObject entityFile) {
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        if (editorSupport == null) {
            LOGGER.log(Level.INFO, "Not found:{0}", EditorSupport.class);
            return null;
        }

        if (StringUtils.isEmpty(aliasName)) {
            return null;
        }
        Collection<PhpClass> phpClasses = editorSupport.getClasses(entityFile);
        for (PhpClass phpClass : phpClasses) {
            phpClass.setFile(entityFile);
            return Pair.of(aliasName, phpClass);
        }
        return null;
    }

    @CheckForNull
    protected FileObject getEntityFile(Category category, String entityName, String pluginName) {
        CakePHP3Module module = CakePHP3Module.forPhpModule(phpModule);
        CakePHP3Module.Base base;
        if (!StringUtils.isEmpty(pluginName)) {
            base = CakePHP3Module.Base.PLUGIN;
        } else {
            base = CakePHP3Module.Base.APP;
        }
        String relativePath = module.toPhpFileName(category, entityName);
        return module.getFile(base, category, relativePath, pluginName);
    }

}
