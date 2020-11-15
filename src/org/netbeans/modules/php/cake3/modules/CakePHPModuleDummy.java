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
package org.netbeans.modules.php.cake3.modules;

import java.util.Collections;
import java.util.List;
import org.openide.filesystems.FileObject;

public class CakePHPModuleDummy extends CakePHPModuleImpl {

    CakePHPModuleDummy() {
        super(null);
    }

    @Override
    public boolean isTemplateFile(FileObject fileObject) {
        return false;
    }

    @Override
    public List<FileObject> getDirectories(CakePHPModule.Base base) {
        return Collections.emptyList();
    }

    @Override
    public List<FileObject> getDirectories(CakePHPModule.Base base, CakePHPModule.Category category, String pluginName) {
        return Collections.emptyList();
    }

    @Override
    public FileObject getController(FileObject template) {
        return null;
    }

    @Override
    public FileObject getController(FileObject template, boolean fallback) {
        return null;
    }

    @Override
    public FileObject getViewCell(FileObject template) {
        return null;
    }

    @Override
    public FileObject getTemplate(String relativePath, FileObject controller, String themeName) {
        return null;
    }

    @Override
    public FileObject getEntity(FileObject table) {
        return null;
    }

}
