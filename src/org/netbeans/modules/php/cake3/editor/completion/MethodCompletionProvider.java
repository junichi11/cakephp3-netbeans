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

import java.util.Arrays;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.lib2.DocUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import static org.netbeans.modules.php.api.util.FileUtils.PHP_MIME_TYPE;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.ModuleInfo;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionProvider;
import static org.netbeans.spi.editor.completion.CompletionProvider.COMPLETION_QUERY_TYPE;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = PHP_MIME_TYPE, service = CompletionProvider.class)
public class MethodCompletionProvider extends CakePHP3CompletionProvider {

    @Override
    public CompletionTask createTask(int queryType, JTextComponent textComponent, PhpModule phpModule, FileObject fo) {
        if (queryType != COMPLETION_QUERY_TYPE) {
            return null;
        }
        Document document = textComponent.getDocument();
        if (document == null) {
            return null;
        }
        TokenSequence<PHPTokenId> tokenSequence = null;
        int caretPosition = textComponent.getCaretPosition();
        DocUtils.atomicLock(document);
        try {
            tokenSequence = LexUtilities.getPHPTokenSequence(document, caretPosition);
        } finally {
            DocUtils.atomicUnlock(document);
        }
        if (tokenSequence == null) {
            return null;
        }
        tokenSequence.move(caretPosition);
        tokenSequence.moveNext();
        Token<? extends PHPTokenId> previousToken = LexUtilities.findPreviousToken(tokenSequence, Arrays.asList(PHPTokenId.PHP_STRING, PHPTokenId.PHP_SEMICOLON));
        if (previousToken == null || previousToken.id() != PHPTokenId.PHP_STRING) {
            return null;
        }

        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                try {
                    autoComplete(resultSet, doc, caretOffset);
                } finally {
                    resultSet.finish();
                }
            }
        }, textComponent);
    }

    private void autoComplete(CompletionResultSet resultSet, Document document, int offset) {
        DocUtils.atomicLock(document);
        TokenSequence<PHPTokenId> tokenSequence = null;
        try {
            tokenSequence = LexUtilities.getPHPTokenSequence(document, offset);
        } finally {
            DocUtils.atomicUnlock(document);
        }

        if (tokenSequence == null) {
            return;
        }

        tokenSequence.move(offset);
        String filter = ""; // NOI18N
        if (tokenSequence.moveNext()) {
            filter = getFilter(tokenSequence, offset);
        }
        if (tokenSequence.movePrevious()) {
            int startOffset = tokenSequence.offset();
            if (tokenSequence.token().id() == PHPTokenId.PHP_STRING) {
                filter = getFilter(tokenSequence, offset);
            }
            if (tokenSequence.token().id() != PHPTokenId.PHP_PAAMAYIM_NEKUDOTAYIM
                    && tokenSequence.token().id() != PHPTokenId.PHP_OBJECT_OPERATOR) {
                tokenSequence.movePrevious();
            }
            tokenSequence.movePrevious();
            if (tokenSequence.token().id() == PHPTokenId.WHITESPACE) {
                tokenSequence.movePrevious();
            }
            String varName = tokenSequence.token().text().toString();
            tokenSequence.moveNext();

            switch (varName) {
                case "Flash":  // NOI18N
                    autoCompleteFlash(document, filter, resultSet, startOffset);
                    break;
                default:
                    break;
            }

        }

    }

    private String getFilter(TokenSequence<PHPTokenId> tokenSequence, int offset) {
        Token<PHPTokenId> token = tokenSequence.token();
        PHPTokenId id = token.id();
        if (id == PHPTokenId.PHP_STRING) {
            String tokenString = token.text().toString();
            int filterLength = offset - tokenSequence.offset();
            if (filterLength >= 0 && tokenString.length() >= filterLength) {
                return tokenString.substring(0, filterLength);
            }
        }
        return ""; // NOI18N
    }

    private void autoCompleteFlash(Document document, String filter, CompletionResultSet resultSet, int startOffset) {
        FileObject fileObject = NbEditorUtilities.getFileObject(document);
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fileObject);
        ModuleInfo info = cakeModule.createModuleInfo(fileObject);
        List<FileObject> directories = cakeModule.getDirectories(info.getBase(), CakePHP3Module.Category.ELEMENT, info.getPluginName());
        for (FileObject directory : directories) {
            FileObject flashDirectory = directory.getFileObject("Flash"); // NOI18N
            if (flashDirectory != null && flashDirectory.isFolder()) {
                for (FileObject child : flashDirectory.getChildren()) {
                    if (child.isFolder()) {
                        continue;
                    }
                    String name = child.getName();
                    if (!name.startsWith(filter)) {
                        continue;
                    }
                    resultSet.addAllItems(CakePHP3CompletionItem.FlashMethodCompletionItem.createItems(name, filter, startOffset));
                }
            }
        }
    }

}
