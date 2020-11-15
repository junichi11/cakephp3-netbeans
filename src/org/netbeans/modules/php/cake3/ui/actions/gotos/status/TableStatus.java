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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.editor.visitors.TableVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
import org.netbeans.modules.php.cake3.modules.CakePHPModule.Category;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import static org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatus.DEFAULT_OFFSET;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Pair;

public class TableStatus extends CakePHP3GoToStatus {

    private final List<GoToItem> tables = new ArrayList<>();
    private final List<GoToItem> behaviors = new ArrayList<>();

    public TableStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
        clear();
        TableVisitor tableVisitor = new TableVisitor(phpModule);
        try {
            scan(tableVisitor, fileObject);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        addTables(tableVisitor.getTables());
        addBehaviors(tableVisitor.getBehaviors());

    }

    private void addTables(Set<Pair<String, PhpClass>> tables) {
        addItems(Category.TABLE, tables);
    }

    private void addBehaviors(Set<Pair<String, PhpClass>> behaviors) {
        addItems(Category.BEHAVIOR, behaviors);
    }

    private void addItems(CakePHPModule.Category category, Collection<Pair<String, PhpClass>> phpClasses) {
        for (Pair<String, PhpClass> clazz : phpClasses) {
            PhpClass phpClass = clazz.second();
            FileObject file = phpClass.getFile();
            if (file == null) {
                continue;
            }
            switch (category) {
                case BEHAVIOR:
                    behaviors.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                case TABLE:
                    tables.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> items = new ArrayList<>(getEntities());
        items.addAll(tables);
        items.addAll(behaviors);
        items.addAll(getTestCases());
        return items;
    }

    @Override
    public List<GoToItem> getEntities() {
        List<GoToItem> items = new ArrayList<>();
        CakePHPModule cakeModule = CakePHPModule.forFileObject(getFileObject());
        FileObject entity = cakeModule.getEntity(getFileObject());
        if (entity != null) {
            items.add(GoToItemFactory.create(Category.ENTITY, entity, DEFAULT_OFFSET));
        }
        items.addAll(super.getEntities());
        return items;
    }

    @Override
    public List<GoToItem> getTables() {
        return new ArrayList<>(tables);
    }

    @Override
    public List<GoToItem> getBehaviors() {
        return new ArrayList<>(behaviors);
    }

    private void clear() {
        tables.clear();
        behaviors.clear();
    }
}
