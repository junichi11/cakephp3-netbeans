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
package org.netbeans.modules.php.cake3.ui.logicalview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.options.CakePHP3Options;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
@NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 600)
public class MVCNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project p) {
        PhpModule phpModule = PhpModule.Factory.lookupPhpModule(p);
        return new MVCNodeList(phpModule);
    }

    private static class MVCNodeList implements NodeList<Node>, PropertyChangeListener {

        private final PhpModule phpModule;
        private static final Logger LOGGER = Logger.getLogger(MVCNodeList.class.getName());
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        public MVCNodeList(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public List<Node> keys() {
            if (!CakePHP3Module.isCakePHP(phpModule)) {
                return Collections.emptyList();
            }
            CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
            if (cakeModule == null) {
                return Collections.emptyList();
            }

            List<Node> list = new ArrayList<>();
            for (Object object : getAvailableCustomNodeList()) {
                List<FileObject> rootDirectories = Collections.emptyList();
                if (object instanceof Category) {
                    rootDirectories = cakeModule.getDirectories(Base.APP, (Category) object, null);
                } else if (object == Base.PLUGIN) {
                    List<FileObject> appDirectories = cakeModule.getDirectories(Base.APP);
                    if (!appDirectories.isEmpty()) {
                        FileObject pluginsDirectory = appDirectories.get(0).getFileObject("plugins"); // NOI18N
                        if (pluginsDirectory != null) {
                            rootDirectories = Arrays.asList(pluginsDirectory);
                        }
                    }
                }
                if (rootDirectories.isEmpty()) {
                    continue;
                }
                FileObject rootDirectory = rootDirectories.get(0);
                DataFolder folder = getFolder(rootDirectory);
                if (folder != null) {
                    list.add(new MVCNode(folder, null, rootDirectory.getName()));
                }
            }
            return list;
        }

        private List<Object> getAvailableCustomNodeList() {
            CakePHP3Options options = CakePHP3Options.getInstance();
            List<Object> list = new ArrayList<>();
            for (String customNode : options.getAvailableCustomNodes()) {
                switch (customNode) {
                    case "Console": // NOI18N
                        list.add(Category.CONSOLE);
                        break;
                    case "Controller": // NOI18N
                        list.add(Category.CONTROLLER);
                        break;
                    case "Component": // NOI18N
                        list.add(Category.COMPONENT);
                        break;
                    case "View": // NOI18N
                        list.add(Category.VIEW);
                        break;
                    case "Model": // NOI18N
                        list.add(Category.MODEL);
                        break;
                    case "webroot": // NOI18N
                        list.add(Category.WEBROOT);
                        break;
                    case "Helper": // NOI18N
                        list.add(Category.HELPER);
                        break;
                    case "Element": // NOI18N
                        list.add(Category.ELEMENT);
                        break;
                    case "Entity": // NOI18N
                        list.add(Category.ENTITY);
                        break;
                    case "Shell": // NOI18N
                        list.add(Category.SHELL);
                        break;
                    case "Table": // NOI18N
                        list.add(Category.TABLE);
                        break;
                    case "Template": // NOI18N
                        list.add(Category.TEMPLATE);
                        break;
                    case "app/plugins": // NOI18N
                        // XXX this is not proper
                        list.add(Base.PLUGIN);
                        break;
                    default:
                        break;
                }
            }
            return list;
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            changeSupport.removeChangeListener(l);
        }

        void fireChange() {
            changeSupport.fireChange();
        }

        @Override
        public Node node(Node node) {
            return node;
        }

        private DataFolder getFolder(FileObject fileObject) {
            if (fileObject != null && fileObject.isValid()) {
                try {
                    DataFolder dataFolder = DataFolder.findFolder(fileObject);
                    return dataFolder;
                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, null, ex);
                }
            }
            return null;
        }

        @Override
        public void addNotify() {
            CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
            if (cakeModule != null) {
                cakeModule.addPropertyChangeListener(this);
            }
        }

        @Override
        public void removeNotify() {
            CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
            if (cakeModule != null) {
                cakeModule.removePropertyChangeListener(this);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (CakePHP3Module.PROPERTY_CHANGE_CAKE3.equals(evt.getPropertyName())) {
                fireChange();
            }
        }
    }
}
