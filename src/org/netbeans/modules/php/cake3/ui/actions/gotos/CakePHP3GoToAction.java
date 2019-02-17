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
package org.netbeans.modules.php.cake3.ui.actions.gotos;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.progress.BaseProgressUtils;
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

/**
 *
 * @author junichi11
 */
public abstract class CakePHP3GoToAction extends BaseAction {

    private static final long serialVersionUID = 7403447287856842348L;

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
        final AtomicBoolean cancel = new AtomicBoolean();
        CakePHP3GoToStatusFactory factory = CakePHP3GoToStatusFactory.getInstance();
        final CakePHP3GoToStatus status = factory.create(fileObject, textComponent.getCaretPosition());
        final List<GoToItem> defaultItems = new ArrayList<>();
        BaseProgressUtils.runOffEventDispatchThread(new Runnable() {

            @Override
            public void run() {
                status.scan();
                defaultItems.addAll(getGoToItems(status));
            }
        }, "CakePHP3 Go To", cancel, false);

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

    protected String getPopupTitle() {
        return "Go To"; // NOI18N
    }

    protected abstract List<GoToItem> getGoToItems(CakePHP3GoToStatus status);

}
