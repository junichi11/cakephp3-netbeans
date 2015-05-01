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
package org.netbeans.modules.php.cake3.validators;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.modules.php.cake3.dotcake.Dotcake;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePHP3CustomizerValidator {

    private final ValidationResult result = new ValidationResult();

    public ValidationResult getResult() {
        return result;
    }

    @NbBundle.Messages({
        "# {0} - name",
        "CakePHP3CustomizerValidator.notFound.error={0} : Existing path must be set."
    })
    public CakePHP3CustomizerValidator validateRootPath(@NonNull FileObject baseDirectory, @NonNull String rootPath) {
        FileObject root = baseDirectory.getFileObject(rootPath);
        if (root == null) {
            addNotFoundError("root", "Root"); // NOI18N
        }
        return this;
    }

    public CakePHP3CustomizerValidator validateSrc(FileObject baseDirectory, String path) {
        FileObject root = baseDirectory.getFileObject(path);
        if (root == null) {
            String error = String.format("Dir(src) %s ", path); // NOI18N
            addNotFoundError("src", error); // NOI18N
        }
        return this;
    }

    public CakePHP3CustomizerValidator validateWWWRoot(FileObject baseDirectory, String path) {
        FileObject root = baseDirectory.getFileObject(path);
        if (root == null) {
            String error = String.format("WWW Root %s ", path); // NOI18N
            addNotFoundError("wwwroot", error); // NOI18N
        }
        return this;
    }

    public CakePHP3CustomizerValidator validateCss(FileObject baseDirectory, String path) {
        FileObject root = baseDirectory.getFileObject(path);
        if (root == null) {
            String error = String.format("Css %s ", path); // NOI18N
            addNotFoundError("css", error); // NOI18N
        }
        return this;
    }

    public CakePHP3CustomizerValidator validateImg(FileObject baseDirectory, String path) {
        FileObject root = baseDirectory.getFileObject(path);
        if (root == null) {
            String error = String.format("Img %s ", path); // NOI18N
            addNotFoundError("img", error); // NOI18N
        }
        return this;
    }

    public CakePHP3CustomizerValidator validateJs(FileObject baseDirectory, String path) {
        FileObject root = baseDirectory.getFileObject(path);
        if (root == null) {
            String error = String.format("Js %s ", path); // NOI18N
            addNotFoundError("js", error); // NOI18N
        }
        return this;
    }

    @NbBundle.Messages({
        "CakePHP3CustomizerValidator.invalid.dotcake.fileName=It's not a .cake file",
        "CakePHP3CustomizerValidator.invalid.jsonFile=It's a invalid json file"
    })
    public CakePHP3CustomizerValidator validateDotcake(FileObject baseDirectory, String path) {
        if (StringUtils.isEmpty(path)) {
            return this;
        }

        FileObject file = baseDirectory.getFileObject(path);
        if (file == null) {
            String error = String.format(".cake %s ", path); // NOI18N
            addNotFoundError(".cake", error); // NOI18N
            return this;
        }

        if (!path.endsWith(Dotcake.DOTCAKE_NAME) || !Dotcake.isDotcake(file)) {
            ValidationResult.Message message = new ValidationResult.Message("dotcake", Bundle.CakePHP3CustomizerValidator_invalid_dotcake_fileName());
            result.addError(message);
            return this;
        }

        boolean isParserError = false;
        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            try (InputStreamReader reader = new InputStreamReader(inputStream, CakePHP3Constants.UTF8)) {
                JSONParser parser = new JSONParser();
                parser.parse(reader);
            } catch (ParseException ex) {
                isParserError = true;
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (isParserError) {
            ValidationResult.Message message = new ValidationResult.Message("dotcake", Bundle.CakePHP3CustomizerValidator_invalid_jsonFile());
            result.addError(message);
            return this;
        }

        return this;
    }

    private void addNotFoundError(Object source, String error) {
        ValidationResult.Message message = new ValidationResult.Message(source, Bundle.CakePHP3CustomizerValidator_notFound_error(error));
        result.addError(message);
    }

}
