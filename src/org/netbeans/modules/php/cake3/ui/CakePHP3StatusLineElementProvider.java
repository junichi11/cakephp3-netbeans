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
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
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

    private void setVersion(String version) {
        assert SwingUtilities.isEventDispatchThread();
        if (CakeVersion.UNNKOWN.equals(version)) {
            clearLabel();
        } else {
            versionLabel.setText(version);
            versionLabel.setIcon(CAKE_ICON);
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
            if (!CakePHP3Module.isCakePHP(phpModule)) {
                clearLabel();
                return;
            }
            CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
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
