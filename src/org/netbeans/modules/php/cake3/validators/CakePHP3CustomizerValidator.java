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
