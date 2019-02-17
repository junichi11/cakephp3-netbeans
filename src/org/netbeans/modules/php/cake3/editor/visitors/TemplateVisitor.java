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
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.utils.CakePHPCodeUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;

public class TemplateVisitor extends CakePHP3Visitor {

    private static final String ELEMENT_METHOD = "element"; // NOI18N
    private static final String EXTEND_METHOD = "extend"; // NOI18N
    private static final String CELL_METHOD = "cell"; // NOI18N
    private final Set<String> elements = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> extendz = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> cells = Collections.synchronizedSet(new HashSet<>());

    public TemplateVisitor(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public void visit(MethodInvocation node) {
        super.visit(node);
        if (!(node.getDispatcher() instanceof Variable) || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) { // NOI18N
            return;
        }
        FunctionInvocation method = node.getMethod();
        handleMethod(method);
    }

    private void handleMethod(FunctionInvocation method) {
        String methodName = CodeUtils.extractFunctionName(method);
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        switch (methodName) {
            case ELEMENT_METHOD:
                addElements(method);
                break;
            case EXTEND_METHOD:
                addExtends(method);
                break;
            case CELL_METHOD:
                addCells(method);
                break;
            default:
                break;
        }

    }

    private void addElements(FunctionInvocation method) {
        String elementPath = getFirstParameter(method);
        if (!StringUtils.isEmpty(elementPath)) {
            elements.add(elementPath);
        }
    }

    private void addExtends(FunctionInvocation method) {
        String extendsPath = getFirstParameter(method);
        if (!StringUtils.isEmpty(extendsPath)) {
            extendz.add(extendsPath);
        }
    }

    private void addCells(FunctionInvocation method) {
        String cell = getFirstParameter(method);
        if (!StringUtils.isEmpty(cell)) {
            cells.add(cell);
        }
    }

    private String getFirstParameter(FunctionInvocation method) {
        return CakePHPCodeUtils.getFirstParameter(method);
    }

    public Set<String> getElements() {
        return new HashSet<>(elements);
    }

    public Set<String> getExtends() {
        return new HashSet<>(extendz);
    }

    public Set<String> getCells() {
        return new HashSet<>(cells);
    }

}
