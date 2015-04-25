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
package org.netbeans.modules.php.cake3.ui.actions.gotos;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseAction;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.ui.GoToPopup;
import org.netbeans.modules.php.cake3.ui.PopupUtil;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatus;
import org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatusFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public abstract class CakePHP3GoToAction extends BaseAction {

    private static final long serialVersionUID = 7403447287856842348L;
    private static final RequestProcessor RP = new RequestProcessor(CakePHP3GoToAction.class); // NOI18N

    @Override
    public final void actionPerformed(ActionEvent event, final JTextComponent textComponent) {
        Document document = textComponent.getDocument();
        if (document == null) {
            return;
        }
        final FileObject fileObject = NbEditorUtilities.getFileObject(document);
        if (fileObject == null) {
            return;
        }
        PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);
        if (phpModule == null) {
            return;
        }
        if (!CakePHP3Module.isCakePHP(phpModule)) {
            return;
        }
        // only php files
        String mimeType = fileObject.getMIMEType(FileUtils.PHP_MIME_TYPE); // NOI18N
        if (!FileUtils.PHP_MIME_TYPE.equals(mimeType)) {
            return;
        }
        RP.execute(new Runnable() {

            @Override
            public void run() {
                CakePHP3GoToStatusFactory factory = CakePHP3GoToStatusFactory.getInstance();
                final CakePHP3GoToStatus status = factory.create(fileObject, textComponent.getCaretPosition());
                status.scan();
                final List<GoToItem> defaultItems = getGoToItems(status);

                // show popup
                try {
                    Rectangle rectangle = textComponent.modelToView(textComponent.getCaretPosition());
                    final Point point = new Point(rectangle.x, rectangle.y + rectangle.height);
                    SwingUtilities.convertPointToScreen(point, textComponent);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String title = getPopupTitle();
                            if (title == null) {
                                title = ""; // NOI18N
                            }
                            PopupUtil.showPopup(new GoToPopup(title, defaultItems, status), title, point.x, point.y, true, 0);
                        }
                    });
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    protected String getPopupTitle() {
        return "Go To"; // NOI18N
    }

    protected abstract List<GoToItem> getGoToItems(CakePHP3GoToStatus status);

}
