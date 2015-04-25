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
