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

import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class CakePHP3GoToStatusFactory {

    private static final CakePHP3GoToStatusFactory INSTANCE = new CakePHP3GoToStatusFactory();
//    private static final CakePHP3GoToStatus DUMMY_STATUS = new DefaultStatus(null, 0);

    private CakePHP3GoToStatusFactory() {
    }

    public static CakePHP3GoToStatusFactory getInstance() {
        return INSTANCE;
    }

    public CakePHP3GoToStatus create(FileObject fileObject, int offset) {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fileObject);
        Category category = cakeModule.getCategory(fileObject);
        CakePHP3GoToStatus status;
        switch (category) {
            case CONTROLLER: // fallthrough
            case VIEW_CELL:
                status = new ControllerStatus(fileObject, offset);
                break;
            case TEMPLATE: // fallthrough
            case TEMPLATE_CELL:
            case ELEMENT:
            case EMAIL:
            case ERROR:
            case PAGES:
            case LAYOUT:
                status = new TemplateStatus(fileObject, offset);
                break;
            case TEST_CASE:
                status = new TestCaseStatus(fileObject, offset);
                break;
            case TABLE:
                status = new TableStatus(fileObject, offset);
                break;
            case ENTITY:
                status = new EntityStatus(fileObject, offset);
                break;
            case COMPONENT:
                status = new ComponentStatus(fileObject, offset);
                break;
            case HELPER:
                status = new HelperStatus(fileObject, offset);
                break;
            default:
//                status = DUMMY_STATUS;
                status = new DefaultStatus(fileObject, offset);
                break;
        }
        return status;
    }

}
