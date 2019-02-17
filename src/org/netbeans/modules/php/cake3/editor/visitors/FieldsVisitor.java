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
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public abstract class FieldsVisitor extends CakePHP3Visitor {

    // Pair of alias name and PhpClass
    private final Set<Pair<String, PhpClass>> componentClasses = Collections.synchronizedSet(new HashSet<>());
    private final Set<Pair<String, PhpClass>> helperClasses = Collections.synchronizedSet(new HashSet<>());
    private final Set<Pair<String, PhpClass>> modelClasses = Collections.synchronizedSet(new HashSet<>());
    private String themeName = ""; // NOI18N

    public static final String COMPONENTS = "$components"; // NOI18N
    public static final String HELPERS = "$helpers"; // NOI18N
    public static final String MODELS = "$models"; // NOI18N dummy
    public static final String THEME = "$theme"; // NOI18N
    public static final Map<String, Category> CATEGORIES = new HashMap<>();

    static {
        CATEGORIES.put(COMPONENTS, Category.COMPONENT);
        CATEGORIES.put(HELPERS, Category.HELPER);
    }

    public FieldsVisitor(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public void visit(FieldsDeclaration node) {
        super.visit(node);
        List<SingleFieldDeclaration> fields = node.getFields();
        for (SingleFieldDeclaration field : fields) {
            // check field name
            String fieldName = CodeUtils.extractVariableName(field.getName());

            // theme
            if (THEME.equals(fieldName)) {
                setThemeName(field);
                continue;
            }

            // the others
            Category category = CATEGORIES.get(fieldName);
            if (category == null) {
                continue;
            }

            // get ArrayCreation
            ArrayCreation arrayCreation = CakePHPCodeUtils.getArrayCreation(field);
            if (arrayCreation == null) {
                continue;
            }
            addPhpClasses(category, arrayCreation);
        }
    }

    private void addPhpClasses(Category category, ArrayCreation arrayCreation) {
        List<ArrayElement> elements = arrayCreation.getElements();
        for (ArrayElement element : elements) {
            String aliasName;
            String entityName = ""; // NOI18N
            Expression value = element.getValue();
            if (value != null) {
                if (value instanceof Scalar) {
                    entityName = CakePHPCodeUtils.getStringValue(value);
                } else if (value instanceof ArrayCreation) {
                    ArrayCreation className = (ArrayCreation) value;
                    Expression entity = CakePHPCodeUtils.getEntity(className);
                    if (entity != null) {
                        entityName = CakePHPCodeUtils.getStringValue(entity);
                    }
                }

            }
            Expression key = element.getKey();
            aliasName = CakePHPCodeUtils.getStringValue(key);
            if (StringUtils.isEmpty(aliasName)) {
                aliasName = entityName;
            }
            Pair<String, PhpClass> phpClass = createPhpClass(category, aliasName, entityName);
            if (phpClass != null) {
                addPhpClasss(category, phpClass);
            }
        }
    }

    protected void addPhpClasss(Category category, Pair<String, PhpClass> phpClass) {
        switch (category) {
            case COMPONENT:
                componentClasses.add(phpClass);
                break;
            case HELPER:
                helperClasses.add(phpClass);
                break;
            case TABLE:
                modelClasses.add(phpClass);
                break;
            default:
                break;
        }
    }

    private void setThemeName(SingleFieldDeclaration field) {
        Expression value = field.getValue();
        if (value instanceof Scalar) {
            themeName = CakePHPCodeUtils.getStringValue(value);
        }
    }

    public List<Pair<String, PhpClass>> getPhpClasses() {
        Set<String> fields = getAvailableFieldNames();
        List<Pair<String, PhpClass>> phpClasses = new ArrayList<>();
        if (fields.contains(COMPONENTS)) {
            phpClasses.addAll(componentClasses);
        }
        if (fields.contains(HELPERS)) {
            phpClasses.addAll(helperClasses);
        }
        if (fields.contains(MODELS)) {
            phpClasses.addAll(modelClasses);
        }
        return phpClasses;
    }

    public List<Pair<String, PhpClass>> getComponents() {
        return new ArrayList<>(componentClasses);
    }

    public List<Pair<String, PhpClass>> getHelpers() {
        return new ArrayList<>(helperClasses);
    }

    public List<Pair<String, PhpClass>> getTables() {
        return new ArrayList<>(modelClasses);
    }

    public String getThemeName() {
        return themeName;
    }

    public abstract Set<String> getAvailableFieldNames();

}
