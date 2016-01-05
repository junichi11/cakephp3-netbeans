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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.lib2.DocUtils;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import static org.netbeans.modules.php.api.util.FileUtils.PHP_MIME_TYPE;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.TypeScope;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionProvider;
import static org.netbeans.spi.editor.completion.CompletionProvider.COMPLETION_QUERY_TYPE;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = PHP_MIME_TYPE, service = CompletionProvider.class)
public class ParameterCompletionProvider extends CakePHP3CompletionProvider {

    @Override
    public CompletionTask createTask(int queryType, JTextComponent textComponent, PhpModule phpModule, FileObject fo) {
        if (queryType != COMPLETION_QUERY_TYPE) {
            return null;
        }
        return new AsyncCompletionTask(new AsyncCompletionQueryImpl(queryType, textComponent.getCaretPosition()), textComponent);
    }

    static class AsyncCompletionQueryImpl extends AsyncCompletionQuery {

        private final int queryType;
        private final int caretOffset;
        private int methodOffset;
        private String filter;
        private String methodName;
        private Collection<CompletionItem> results;

        public AsyncCompletionQueryImpl(int queryType, int caretOffset) {
            this.queryType = queryType;
            this.caretOffset = caretOffset;
        }

        @Override
        protected void query(CompletionResultSet resultSet, Document document, final int caretOffset) {
            results = null;
            filter = ""; // NOI18N
            methodName = ""; // NOI18N
            methodOffset = -1;
            TokenSequence<PHPTokenId> tokenSequence = null;
            try {
                DocUtils.atomicLock(document);
                try {
                    tokenSequence = LexUtilities.getPHPTokenSequence(document, caretOffset);
                } finally {
                    DocUtils.atomicUnlock(document);
                }
                if (tokenSequence == null) {
                    return;
                }

                // filter
                tokenSequence.move(caretOffset);
                tokenSequence.moveNext();
                Token<PHPTokenId> token = tokenSequence.token();

                // check string ('' or "")
                if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                    return;
                }

                String parameterString = tokenSequence.token().text().toString();
                int startOffset = tokenSequence.offset() + 1; // + quote
                int diff = caretOffset - startOffset;
                if (diff < 0) {
                    return;
                } else {
                    filter = parameterString.substring(1, diff + 1);
                }

                // method offset
                tokenSequence.move(caretOffset);
                tokenSequence.moveNext();
                Token<? extends PHPTokenId> previousToken = LexUtilities.findPreviousToken(tokenSequence, Arrays.asList(PHPTokenId.PHP_STRING, PHPTokenId.PHP_SEMICOLON));
                if (previousToken == null || previousToken.id() != PHPTokenId.PHP_STRING) {
                    // no method
                    return;
                }
                methodOffset = tokenSequence.offset();
                methodName = previousToken.text().toString();

                // parse
                Source source = Source.create(document);
                if (source != null) {
                    final Collection<Source> sources = Collections.singleton(source);
                    final UserTask task = new UserTask() {
                        @Override
                        public void run(ResultIterator resultIterator) throws Exception {
                            Parser.Result result = resultIterator.getParserResult(caretOffset);
                            if (!(result instanceof PHPParseResult)) {
                                return;
                            }
                            PHPParseResult parserResult = (PHPParseResult) result;
                            resolveCompletion(parserResult);
                        }
                    };

                    ParserManager.parse(sources, task);
                    if (results != null) {
                        resultSet.addAllItems(results);
                    }
                }
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                resultSet.finish();
            }
        }

        private void resolveCompletion(PHPParseResult result) {
            BaseDocument doc = (BaseDocument) result.getSnapshot().getSource().getDocument(false);
            if (doc == null || caretOffset < 0 || methodOffset < 0 || methodName.isEmpty()) {
                return;
            }
            results = new ArrayList<>();
            Parameter parameter = getParameter(result, doc, methodName, methodOffset);
            if (parameter == null) {
                return;
            }
            results.addAll(parameter.getCompletionItems(filter, caretOffset));
        }

        /**
         * Get Parameter.
         *
         * @param parserResult the parser result
         * @param document the document
         * @param methodName the method name
         * @param methodOffset the method offset
         * @return
         */
        @CheckForNull
        private Parameter getParameter(PHPParseResult parserResult, Document document, String methodName, int methodOffset) {
            int parameterIndex;
            if (parserResult != null) {
                FileObject fileObject = parserResult.getSnapshot().getSource().getFileObject();
                if (fileObject == null) {
                    return null;
                }
                parameterIndex = getParameterIndex(document, methodName);
                if (parameterIndex >= 0) {
                    TokenHierarchy<?> th = parserResult.getSnapshot().getTokenHierarchy();
                    TokenSequence<PHPTokenId> tokenSequence = LexUtilities.getPHPTokenSequence(th, methodOffset);

                    if (tokenSequence == null) {
                        return null;
                    }

                    tokenSequence.move(methodOffset);
                    if (tokenSequence.movePrevious()) {
                        if (tokenSequence.token().id() != PHPTokenId.PHP_PAAMAYIM_NEKUDOTAYIM
                                && tokenSequence.token().id() != PHPTokenId.PHP_OBJECT_OPERATOR) {
                            tokenSequence.movePrevious();
                        }
                        tokenSequence.movePrevious();
                        if (tokenSequence.token().id() == PHPTokenId.WHITESPACE) {
                            tokenSequence.movePrevious();
                        }
                        tokenSequence.moveNext();
                        Model model = parserResult.getModel();
                        Collection<? extends TypeScope> types = ModelUtils.resolveTypeAfterReferenceToken(model, tokenSequence, methodOffset);
                        for (TypeScope type : types) {
                            String typeName = type.getName();
                            return Parameter.create(parameterIndex, typeName, methodName, fileObject);
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Get parameter index for the current caret position.
         *
         * @param document document
         * @param methodName the method name
         * @return index number if it is found, otherwise -1
         */
        private int getParameterIndex(Document document, String methodName) {
            ((AbstractDocument) document).readLock();
            try {
                TokenSequence<PHPTokenId> ts = LexUtilities.getPHPTokenSequence(document, methodOffset);
                if (ts != null && !StringUtils.isEmpty(methodName) && methodOffset >= 0) {
                    ts.move(methodOffset);
                    int index = 0;
                    int braceBalance = 0;
                    int bracketBalance = 0;
                    while (ts.moveNext()) {
                        Token<PHPTokenId> token = ts.token();
                        if (token == null) {
                            break;
                        }

                        PHPTokenId id = token.id();
                        if (id == PHPTokenId.PHP_SEMICOLON) {
                            break;
                        }

                        String tokenText = token.text().toString();
                        if (ts.offset() > caretOffset) {
                            return index;
                        }
                        // check array(), [], function()
                        switch (tokenText) {
                            case ",": // NOI18N
                                if (braceBalance == 1 && bracketBalance == 0) {
                                    index++;
                                }
                                break;
                            case "(": // NOI18N
                                braceBalance++;
                                break;
                            case ")": // NOI18N
                                braceBalance--;
                                break;
                            case "[": // NOI18N
                                bracketBalance++;
                                break;
                            case "]": // NOI18N
                                bracketBalance--;
                                break;
                            default:
                                break;
                        }
                    }
                }
            } finally {
                ((AbstractDocument) document).readUnlock();
            }
            return -1;
        }

    }
}
