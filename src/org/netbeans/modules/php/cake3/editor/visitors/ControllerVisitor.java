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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHPModule.Category;
import org.netbeans.modules.php.cake3.utils.CakePHPCodeUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.ExpressionStatement;
import org.netbeans.modules.php.editor.parser.astnodes.FieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceName;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.StaticMethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Pair;

public class ControllerVisitor extends FieldsVisitor {

    private static final String LOAD_COMPONENT_METHOD = "loadComponent"; // NOI18N
    private static final String LOAD_MODEL_METHOD = "loadModel"; // NOI18N
    private static final String TABLE_REGISTRY_METHOD = "TableRegistry"; // NOI18N
    private static final String RENDER_METHOD = "render"; // NOI18N
    private static final String SET_METHOD = "set"; // NOI18N
    private final boolean isTemplate;
    private String currentMethodName = ""; // NOI18N
    private String templateName = ""; // NOI18N
    private final Set<String> templateNames = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> allTemplateNames = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> themeNames = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> allThemeNames = Collections.synchronizedSet(new HashSet<>());

    public ControllerVisitor(PhpModule phpModule) {
        this(phpModule, false);
    }

    public ControllerVisitor(PhpModule phpModule, boolean isTemplate) {
        super(phpModule);
        this.isTemplate = isTemplate;
    }

    public ControllerVisitor(PhpModule phpModule, FileObject targetFile, boolean isTemplate, int caretPosition) {
        super(phpModule);
        this.isTemplate = isTemplate;
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement phpElement = editorSupport.getElement(targetFile, caretPosition);
        if (phpElement != null && phpElement instanceof PhpClass.Method) {
            PhpClass.Method method = (PhpClass.Method) phpElement;
            templateName = method.getName();
            allTemplateNames.add(templateName);
        }
    }

    @Override
    public Set<String> getAvailableFieldNames() {
        if (isTemplate) {
            return new HashSet<>(Arrays.asList(HELPERS));
        }
        return new HashSet<>(Arrays.asList(COMPONENTS, MODELS));
    }

    @Override
    public void visit(ExpressionStatement node) {
        super.visit(node);
        if (StringUtils.isEmpty(currentMethodName)) {
            return;
        }

        Expression expression = node.getExpression();
        if (expression instanceof Assignment) {
            Assignment assignment = (Assignment) expression;
            Assignment.Type operator = assignment.getOperator();
            if (operator != Assignment.Type.EQUAL) {
                return;
            }

            VariableBase leftHandSide = assignment.getLeftHandSide();
            if (leftHandSide instanceof FieldAccess) {
                // e.g. $this->theme = 'Modern';
                // left
                FieldAccess f = (FieldAccess) leftHandSide;
                Variable v = f.getField();
                String variableName = CodeUtils.extractVariableName(v);
                if (!"theme".equals(variableName)) { // NOI18N
                    return;
                }

                // right
                Expression rightHandSide = assignment.getRightHandSide();
                String rightValue = CakePHPCodeUtils.getStringValue(rightHandSide);
                if (rightValue.isEmpty()) {
                    return;
                }

                if (currentMethodName.equals(templateName)) {
                    themeNames.add(rightValue);
                } else {
                    allThemeNames.add(rightValue);
                }
            }
        }
    }

    @Override
    public void visit(ClassDeclaration node) {
        // add a default table e.g. UsersController -> UsersTable
        String className = CodeUtils.extractClassName(node);
        int lastIndexOfController = className.lastIndexOf(Category.CONTROLLER.getSuffix());
        if (lastIndexOfController > 0) {
            String controllerName = className.substring(0, lastIndexOfController);
            Pair<String, PhpClass> phpClass = createPhpClass(Category.TABLE, controllerName, controllerName);
            if (phpClass != null) {
                addPhpClasss(Category.TABLE, phpClass);
            }
        }
        super.visit(node);
    }

    @Override
    public void visit(MethodDeclaration node) {
        FunctionDeclaration function = node.getFunction();
        currentMethodName = CodeUtils.extractFunctionName(function);
        if (templateName.equals(currentMethodName)) {
            templateNames.add(currentMethodName);
        }
        if (!currentMethodName.startsWith("_")) { // NOI18N
            // XXX ignore override methods? e.g. initialize
            allTemplateNames.add(currentMethodName);
        }
        super.visit(node);
    }

    @Override
    public void visit(MethodInvocation node) {
        FunctionInvocation method = node.getMethod();
        handleMethod(method);
        super.visit(node);
    }

    @Override
    public void visit(StaticMethodInvocation node) {
        super.visit(node);
        // TableRegistry::get('Users');
        String methodClassName = getClassName(node);
        if (methodClassName == null || !TABLE_REGISTRY_METHOD.equals(methodClassName)) {
            return;
        }
        FunctionInvocation method = node.getMethod();
        String methodName = CodeUtils.extractFunctionName(method);
        if (!methodName.equals("get")) { // NOI18N
            return;
        }
        addModel(method);
    }

    private String getClassName(StaticMethodInvocation node) {
        Expression className = node.getDispatcher();
        if (className instanceof NamespaceName) {
            return CodeUtils.extractQualifiedName((NamespaceName) className);
        }
        return null;
    }

    private void handleMethod(FunctionInvocation method) {
        String name = CodeUtils.extractFunctionName(method);
        if (StringUtils.isEmpty(name)) {
            return;
        }
        switch (name) {
            case LOAD_COMPONENT_METHOD:
                addComponent(method);
                break;
            case LOAD_MODEL_METHOD:
                addModel(method);
                break;
            case RENDER_METHOD:
                addTemplate(method);
                break;
            case SET_METHOD:
                // TODO
                break;
            default:
                break;
        }
    }

    private void addComponent(FunctionInvocation method) {
        List<Expression> parameters = method.getParameters();
        Pair<String, String> aliasAndEntityName = CakePHPCodeUtils.getAliasAndEntityName(parameters);
        String aliasName = aliasAndEntityName.first();
        String entityName = aliasAndEntityName.second();
        if (!StringUtils.isEmpty(aliasName)) {
            if (StringUtils.isEmpty(entityName)) {
                entityName = aliasName;
            }
            Pair<String, PhpClass> phpClass = createPhpClass(Category.COMPONENT, aliasName, entityName);
            if (phpClass != null) {
                addPhpClasss(Category.COMPONENT, phpClass);
            }
        }
    }

    private void addModel(FunctionInvocation method) {
        List<Expression> parameters = method.getParameters();
        String tableName = ""; // NOI18N
        for (Expression parameter : parameters) {
            if (parameter instanceof Scalar) {
                tableName = CakePHPCodeUtils.getStringValue(parameter);
            }
            // only the first parameter
            break;
        }
        if (!StringUtils.isEmpty(tableName)) {
            Pair<String, PhpClass> phpClass = createPhpClass(Category.TABLE, tableName, ""); // NOI18N
            if (phpClass != null) {
                addPhpClasss(Category.TABLE, phpClass);
            }
        }
    }

    private void addTemplate(FunctionInvocation method) {
        List<Expression> parameters = method.getParameters();
        String methodName = ""; // NOI18N
        for (Expression parameter : parameters) {
            if (parameter instanceof Scalar) {
                methodName = CakePHPCodeUtils.getStringValue(parameter);
            }
            // only the first parameter
            // XXX add layout?
            break;
        }
        if (currentMethodName.equals(templateName)) {
            templateNames.add(methodName);
        }
        if (!StringUtils.isEmpty(methodName)) {
            allTemplateNames.add(methodName);
        }
    }

    public List<String> getTemplateNames() {
        return new ArrayList<>(templateNames);
    }

    public List<String> getAllTemplateNames() {
        return new ArrayList<>(allTemplateNames);
    }

    public List<String> getThemeNames() {
        return new ArrayList<>(themeNames);
    }

    public List<String> getAllThemeNames() {
        return new ArrayList<>(allThemeNames);
    }
}
