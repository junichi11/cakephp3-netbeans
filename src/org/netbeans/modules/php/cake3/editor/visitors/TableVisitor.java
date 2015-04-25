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
