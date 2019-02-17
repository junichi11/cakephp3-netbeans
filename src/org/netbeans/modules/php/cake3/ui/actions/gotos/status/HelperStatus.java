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
import org.netbeans.modules.php.cake3.editor.visitors.HelperVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import static org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatus.DEFAULT_OFFSET;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Pair;

public class HelperStatus extends CakePHP3GoToStatus {

    private final List<GoToItem> helpers = new ArrayList<>();

    public HelperStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
        clear();
        HelperVisitor visitor = new HelperVisitor(phpModule);
        try {
            scan(visitor, fileObject);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        addItems(Category.HELPER, visitor.getHelpers());
    }

    private void addItems(CakePHP3Module.Category category, List<Pair<String, PhpClass>> phpClasses) {
        for (Pair<String, PhpClass> clazz : phpClasses) {
            PhpClass phpClass = clazz.second();
            FileObject file = phpClass.getFile();
            if (file == null) {
                continue;
            }
            switch (category) {
                case HELPER:
                    helpers.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<GoToItem> getSmart() {
        return getHelpers();
    }

    @Override
    public List<GoToItem> getHelpers() {
        List<GoToItem> items = new ArrayList<>(helpers);
        return items;
    }

    private void clear() {
        helpers.clear();
    }
}
