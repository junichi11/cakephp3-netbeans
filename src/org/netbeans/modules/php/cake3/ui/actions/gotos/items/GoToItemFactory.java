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
package org.netbeans.modules.php.cake3.ui.actions.gotos.items;

import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class GoToItemFactory {

    private GoToItemFactory() {
    }

    public static GoToItem create(Category category, FileObject fileObject, int offset) {
        return create(category, fileObject, offset, "");
    }

    public static GoToItem create(Category category, FileObject fileObject, int offset, String offsetName) {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fileObject);
        CakePHP3Module.Base base = cakeModule.getBase(fileObject);
        String baseName = ""; // NOI18N
        switch (base) {
            case APP:
                baseName = cakeModule.getNameSpace();
                break;
            case CORE:
                baseName = "Core"; // NOI18N
                break;
            case PLUGIN:
                baseName = cakeModule.getPluginName(fileObject);
                break;
            default:
                break;
        }
        switch (category) {
            case BEHAVIOR:
                return new GoToBehaviorItem(fileObject, offset, baseName);
            case COMPONENT:
                return new GoToComponentItem(fileObject, offset, baseName);
            case CONTROLLER:
                GoToControllerItem controllerItem = new GoToControllerItem(fileObject, offset, baseName);
                controllerItem.setOffsetName(offsetName);
                return controllerItem;
            case ELEMENT:
                return new GoToElementItem(fileObject, offset, baseName);
            case ENTITY:
                return new GoToEntityItem(fileObject, offset, baseName);
            case FIXTURE:
                return new GoToFixtureItem(fileObject, offset, baseName);
            case HELPER:
                return new GoToHelperItem(fileObject, offset, baseName);
            case TABLE:
                return new GoToTableItem(fileObject, offset, baseName);
            case TEMPLATE:
                return new GoToTemplateItem(fileObject, offset, baseName);
            case TEMPLATE_CELL:
                return new GoToTemplateCellItem(fileObject, offset, baseName);
            case TEST_CASE:
                return new GoToTestCaseItem(fileObject, offset, baseName);
            case VIEW_CELL:
                GoToViewCellItem viewCellItem = new GoToViewCellItem(fileObject, offset, baseName);
                viewCellItem.setOffsetName(offsetName);
                return viewCellItem;
            default:
                return new GoToDefaultItem(fileObject, offset);
        }
    }
}
