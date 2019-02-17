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

import java.awt.Component;
import org.netbeans.core.multitabs.TabDecorator;
import org.netbeans.swing.tabcontrol.TabData;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = TabDecorator.class, position = 1000)
public class CakePHPTabDecorator extends TabDecorator {

    @Override
    public String getText(TabData tab) {
        // show a parent directory name if it's a view file
        // e.g. home.ctp [Pages]
        String text = tab.getText();
        if (text.endsWith(".ctp")) { // NOI18N
            Component component = tab.getComponent();
            if (component instanceof TopComponent) {
                TopComponent topComponent = (TopComponent) component;
                Lookup lookup = topComponent.getLookup();
                if (lookup != null) {
                    FileObject fileObject = lookup.lookup(FileObject.class);
                    if (fileObject != null) {
                    FileObject parent = fileObject.getParent();
                        if (parent != null) {
                            String parentName = parent.getName();
                            return String.format("%s [%s]", text, parentName); // NOI18N
                        }
                    }
                }
            }
        }
        return super.getText(tab);
    }

}
