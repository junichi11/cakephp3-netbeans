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
package org.netbeans.modules.php.cake3.editor;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.DataLoadersBridge;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.modules.ModuleUtils;
import org.netbeans.modules.php.cake3.options.CakePHP3Options;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.ExternalDropHandler;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = ExternalDropHandler.class, position = 450)
public class CakePHPExternalDropHandler extends ExternalDropHandler {

    private static final Logger LOGGER = Logger.getLogger(CakePHPExternalDropHandler.class.getName());
    private static DataFlavor uriListDataFlavor;
    private boolean canDrop;

    @Override
    public boolean canDrop(DropTargetDragEvent event) {
        JEditorPane editorPane = findPane(event.getDropTargetContext().getComponent());
        if (editorPane == null || !isInCakePHP(editorPane)) {
            return false;
        }
        Transferable t = event.getTransferable();
        canDrop = canDrop(t);
        if (!canDrop) {
            return false;
        }

        editorPane.setCaretPosition(getOffset(editorPane, event.getLocation()));
        editorPane.requestFocusInWindow(); //pity we need to call this all the time when dragging, but  ExternalDropHandler don't handle dragEnter event
        return canDrop(event.getCurrentDataFlavors());
    }

    @Override
    public boolean canDrop(DropTargetDropEvent event) {
        if (!canDrop) {
            return false;
        }
        JEditorPane editorPane = findPane(event.getDropTargetContext().getComponent());
        if (editorPane == null || !isInCakePHP(editorPane)) {
            return false;
        }
        return canDrop(event.getCurrentDataFlavors());
    }

    private boolean canDrop(Transferable t) {
        if (!CakePHP3Options.getInstance().isExternalDragAndDrop()) {
            return false;
        }
        if (null == t) {
            return false;
        }
        List<File> fileList = getFileList(t);
        if ((fileList == null) || fileList.isEmpty()) {
            return false;
        }
        //handle just the first file
        File file = fileList.get(0);
        FileObject target = FileUtil.toFileObject(file);
        if (file.isDirectory() || !isAvailableMimeType(target) || !isInCakePHP(target)) {
            return false;
        }
        return true;
    }

    private boolean canDrop(DataFlavor[] flavors) {
        for (int i = 0; null != flavors && i < flavors.length; i++) {
            if (DataFlavor.javaFileListFlavor.equals(flavors[i])
                    || getUriListDataFlavor().equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    private int getOffset(JEditorPane pane, Point location) {
        return pane.getUI().viewToModel(pane, location);
    }

    @Override
    public boolean handleDrop(DropTargetDropEvent event) {
        Transferable t = event.getTransferable();
        if (null == t) {
            return false;
        }
        List<File> fileList = getFileList(t);
        if ((fileList == null) || fileList.isEmpty()) {
            return false;
        }
        //handle just the first file
        File file = fileList.get(0);
        FileObject target = FileUtil.toFileObject(file);
        if (file.isDirectory()) {
            return true; //as we previously claimed we canDrop() it so we need to say we've handled it even if did nothing.
        }
        JEditorPane pane = findPane(event.getDropTargetContext().getComponent());
        if (pane == null) {
            return false;
        }
        final BaseDocument document = (BaseDocument) pane.getDocument();
        FileObject current = DataLoadersBridge.getDefault().getFileObject(document);
        PhpModule phpModule = PhpModule.Factory.forFileObject(current);
        if (phpModule == null) {
            return true;
        }
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);

        final StringBuilder sb = new StringBuilder();

        String mimeType = target.getMIMEType();
        if ("content/unknown".equals(mimeType)) { //NOI18N
            return true;
        }

        // plugin
        CakePHP3Module.Base base = cakeModule.getBase(target);
        String pluginName = ""; // NOI18N
        if (base == CakePHP3Module.Base.PLUGIN) {
            pluginName = cakeModule.getPluginName(target);
        }
        String relativePath;
        switch (mimeType) {
            case "text/css": // NOI18N
                List<FileObject> csss = cakeModule.getDirectories(base, Category.CSS, pluginName);
                relativePath = getRelativePath(csss, target);
                relativePath = ModuleUtils.appendPluignName(pluginName, relativePath);
                sb.append("<?= $this->Html->css('").append(relativePath).append("') ?>"); // NOI18N
                break;
            case "text/javascript": // NOI18N
                List<FileObject> jss = cakeModule.getDirectories(base, Category.JS, pluginName);
                relativePath = getRelativePath(jss, target);
                relativePath = ModuleUtils.appendPluignName(pluginName, relativePath);
                sb.append("<?= $this->Html->js('").append(relativePath).append("') ?>"); // NOI18N
                break;
            case "image/png":
            case "image/jpeg":
            case "image/gif":
                List<FileObject> imgs = cakeModule.getDirectories(base, Category.IMG, pluginName);
                relativePath = getRelativePath(imgs, target);
                relativePath = ModuleUtils.appendPluignName(pluginName, relativePath);
                // $this->Html->image('something.png', ['alt' => '']);
                sb.append("<?= $this->Html->image('").append(relativePath).append("', ['alt' => '']) ?>"); // NOI18N
                break;
            case "text/x-php5": // NOI18N
                // TODO add the others?
                Category category = cakeModule.getCategory(target);
                String commonName = ModuleUtils.toCommonName(target.getName(), category);
                String name = ModuleUtils.appendPluignName(pluginName, commonName);
                switch (category) {
                    case COMPONENT:
                        sb.append("$this->loadComponent('").append(name).append("');"); // NOI18N
                        break;
                    case TABLE:
                        // XXX use TableRegistry?
                        sb.append("$this->loadModel('").append(name).append("');"); // NOI18N
                        break;
                    default:
                        return true;
                }
                break;
            default:
                return true;
        }

        final int offset = getOffset(pane, event.getLocation());
        if (!(document instanceof StyledDocument)) {
            return true;
        }
        try {
            NbDocument.runAtomicAsUser((StyledDocument) document, new Runnable() {

                @Override
                public void run() {
                    try {
                        document.insertString(offset, sb.toString(), null);
                    } catch (BadLocationException ex) {
                        // ignore
                    }
                }
            });
        } catch (BadLocationException ex) {
            // ignore
        }

        return true;
    }

    private String getRelativePath(List<FileObject> directories, FileObject target) {
        String relativePath = ""; // NOI18N
        for (FileObject directory : directories) {
            String targetPath = target.getPath();
            String directoryPath = directory.getPath();
            if (targetPath.startsWith(directoryPath)) {
                relativePath = targetPath.replace(directoryPath + "/", ""); // NOI18N
                break;
            }
        }
        return relativePath;
    }

    private JEditorPane findPane(Component component) {
        while (component != null) {
            if (component instanceof JEditorPane) {
                return (JEditorPane) component;
            }
            component = component.getParent();
        }
        return null;
    }

    private boolean isAvailableMimeType(FileObject fileObject) {
        String mimeType = fileObject.getMIMEType();
        switch (mimeType) {
            case "text/css": // NOI18N
            case "text/javascript": // NOI18N
            case "image/png": // NOI18N
            case "image/jpeg": // NOI18N
            case "image/gif": // NOI18N
            case "text/x-php5": // NOI18N
                return true;
            default:
                return false;
        }
    }

    private boolean isInCakePHP(JEditorPane pane) {
        Document document = pane.getDocument();
        if (document == null) {
            return false;
        }
        FileObject fileObject = NbEditorUtilities.getFileObject(document);
        return isInCakePHP(fileObject);
    }

    private boolean isInCakePHP(FileObject fileObject) {
        if (fileObject == null) {
            return false;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);
        if (phpModule == null) {
            return false;
        }
        return CakePHP3Module.isCakePHP(phpModule);
    }

    //copied from org.netbeans.modules.openfile.DefaultExternalDropHandler
    private List<File> getFileList(Transferable t) {
        try {
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                //windows & mac
                try {
                    return (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                } catch (InvalidDnDOperationException ex) { // #212390
                    LOGGER.log(Level.FINE, null, ex);
                }
            }
            if (t.isDataFlavorSupported(getUriListDataFlavor())) {
                //linux
                String uriList = (String) t.getTransferData(getUriListDataFlavor());
                return textURIListToFileList(uriList);
            }
        } catch (UnsupportedFlavorException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        } catch (IOException ex) {
            // Ignore. Can be just "Owner timed out" from sun.awt.X11.XSelection.getData.
            LOGGER.log(Level.FINE, null, ex);
        }
        return null;
    }

    //copied from org.netbeans.modules.openfile.DefaultExternalDropHandler
    private DataFlavor getUriListDataFlavor() {
        if (null == uriListDataFlavor) {
            try {
                uriListDataFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
            } catch (ClassNotFoundException cnfE) {
                //cannot happen
                throw new AssertionError(cnfE);
            }
        }
        return uriListDataFlavor;
    }

    //copied from org.netbeans.modules.openfile.DefaultExternalDropHandler
    private List<File> textURIListToFileList(String data) {
        List<File> list = new ArrayList<>(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n\u0000");
                st.hasMoreTokens();) {
            String s = st.nextToken();
            if (s.startsWith("#")) {
                // the line is a comment (as per the RFC 2483)
                continue;
            }
            try {
                URI uri = new URI(s);
                File file = org.openide.util.Utilities.toFile(uri);
                list.add(file);
            } catch (URISyntaxException | IllegalArgumentException e) {
                // malformed URI
            }
        }
        return list;
    }

}
