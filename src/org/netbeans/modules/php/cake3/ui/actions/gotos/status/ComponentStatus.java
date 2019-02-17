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
package org.netbeans.modules.php.cake3.ui.actions.gotos.status;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.editor.visitors.ComponentVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import static org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category.COMPONENT;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Pair;

public class ComponentStatus extends CakePHP3GoToStatus {

    private final List<GoToItem> components = new ArrayList<>();

    public ComponentStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
        clear();
        ComponentVisitor visitor = new ComponentVisitor(phpModule);
        try {
            scan(visitor, fileObject);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        addItems(COMPONENT, visitor.getComponents());
    }

    private void addItems(Category category, List<Pair<String, PhpClass>> phpClasses) {
        for (Pair<String, PhpClass> clazz : phpClasses) {
            PhpClass phpClass = clazz.second();
            FileObject file = phpClass.getFile();
            if (file == null) {
                continue;
            }
            switch (category) {
                case COMPONENT:
                    components.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<GoToItem> getSmart() {
        return getComponents();
    }

    @Override
    public List<GoToItem> getComponents() {
        List<GoToItem> items = new ArrayList<>(components);
        return items;
    }

    private void clear() {
        components.clear();
    }
}
