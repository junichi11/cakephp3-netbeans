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
import java.util.List;
import java.util.Set;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.CakePHP3FrameworkProvider;
import static org.netbeans.modules.php.cake3.modules.CakePHP3ModuleFactory.DUMMY_MODULE;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class CakePHP3Module {

    public enum Category {

        BEHAVIOR("Behavior"), // NOI18N
        CONFIG(""), // NOI18N
        CONSOLE(""), // NOI18N
        CONTROLLER("Controller"), // NOI18N
        COMPONENT("Component"), // NOI18N
        CSS(""), // NOI18N
        DIR(""), // NOI18N
        ELEMENT(""), // NOI18N
        EMAIL(""), // NOI18N
        ENTITY(""), // NOI18N
        ERROR(""), // NOI18N
        FIXTURE("Fixture"), // NOI18N
        HELPER("Helper"), // NOI18N
        IMG(""), // NOI18N
        JS(""), // NOI18N
        LAYOUT(""), // NOI18N
        LOCALE(""), // NOI18N
        MODEL(""), // NOI18N
        PAGES(""), // NOI18N
        SHELL("Shell"), // NOI18N
        TABLE("Table"), // NOI18N
        TASK("Task"), // NOI18N
        TEMPLATE(""), // NOI18N
        TEMPLATE_CELL(""), // NOI18N
        TEST(""), // NOI18N
        TEST_CASE("Test"), // NOI18N
        VIEW("View"), // NOI18N
        VIEW_CELL("Cell"), // NOI18N
        WEBROOT(""), // NOI18N
        UNKNOWN(""); // NOI18N
        private final String suffix;

        private Category(String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }

    }

    public enum Base {

        APP,
        PLUGIN,
        VENDOR,
        CORE,
        UNKNOWN
    }

    private final CakePHP3ModuleImpl impl;

    CakePHP3Module(CakePHP3ModuleImpl impl) {
        this.impl = impl;
    }

    public static boolean isCakePHP(PhpModule phpmodule) {
        return CakePHP3FrameworkProvider.getInstance().isInPhpModule(phpmodule);
    }

    public static CakePHP3Module forPhpModule(PhpModule phpModule) {
        if (phpModule == null) {
            phpModule = PhpModule.Factory.inferPhpModule();
        }
        if (phpModule == null || !isCakePHP(phpModule)) {
            return DUMMY_MODULE;
        }
        CakePHP3ModuleFactory factory = CakePHP3ModuleFactory.getInstance();
        return factory.create(phpModule);
    }

    public static CakePHP3Module forFileObject(FileObject fileObject) {
        if (fileObject == null) {
            return DUMMY_MODULE;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);
        return forPhpModule(phpModule);
    }

    public boolean isTemplateFile(FileObject fo) {
        return impl.isTemplateFile(fo);
    }

    public List<FileObject> getDirectories(Base base) {
        return impl.getDirectories(base);
    }

    public List<FileObject> getDirectories(Base base, Category category, String pluginName) {
        if (base == Base.PLUGIN && StringUtils.isEmpty(pluginName)) {
            return Collections.emptyList();
        }
        return impl.getDirectories(base, category, pluginName);
    }

    public FileObject getFile(Base base, Category category, String relativePath, String pluginName) {
        return impl.getFile(base, category, relativePath, pluginName);
    }

    public String getCtpExt() {
        return impl.getCtpExt();
    }

    public String getNameSpace() {
        return impl.getAppNamespace();
    }

    public Base getBase(FileObject fileObject) {
        return impl.getBase(fileObject);
    }

    public Category getCategory(FileObject fileObject) {
        return impl.getCategory(fileObject);
    }

    @CheckForNull
    public FileObject getController(FileObject template) {
        return impl.getController(template);
    }

    public FileObject getController(FileObject template, boolean forceApp) {
        return impl.getController(template, forceApp);
    }

    @CheckForNull
    public FileObject getViewCell(FileObject template) {
        return impl.getViewCell(template);
    }

    @CheckForNull
    public FileObject getTemplate(String relativePath, FileObject controller, String themeName) {
        return impl.getTemplate(relativePath, controller, themeName);
    }

    public FileObject getEntity(FileObject table) {
        return impl.getEntity(table);
    }

    public Set<String> getAllPluginNames() {
        return impl.getAllPluginNames();
    }

    public String getPluginName(FileObject fileObject) {
        return impl.getPluginName(fileObject);
    }

    public String toPhpFileName(Category category, String name) {
        return impl.toPhpFileName(category, name);
    }

    public String getNamespace(FileObject fileObject) {
        return impl.getNamespace(fileObject);
    }

    public ModuleInfo createModuleInfo(FileObject fileObject) {
        return impl.createModuleInfo(fileObject);
    }

}
