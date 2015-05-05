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
package org.netbeans.modules.php.cake3.ui.customizer;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
public class CakePHP3CustomizerPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -4175785695400750924L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form CakePHP3CustomizerPanel
     */
    public CakePHP3CustomizerPanel() {
        initComponents();
        init();
    }

    private void init() {
        // add DocumentListener
        DefaultDocumentListener documentListener = new DefaultDocumentListener();
        rootTextField.getDocument().addDocumentListener(documentListener);
        srcTextField.getDocument().addDocumentListener(documentListener);
        wwwRootTextField.getDocument().addDocumentListener(documentListener);
        cssTextField.getDocument().addDocumentListener(documentListener);
        imgTextField.getDocument().addDocumentListener(documentListener);
        jsTextField.getDocument().addDocumentListener(documentListener);
        dotcakeTextField.getDocument().addDocumentListener(documentListener);
    }

    public void setCakePHP3Enabled(boolean isEnabled) {
        enabledCheckBox.setSelected(isEnabled);
    }

    public boolean isCakePHP3Enabled() {
        return enabledCheckBox.isSelected();
    }

    public String getNamespace() {
        return namespaceTextField.getText().trim();
    }

    public void setNameSpace(String namespace) {
        namespaceTextField.setText(namespace);
    }

    public String getRoot() {
        return rootTextField.getText().trim();
    }

    public void setRoot(String root) {
        rootTextField.setText(root);
    }

    public String getSrc() {
        return srcTextField.getText().trim();
    }

    public void setSrc(String src) {
        srcTextField.setText(src);
    }

    public String getWWWRoot() {
        return wwwRootTextField.getText().trim();
    }

    public void setWWWRoot(String wwwRoot) {
        wwwRootTextField.setText(wwwRoot);
    }

    public String getCss() {
        return cssTextField.getText().trim();
    }

    public void setCss(String css) {
        cssTextField.setText(css);
    }

    public String getJs() {
        return jsTextField.getText().trim();
    }

    public void setJs(String js) {
        jsTextField.setText(js);
    }

    public String getImg() {
        return imgTextField.getText().trim();
    }

    public void setImg(String img) {
        imgTextField.setText(img);
    }

    public String getDotcakePath() {
        return dotcakeTextField.getText().trim();
    }

    public void setDotcakePath(String path) {
        dotcakeTextField.setText(path);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        generalLabel = new javax.swing.JLabel();
        enabledCheckBox = new javax.swing.JCheckBox();
        enabledMessageLabel = new javax.swing.JLabel();
        pathSettingsLabel = new javax.swing.JLabel();
        wwwRootLabel = new javax.swing.JLabel();
        wwwRootTextField = new javax.swing.JTextField();
        cssLabel = new javax.swing.JLabel();
        cssTextField = new javax.swing.JTextField();
        jsLabel = new javax.swing.JLabel();
        jsTextField = new javax.swing.JTextField();
        imgLabel = new javax.swing.JLabel();
        imgTextField = new javax.swing.JTextField();
        rootLabel = new javax.swing.JLabel();
        rootTextField = new javax.swing.JTextField();
        srcLabel = new javax.swing.JLabel();
        srcTextField = new javax.swing.JTextField();
        namespaceLabel = new javax.swing.JLabel();
        namespaceTextField = new javax.swing.JTextField();
        dotcakeLabel = new javax.swing.JLabel();
        dotcakeTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(generalLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.generalLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(enabledCheckBox, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.enabledCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(enabledMessageLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.enabledMessageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pathSettingsLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.pathSettingsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wwwRootLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.wwwRootLabel.text")); // NOI18N

        wwwRootTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.wwwRootTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cssLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.cssLabel.text")); // NOI18N

        cssTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.cssTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jsLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.jsLabel.text")); // NOI18N

        jsTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.jsTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(imgLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.imgLabel.text")); // NOI18N

        imgTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.imgTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(rootLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.rootLabel.text")); // NOI18N

        rootTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.rootTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(srcLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.srcLabel.text")); // NOI18N

        srcTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.srcTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(namespaceLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.namespaceLabel.text")); // NOI18N

        namespaceTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.namespaceTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dotcakeLabel, org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.dotcakeLabel.text")); // NOI18N

        dotcakeTextField.setText(org.openide.util.NbBundle.getMessage(CakePHP3CustomizerPanel.class, "CakePHP3CustomizerPanel.dotcakeTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(enabledCheckBox))
                            .addComponent(generalLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(enabledMessageLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pathSettingsLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(wwwRootLabel)
                                            .addComponent(rootLabel)
                                            .addComponent(srcLabel)))
                                    .addComponent(namespaceLabel)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cssLabel)
                                    .addComponent(imgLabel)
                                    .addComponent(jsLabel))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wwwRootTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                            .addComponent(cssTextField)
                            .addComponent(rootTextField)
                            .addComponent(srcTextField)
                            .addComponent(namespaceTextField)
                            .addComponent(imgTextField)
                            .addComponent(jsTextField)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dotcakeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dotcakeTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generalLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enabledCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enabledMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namespaceLabel)
                    .addComponent(namespaceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pathSettingsLabel)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rootLabel)
                    .addComponent(rootTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(srcLabel)
                    .addComponent(srcTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wwwRootLabel)
                    .addComponent(wwwRootTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cssTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cssLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imgLabel)
                    .addComponent(imgTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jsLabel)
                    .addComponent(jsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dotcakeLabel)
                    .addComponent(dotcakeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cssLabel;
    private javax.swing.JTextField cssTextField;
    private javax.swing.JLabel dotcakeLabel;
    private javax.swing.JTextField dotcakeTextField;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JLabel enabledMessageLabel;
    private javax.swing.JLabel generalLabel;
    private javax.swing.JLabel imgLabel;
    private javax.swing.JTextField imgTextField;
    private javax.swing.JLabel jsLabel;
    private javax.swing.JTextField jsTextField;
    private javax.swing.JLabel namespaceLabel;
    private javax.swing.JTextField namespaceTextField;
    private javax.swing.JLabel pathSettingsLabel;
    private javax.swing.JLabel rootLabel;
    private javax.swing.JTextField rootTextField;
    private javax.swing.JLabel srcLabel;
    private javax.swing.JTextField srcTextField;
    private javax.swing.JLabel wwwRootLabel;
    private javax.swing.JTextField wwwRootTextField;
    // End of variables declaration//GEN-END:variables

    //~ Inner class
    private class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }

    }
}
