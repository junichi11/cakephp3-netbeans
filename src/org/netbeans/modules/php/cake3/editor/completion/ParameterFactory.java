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

import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class ParameterFactory {

    private static final ParameterFactory INSTANCE = new ParameterFactory();

    private ParameterFactory() {
    }

    public static final ParameterFactory getInstance() {
        return INSTANCE;
    }

    @CheckForNull
    public Parameter create(int position, String className, String methodName, FileObject fileObject) {
        Parameter.Type type = Parameter.getType(position, className, methodName);
        switch (type) {
            case Constant:
                return new ConstantParameter(position, className, methodName, fileObject);
            case Path:
                return new FilePathParameter(position, className, methodName, fileObject);
            default:
                break;
        }
        return null;
    }

}
