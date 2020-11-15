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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.cake3.modules.CakePHPModule;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.openide.filesystems.FileObject;

public class TestCaseStatus extends CakePHP3GoToStatus {

    private final Set<GoToItem> testeds = new HashSet<>();

    public TestCaseStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
        if (fileObject.isFolder() || !FileUtils.isPhpFile(fileObject)) {
            return;
        }
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return;
        }
        String name = fileObject.getName();
        int lastIndexOfTest = name.lastIndexOf("Test"); // NOI18N
        String testedClassName = ""; // NOI18N
        if (lastIndexOfTest != -1) {
            testedClassName = name.substring(0, lastIndexOfTest);
        }
        CakePHPModule cakeModule = CakePHPModule.forPhpModule(phpModule);
        Set<ClassElement> classElements = getClassElements(sourceDirectory, testedClassName);
        for (ClassElement classElement : classElements) {
            FileObject fo = classElement.getFileObject();
            if (fo != null && fo != fileObject) {
                CakePHPModule.Category category = cakeModule.getCategory(fo);
                testeds.add(GoToItemFactory.create(category, fo, offset));
            }
        }
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> items = new ArrayList<>(testeds);
        return items;
    }

}
