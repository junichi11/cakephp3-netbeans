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
import java.util.Collection;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.editor.lib2.DocUtils;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import static org.netbeans.modules.php.api.util.FileUtils.PHP_MIME_TYPE;
import org.netbeans.modules.php.editor.api.PhpElementKind;
import org.netbeans.modules.php.editor.api.elements.PhpElement;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.Occurence;
import org.netbeans.modules.php.editor.model.OccurencesSupport;
import org.netbeans.modules.php.editor.model.ParameterInfoSupport;
import org.netbeans.spi.editor.completion.CompletionProvider;
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
        int methodOffset = tokenSequence.offset();
        Parameter parameter = null;
        try {
            parameter = getParameter(fo, caretPosition, methodOffset);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (parameter == null) {
            return null;
        }
        return new AsyncCompletionTask(new AsyncCompletionQueryImpl(parameter), textComponent);
    }

    /**
     * Get Parameter.
     *
     * @param fileObject FileObject
     * @param caretOffset caret position
     * @param offset
     * @return
     * @throws ParseException
     */
    private Parameter getParameter(FileObject fileObject, int caretOffset, int offset) throws ParseException {
        int parameterIndex;
        Model model = ModelUtils.getModel(Source.create(fileObject), 3000);
        if (model != null) {
            ParameterInfoSupport parameterInfoSupport = model.getParameterInfoSupport(caretOffset);
            ParameterInfo parameterInfo = parameterInfoSupport.getParameterInfo();
            parameterIndex = parameterInfo.getCurrentIndex();
            if (parameterIndex >= 0) {
                OccurencesSupport occurencesSupport = model.getOccurencesSupport(offset);
                Occurence occurence = occurencesSupport.getOccurence();
                if (occurence != null) {
                    PhpElementKind kind = occurence.getKind();
                    if (kind == PhpElementKind.METHOD) {
                        Collection<? extends PhpElement> gotoDeclarations = occurence.gotoDeclarations();
                        for (PhpElement gotoDeclaration : gotoDeclarations) {
                            String className = gotoDeclaration.getIn();
                            String methodName = gotoDeclaration.getName();
                            return Parameter.create(parameterIndex, className, methodName, fileObject);
                        }
                    }
                }
            }
        }
        return null;
    }

    static class AsyncCompletionQueryImpl extends AsyncCompletionQuery {

        private final Parameter parameter;

        public AsyncCompletionQueryImpl(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        protected void query(CompletionResultSet resultSet, Document document, int caretOffset) {
            try {
                if (parameter == null) {
                    return;
                }
                TokenSequence<PHPTokenId> tokenSequence = null;
                DocUtils.atomicLock(document);
                try {
                    tokenSequence = LexUtilities.getPHPTokenSequence(document, caretOffset);
                } finally {
                    DocUtils.atomicUnlock(document);
                }
                if (tokenSequence == null) {
                    return;
                }
                tokenSequence.move(caretOffset);
                tokenSequence.moveNext();
                Token<PHPTokenId> token = tokenSequence.token();

                // check string ('' or "")
                if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                    return;
                }

                String parameterString = tokenSequence.token().text().toString();
                int startOffset = tokenSequence.offset() + 1; // + quote
                String filter;
                int diff = caretOffset - startOffset;
                if (diff < 0) {
                    return;
                } else {
                    filter = parameterString.substring(1, diff + 1);
                }

                resultSet.addAllItems(parameter.getCompletionItems(filter, caretOffset));
            } finally {
                resultSet.finish();
            }
        }
    }
}
