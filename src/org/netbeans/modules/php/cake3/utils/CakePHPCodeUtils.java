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
package org.netbeans.modules.php.cake3.utils;

import java.util.List;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public final class CakePHPCodeUtils {

    private CakePHPCodeUtils() {
    }

    /**
     * Get string name from Expression
     *
     * @param e Expression
     * @return string name | empty string
     */
    public static String getStringValue(Expression e) {
        String name = ""; // NOI18N
        if (e instanceof Scalar) {
            Scalar s = (Scalar) e;
            if (s.getScalarType() == Scalar.Type.STRING) {
                name = s.getStringValue();
            }
            if (name.length() > 2) {
                name = name.substring(1, name.length() - 1);
            } else {
                name = ""; // NOI18N
            }
        }
        return name;
    }

    /**
     * Get entity. Return a class Expression if "className" exists in the array.
     *
     * @param ac ArrayCreation
     * @return entity | null
     */
    public static Expression getEntity(ArrayCreation ac) {
        if (ac == null) {
            return null;
        }
        for (ArrayElement element : ac.getElements()) {
            Expression key = element.getKey();
            String keyName = getStringValue(key);
            if (keyName.equals("className")) { // NOI18N
                return element.getValue();
            }
        }
        return null;
    }

    /**
     * Get ArrayCreation
     *
     * @param field
     * @return
     */
    public static ArrayCreation getArrayCreation(SingleFieldDeclaration field) {
        ArrayCreation arrayCreation = null;
        Expression value = field.getValue();
        if (value instanceof ArrayCreation) {
            arrayCreation = (ArrayCreation) value;
        }
        return arrayCreation;
    }

    /**
     * Get pair of alias and entity name from method parameters. If "className"
     * element doesn't exist in the parameter's array, entity name will be empty
     * string.
     *
     * @param parameters method parameters
     * @return Pair of alias and entity name
     */
    public static Pair<String, String> getAliasAndEntityName(List<Expression> parameters) {
        String aliasName = ""; // NOI18N
        String entityName = ""; // NOI18N
        for (Expression parameter : parameters) {
            if (parameter instanceof Scalar) {
                aliasName = getStringValue(parameter);
            }
            if (parameter instanceof ArrayCreation) {
                Expression entity = getEntity((ArrayCreation) parameter);
                entityName = getStringValue(entity);
            }
        }
        return Pair.of(aliasName, entityName);
    }

    /**
     * Get the first parameter string.
     *
     * @param method
     * @return the first parameter if it is string, otherwise empty string
     */
    public static String getFirstParameter(FunctionInvocation method) {
        List<Expression> parameters = method.getParameters();
        String firstParameter = ""; // NOI18N
        for (Expression parameter : parameters) {
            if (parameter instanceof Scalar) {
                firstParameter = CakePHPCodeUtils.getStringValue(parameter);
            }
            // only the first parameter
            break;
        }
        return firstParameter;
    }

}
