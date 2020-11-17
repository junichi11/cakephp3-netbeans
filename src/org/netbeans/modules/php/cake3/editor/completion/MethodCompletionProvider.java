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
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
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
        CakePHPModule cakeModule = CakePHPModule.forFileObject(fileObject);
        ModuleInfo info = cakeModule.createModuleInfo(fileObject);
        List<FileObject> directories = cakeModule.getDirectories(info.getBase(), CakePHPModule.Category.ELEMENT, info.getPluginName());
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
