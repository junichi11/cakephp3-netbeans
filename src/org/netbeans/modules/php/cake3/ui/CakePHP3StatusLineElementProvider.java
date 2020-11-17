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
package org.netbeans.modules.php.cake3.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.modules.php.cake3.CakeVersion;
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class CakePHP3StatusLineElementProvider implements StatusLineElementProvider {

    private static final Icon CAKE_ICON = ImageUtilities.loadImageIcon(CakePHP3Constants.CAKE_ICON_16, true);
    private final JLabel versionLabel = new JLabel(""); // NOI18N
    private final Lookup.Result<FileObject> result;
    private PhpModule phpModule = null;

    public CakePHP3StatusLineElementProvider() {
        // add lookup listener
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());
    }

    @Override
    public Component getStatusLineElement() {
        // XXX add a debug level?
        JLabel cell = new JLabel();
        return panelWithSeparator(cell);
    }

    /**
     * Create Component(JPanel) and add separator and JLabel to it.
     *
     * @param cell JLabel
     * @return panel
     */
    private Component panelWithSeparator(JLabel cell) {
        // create separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL) {
            private static final long serialVersionUID = -6385848933295984637L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(3, 3);
            }
        };
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // create panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(cell, BorderLayout.EAST);
        panel.add(versionLabel, BorderLayout.CENTER);
        return panel;
    }

    private void setVersion(final String version) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                versionLabel.setText(version);
                versionLabel.setIcon(CAKE_ICON);
            }
        };
        if (CakeVersion.UNNKOWN.equals(version)) {
            clearLabel();
            return;
        }

        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Clear label
     */
    private void clearLabel() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                versionLabel.setText(""); //NOI18N
                versionLabel.setIcon(null);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private class LookupListenerImpl implements LookupListener {

        public LookupListenerImpl() {
        }

        @Override
        public void resultChanged(LookupEvent ev) {
            // get FileObject
            FileObject fileObject = getFileObject(ev);
            if (fileObject == null) {
                clearLabel();
                return;
            }
            PhpModule currentPhpModule = PhpModule.Factory.forFileObject(fileObject);
            if (currentPhpModule == null) {
                clearLabel();
                phpModule = null;
            }
            if (phpModule == currentPhpModule) {
                return;
            }
            phpModule = currentPhpModule;
            if (!CakePHPModule.isCakePHP(phpModule)) {
                clearLabel();
                return;
            }
            CakePHPModule cakeModule = CakePHPModule.forPhpModule(phpModule);
            CakeVersion version = cakeModule.getVersion();
            setVersion(version.getVersionNumber());

        }

        /**
         * Get FileObject
         *
         * @param lookupEvent
         * @return current FileObject if exists, otherwise null
         */
        private FileObject getFileObject(LookupEvent lookupEvent) {
            Lookup.Result<?> lookupResult = (Lookup.Result<?>) lookupEvent.getSource();
            Collection<?> c = (Collection<?>) lookupResult.allInstances();
            FileObject fileObject = null;
            if (!c.isEmpty()) {
                fileObject = (FileObject) c.iterator().next();
            }
            return fileObject;
        }
    }
}
