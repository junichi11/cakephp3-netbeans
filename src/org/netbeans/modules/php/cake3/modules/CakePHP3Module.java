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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.CakePHP3FrameworkProvider;
import org.netbeans.modules.php.cake3.CakeVersion;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import static org.netbeans.modules.php.cake3.modules.CakePHP3ModuleFactory.DUMMY_MODULE;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class CakePHP3Module {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static String PROPERTY_CHANGE_CAKE3 = "property-change-cake3"; // NOI18N

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
    private final CakeVersion version;

    CakePHP3Module(CakePHP3ModuleImpl impl, CakeVersion version) {
        this.impl = impl;
        this.version = version;
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

    public CakeVersion getVersion() {
        return version;
    }

    @CheckForNull
    public Dotcake getDotcake() {
        return impl.getDotcake();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void notifyPropertyChanged(PropertyChangeEvent event) {
        if (PROPERTY_CHANGE_CAKE3.equals(event.getPropertyName())) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    refreshNodes();
//                    reset();
                }
            });
        }
    }

//    void reset() {
//        CakePHP3ModuleFactory.getInstance().reset(this);
//    }
    void refreshNodes() {
        propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_CAKE3, null, null);
    }

}
