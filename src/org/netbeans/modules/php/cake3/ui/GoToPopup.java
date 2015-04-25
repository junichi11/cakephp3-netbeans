/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.php.cake3.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatus;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * This file is originally from Retouche, the Java Support infrastructure in
 * NetBeans. I have modified the file as little as possible to make merging
 * Retouche fixes back as simple as possible.
 *
 * (This used to be IsOverriddenPopup in
 * org.netbeans.modules.java.editor.overridden)
 *
 *
 * @author Jan Lahoda
 * @author Tor Norbye
 * @author junichi11
 */
public class GoToPopup extends JPanel implements FocusListener {

    private static final long serialVersionUID = -5093147329704539371L;

    private final String caption;
    private final List<GoToItem> items;
    private CakePHP3GoToStatus status;

    /**
     * Creates new form GoToPopup
     */
    public GoToPopup(String caption, List<GoToItem> items) {
        this.caption = caption;
        this.items = items;

        initComponents();

        jList1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addFocusListener(this);
        init();
    }

    public GoToPopup(String caption, List<GoToItem> items, CakePHP3GoToStatus status) {
        this.caption = caption;
        this.items = new ArrayList<>(items);
        this.status = status;

        initComponents();

        jList1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addFocusListener(this);
        init();
    }

    private void init() {
        Document document = filterTextField.getDocument();
        DefaultDocumentListener documentListener = new DefaultDocumentListener();
        document.addDocumentListener(documentListener);
    }

    private void fireChange() {
        final DefaultListModel<GoToItem> model = (DefaultListModel<GoToItem>) jList1.getModel();
        model.clear();
        String filter = getFilter();
        for (GoToItem item : items) {
            FileObject fileObject = item.getFileObject();
            if (fileObject == null) {
                continue;
            }
            if (fileObject.getNameExt().toLowerCase().contains(filter.toLowerCase())) {
                model.addElement(item);
            }
        }
        if (!model.isEmpty()) {
            jList1.setSelectedIndex(0);
        }
        jList1.setSize(jList1.getPreferredSize());
        jList1.setVisibleRowCount(model.getSize());
        firePropertyChange(PopupUtil.COMPONENT_SIZE_CHANGED, null, null);
    }

    private String getFilter() {
        return filterTextField.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<GoToItem>();
        filterTextField = new javax.swing.JTextField();

        setFocusCycleRoot(true);
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(caption
        );
        jLabel1.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jLabel1, gridBagConstraints);

        jList1.setModel(createListModel());
        jList1.setCellRenderer(new RendererImpl());
        jList1.setSelectedIndex(0);
        jList1.setVisibleRowCount(items.size()
        );
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(filterTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 1) {
            openSelected();
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.getModifiers() == 0) {
            openSelected();
        }

        // change items
        if (evt.isControlDown() && evt.isShiftDown() || evt.isMetaDown() && evt.isShiftDown()) {
            if (status == null) {
                return;
            }
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_A:
                    items.clear();
                    items.addAll(status.getAllItems());
                    break;
                case KeyEvent.VK_B:
                    items.clear();
                    items.addAll(status.getAllBehaviors());
                    break;
                case KeyEvent.VK_C:
                    items.clear();
                    items.addAll(status.getAllControllers());
                    break;
                case KeyEvent.VK_E:
                    items.clear();
                    items.addAll(status.getAllEntities());
                    break;
                case KeyEvent.VK_F:
                    items.clear();
                    items.addAll(status.getAllFixtures());
                    break;
                case KeyEvent.VK_H:
                    items.clear();
                    items.addAll(status.getAllHelpers());
                    break;
                case KeyEvent.VK_I:
                    items.clear();
                    items.addAll(status.getImportants());
                    break;
                case KeyEvent.VK_L:
                    items.clear();
                    items.addAll(status.getAllViewCells());
                    break;
                case KeyEvent.VK_P:
                    items.clear();
                    items.addAll(status.getAllComponents());
                    break;
                case KeyEvent.VK_M:
                    items.clear();
                    items.addAll(status.getAllTables());
                    break;
                case KeyEvent.VK_T:
                    items.clear();
                    items.addAll(status.getAllTestCases());
                    break;
                default:
                    return;
            }
            fireChange();
            return;
        }
        if (evt.isControlDown() || evt.isMetaDown()) {
            if (status == null) {
                return;
            }
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_B:
                    items.clear();
                    items.addAll(status.getBehaviors());
                    break;
                case KeyEvent.VK_C:
                    items.clear();
                    items.addAll(status.getControllers());
                    break;
                case KeyEvent.VK_E:
                    items.clear();
                    items.addAll(status.getEntities());
                    break;
                case KeyEvent.VK_F:
                    items.clear();
                    items.addAll(status.getFixtures());
                    break;
                case KeyEvent.VK_H:
                    items.clear();
                    items.addAll(status.getHelpers());
                    break;
                case KeyEvent.VK_I:
                    items.clear();
                    items.addAll(status.getImportants());
                    break;
                case KeyEvent.VK_L:
                    items.clear();
                    items.addAll(status.getViewCells());
                    break;
                case KeyEvent.VK_P:
                    items.clear();
                    items.addAll(status.getComponents());
                    break;
                case KeyEvent.VK_S:
                    items.clear();
                    items.addAll(status.getSmart());
                    break;
                case KeyEvent.VK_M:
                    items.clear();
                    items.addAll(status.getTables());
                    break;
                case KeyEvent.VK_T:
                    items.clear();
                    items.addAll(status.getTestCases());
                    break;
                case KeyEvent.VK_V:
                    items.clear();
                    items.addAll(status.getTemplates());
                    break;
                default:
                    return;
            }
            fireChange();
            return;
        }

        String text = filterTextField.getText();
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (text.length() > 0) {
                filterTextField.setText(text.substring(0, text.length() - 1));
            }
            return;
        }
        char keyChar = evt.getKeyChar();
        if (keyChar != KeyEvent.CHAR_UNDEFINED) {
            filterTextField.setText(text + keyChar);
        }
    }//GEN-LAST:event_jList1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<GoToItem> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private void openSelected() {
        GoToItem item = jList1.getSelectedValue();
        if (item == null) {
            return;
        }
        FileObject fileObject = item.getFileObject();
        int offset = item.getOffset();
        if (fileObject != null && offset >= 0) {
            if (offset == 0) {
                try {
                    DataObject dataObject = DataObject.find(fileObject);
                    EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
                    ec.open();
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                UiUtils.open(fileObject, offset);
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

        PopupUtil.hidePopup();
    }

    private ListModel<GoToItem> createListModel() {
        DefaultListModel<GoToItem> dlm = new DefaultListModel<>();

        for (GoToItem el : items) {
            dlm.addElement(el);
        }

        return dlm;
    }

    private static class RendererImpl extends DefaultListCellRenderer {

        private static final long serialVersionUID = 5408637835559113711L;

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof GoToItem) {
                GoToItem item = (GoToItem) value;
                c.setIcon(item.getIcon());
            }
            return c;
        }
    }

    @Override
    public void focusGained(FocusEvent arg0) {
        jList1.requestFocus();
        jList1.requestFocusInWindow();
    }

    @Override
    public void focusLost(FocusEvent arg0) {
    }

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
