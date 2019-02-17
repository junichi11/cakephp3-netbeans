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
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.modules.ModuleInfo;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import org.netbeans.modules.php.cake3.utils.Inflector;
import org.openide.filesystems.FileObject;

public class EntityStatus extends CakePHP3GoToStatus {

    public EntityStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> items = new ArrayList<>(getTables());
        items.addAll(getTestCases());
        return items;
    }

    @Override
    public List<GoToItem> getTables() {
        FileObject fileObject = getFileObject();
        if (fileObject == null) {
            return Collections.emptyList();
        }
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fileObject);
        ModuleInfo info = cakeModule.createModuleInfo(fileObject);
        String name = fileObject.getName();
        Inflector inflector = Inflector.getInstance();
        String pluralizedName = inflector.pluralize(name);
        String relativePath = cakeModule.toPhpFileName(Category.TABLE, pluralizedName);
        FileObject file = cakeModule.getFile(info.getBase(), Category.TABLE, relativePath, info.getPluginName());
        if (file == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(GoToItemFactory.create(Category.TABLE, file, DEFAULT_OFFSET));
    }

}
