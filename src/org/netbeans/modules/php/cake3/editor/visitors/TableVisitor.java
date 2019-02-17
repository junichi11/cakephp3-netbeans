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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
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
public class TableVisitor extends CakePHP3Visitor {

    public enum Assosiation {

        HAS_ONE("hasOne"), // NOI18N
        HAS_MAMY("hasMany"), // NOI18N
        BELONGS_TO("belongsTo"), // NOI18N
        BELONGS_TO_MANY("belongsToMany"), // NOI18N
        UNKNOWN("UNKNOWN"); // NOI18N

        private static final Map<String, Assosiation> ASSOSIATIONS = new HashMap<>();
        private final String type;

        static {
            for (Assosiation value : Assosiation.values()) {
                ASSOSIATIONS.put(value.getType(), value);
            }
        }

        private Assosiation(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static Assosiation get(String type) {
            Assosiation assosiation = ASSOSIATIONS.get(type);
            if (assosiation == null) {
                return UNKNOWN;
            }
            return assosiation;
        }
    }

    // pair of alias name and PhpClass
    private final Set<Pair<String, PhpClass>> tables = Collections.synchronizedSet(new HashSet<Pair<String, PhpClass>>());
    private final Set<Pair<String, PhpClass>> behaviors = Collections.synchronizedSet(new HashSet<Pair<String, PhpClass>>());
    private static final String ADD_BEHABIOR_METHOD = "addBehavior"; // NOI18N

    public TableVisitor(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public void visit(MethodInvocation node) {
        super.visit(node);
        FunctionInvocation method = node.getMethod();
        handleMethod(method);
    }

    private void handleMethod(FunctionInvocation method) {
        String methodName = CodeUtils.extractFunctionName(method);
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        Assosiation assosiation = Assosiation.get(methodName);
        if (assosiation != Assosiation.UNKNOWN) {
            addTable(method);
        }

        switch (methodName) {
            case ADD_BEHABIOR_METHOD:
                addBehavior(method);
                break;
            default:
                break;
        }
    }

    private void addTable(FunctionInvocation method) {
        Pair<String, PhpClass> phpClass = createPhpClass(Category.TABLE, method);
        if (phpClass != null) {
            tables.add(phpClass);
        }
    }

    private void addBehavior(FunctionInvocation method) {
        Pair<String, PhpClass> phpClass = createPhpClass(Category.BEHAVIOR, method);
        if (phpClass != null) {
            behaviors.add(phpClass);
        }
    }

    private Pair<String, PhpClass> createPhpClass(Category category, FunctionInvocation method) {
        List<Expression> parameters = method.getParameters();
        Pair<String, String> aliasAndEntityName = CakePHPCodeUtils.getAliasAndEntityName(parameters);
        String aliasName = aliasAndEntityName.first();
        String entityName = aliasAndEntityName.second();

        if (!StringUtils.isEmpty(aliasName)) {
            if (StringUtils.isEmpty(entityName)) {
                entityName = aliasName;
            }
            return createPhpClass(category, aliasName, entityName);
        }
        return null;
    }

    public Set<Pair<String, PhpClass>> getTables() {
        return new HashSet<>(tables);
    }

    public Set<Pair<String, PhpClass>> getBehaviors() {
        return new HashSet<>(behaviors);
    }

}
