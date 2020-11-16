/*
 * Copyright 2020 junichi11.
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
package org.netbeans.modules.php.cake3.modules;

import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.openide.filesystems.FileObject;

public class CakePHP4ModuleDefault extends CakePHP3ModuleDefault {

    public CakePHP4ModuleDefault(PhpModule phpModule) {
        super(phpModule);
    }

    @Override
    public boolean isTemplateFile(FileObject fileObject) {
        CakePHPModule.Category category = getCategory(fileObject);
        return ModuleUtils.isTemplate(category)
                && FileUtils.isPhpFile(fileObject);
    }

    @Override
    protected FileObject getDirectory(FileObject baseDirectory, CakePHPModule.Base base, CakePHPModule.Category category) {
        if (baseDirectory == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        switch (category) {
            case BEHAVIOR:
                sb.append(getSrcDirName(base));
                if (base == CakePHPModule.Base.CORE) {
                    sb.append("/ORM/Behavior"); // NOI18N
                } else {
                    sb.append("/Model/Behavior"); // NOI18N
                }
                break;
            case CONFIG:
                sb.append("config"); // NOI18N
                break;
            case CONSOLE:
                sb.append(getSrcDirName(base));
                sb.append("/Console"); // NOI18N
                break;
            case CONTROLLER:
                sb.append(getSrcDirName(base));
                sb.append("/Controller"); // NOI18N
                break;
            case COMPONENT:
                sb.append(getSrcDirName(base));
                sb.append("/Controller/Component"); // NOI18N
                break;
            case CSS:
                sb.append(getWWWRootPath(base));
                sb.append("/");
                sb.append(getCssPath(base));
                break;
            case DIR:
                sb.append(getSrcDirName(base));
                break;
            case ELEMENT:
                sb.append("templates/element"); // NOI18N
                break;
            case EMAIL:
                sb.append("templates/email"); // NOI18N
                break;
            case ENTITY:
                sb.append(getSrcDirName(base));
                sb.append("/Model/Entity"); // NOI18N
                break;
            case ERROR:
                sb.append("templates/Error"); // NOI18N
                break;
            case FIXTURE:
                sb.append("tests/Fixture"); // NOI18N
                break;
            case FORM:
                sb.append(getSrcDirName(base));
                sb.append("/Form"); // NOI18N
                break;
            case HELPER:
                sb.append(getSrcDirName(base));
                sb.append("/View/Helper"); // NOI18N
                break;
            case IMG:
                sb.append(getWWWRootPath(base));
                sb.append("/"); // NOI18N
                sb.append(getImagePath(base));
                break;
            case JS:
                sb.append(getWWWRootPath(base));
                sb.append("/"); // NOI18N
                sb.append(getJsPath(base));
                break;
            case LAYOUT:
                sb.append("templates/layout"); // NOI18N
                break;
            case LOCALE:
                sb.append(getSrcDirName(base));
                sb.append("/Locale"); // NOI18N
                break;
            case MODEL:
                sb.append(getSrcDirName(base));
                sb.append("/Model"); // NOI18N
                break;
            case PAGES:
                sb.append("templates/Pages"); // NOI18N
                break;
            case SHELL:
                sb.append(getSrcDirName(base));
                sb.append("/Shell"); // NOI18N
                break;
            case TABLE:
                sb.append(getSrcDirName(base));
                sb.append("/Model/Table"); // NOI18N
                break;
            case TASK:
                sb.append(getSrcDirName(base));
                sb.append("/Shell/Task"); // NOI18N
                break;
            case TEMPLATE:
                sb.append("templates"); // NOI18N
                break;
            case TEMPLATE_CELL:
                sb.append("templates/cell"); // NOI18N
                break;
            case TEST:
                sb.append("tests"); // NOI18N
                break;
            case TEST_CASE:
                sb.append("tests/TestCase"); // NOI18N
                break;
            case VIEW:
                sb.append(getSrcDirName(base));
                sb.append("/View"); // NOI18N
                break;
            case VIEW_CELL:
                sb.append(getSrcDirName(base));
                sb.append("/View/Cell"); // NOI18N
                break;
            case WEBROOT:
                sb.append(getWWWRootPath(base));
                break;
            default:
                throw new AssertionError();
        }
        String relativePath = sb.toString();
        return baseDirectory.getFileObject(relativePath);
    }

    @Override
    public String getCtpExt() {
        // CakePHP4 uses php as the template file extension
        return "php"; // NOI18N
    }

}
