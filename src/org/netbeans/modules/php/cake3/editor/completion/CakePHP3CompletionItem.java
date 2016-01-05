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
package org.netbeans.modules.php.cake3.editor.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author junichi11
 */
public class CakePHP3CompletionItem implements CompletionItem {

    private final String text;
    private final String filter;
    private final int startOffset;
    private static final ImageIcon ICON = ImageUtilities.loadImageIcon(CakePHP3Constants.CAKE_ICON_16, true);
    private static final Logger LOGGER = Logger.getLogger(CakePHP3CompletionItem.class.getName());

    public CakePHP3CompletionItem(String text, String filter, int startOffset) {
        this.text = text;
        this.filter = filter;
        this.startOffset = startOffset;
    }

    public String getText() {
        return text;
    }

    public String getFilter() {
        return filter;
    }

    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            final StyledDocument doc = (StyledDocument) jtc.getDocument();
            NbDocument.runAtomicAsUser(doc, new Runnable() {

                @Override
                public void run() {
                    try {
                        String insertString;
                        if (text.startsWith(filter)) {
                            insertString = text.replaceFirst(filter, ""); // NOI18N
                            doc.insertString(startOffset, insertString, null);
                        } else {
                            insertString = text;
                            int removeLength = filter.length();
                            int removeStart = startOffset - removeLength;
                            if (removeStart >= 0) {
                                doc.remove(removeStart, removeLength);
                                doc.insertString(removeStart, insertString, null);
                            } else {
                                LOGGER.log(Level.WARNING, "Invalid start position[text: {0}, filter: {1}]", new Object[]{text, filter}); // NOI18N
                            }
                        }
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    Completion.get().hideAll();
                }
            });
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent ke) {
    }

    @Override
    public int getPreferredWidth(Graphics grphcs, Font font) {
        return CompletionUtilities.getPreferredWidth(text, null, grphcs, font);
    }

    @Override
    public void render(Graphics grphcs, Font font, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(), getRightHtmlText(), grphcs, font, defaultColor, width, height, selected);
    }

    public ImageIcon getIcon() {
        return ICON;
    }

    public String getLeftHtmlText() {
        return text;
    }

    public String getRightHtmlText() {
        return null;
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent jtc) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }

    static class FlashMethodCompletionItem extends CakePHP3CompletionItem {

        private static final String METHOD_FORMAT1 = "%s($name)"; // NOI18N
        private static final String METHOD_FORMAT2 = "%s($name, $options = [])"; // NOI18N
        private final int paramCount;

        FlashMethodCompletionItem(String text, String filter, int startOffset, int paramCount) {
            super(text, filter, startOffset);
            this.paramCount = paramCount;
        }

        @Override
        public void defaultAction(JTextComponent jtc) {
            Document document = jtc.getDocument();
            if (document == null) {
                return;
            }
            CodeTemplateManager manager = CodeTemplateManager.get(document);
            String text = getText();
            if (text.startsWith(getFilter())) {
                text = text.replace(getFilter(), ""); // NOI18N
            }
            StringBuilder sb = new StringBuilder();
            sb.append(text).append("(") // NOI18N
                    .append("${$name}"); // NOI18N
            if (paramCount >= 2) {
                sb.append(", ${$options}"); // NOI18N
            }
            sb.append(")"); // NOI18N
            CodeTemplate template = manager.createTemporary(sb.toString());
            template.insert(jtc);
        }

        @Override
        public String getLeftHtmlText() {
            String format;
            switch (paramCount) {
                case 1:
                    format = METHOD_FORMAT1;
                    break;
                case 2:
                    format = METHOD_FORMAT2;
                    break;
                default:
                    throw new AssertionError();
            }
            return String.format(format, getText());
        }

        public static List<CakePHP3CompletionItem> createItems(String text, String filter, int startOffset) {
            if (StringUtils.isEmpty(text)) {
                Collections.emptyList();
            }
            return Arrays.<CakePHP3CompletionItem>asList(
                    new FlashMethodCompletionItem(text, filter, startOffset, 1),
                    new FlashMethodCompletionItem(text, filter, startOffset, 2)
            );
        }

    }
}
