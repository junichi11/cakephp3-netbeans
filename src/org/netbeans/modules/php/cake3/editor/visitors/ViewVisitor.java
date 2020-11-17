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
package org.netbeans.modules.php.cake3.editor.visitors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHPModule.Category;
import org.netbeans.modules.php.cake3.utils.CakePHPCodeUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public class ViewVisitor extends CakePHP3Visitor {

    // pair of alias name and PhpClass
    private final Set<Pair<String, PhpClass>> phpClasses = Collections.synchronizedSet(new HashSet<>());
    private static final String LOAD_HELPER_METHOD = "loadHelper"; // NOI18N

    public ViewVisitor(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public void visit(MethodInvocation node) {
        FunctionInvocation method = node.getMethod();
        handleMethod(method);
        super.visit(node);
    }

    private void handleMethod(FunctionInvocation method) {
        String methodName = CodeUtils.extractFunctionName(method);
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        switch (methodName) {
            case LOAD_HELPER_METHOD:
                addHelper(method);
                break;
            default:
                break;
        }
    }

    private synchronized void addHelper(FunctionInvocation method) {
        List<Expression> parameters = method.getParameters();
        Pair<String, String> aliasAndEntityName = CakePHPCodeUtils.getAliasAndEntityName(parameters);
        String aliasName = aliasAndEntityName.first();
        String entityName = aliasAndEntityName.second();
        if (!StringUtils.isEmpty(aliasName)) {
            if (StringUtils.isEmpty(entityName)) {
                entityName = aliasName;
            }
            Pair<String, PhpClass> phpClass = createPhpClass(Category.HELPER, aliasName, entityName);
            if (phpClass != null) {
                phpClasses.add(phpClass);
            }
        }
    }

    public Set<Pair<String, PhpClass>> getPhpClasses() {
        return new HashSet<>(phpClasses);
    }

}
