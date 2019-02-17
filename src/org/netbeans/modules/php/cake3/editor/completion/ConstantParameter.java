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
package org.netbeans.modules.php.cake3.editor.completion;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;

public class ConstantParameter extends Parameter {

    ConstantParameter(int position, String className, String methodName, FileObject fileObject) {
        super(position, className, methodName, fileObject);
    }

    @Override
    public List<CompletionItem> getCompletionItems(String filter, int caretOffset) {
        List<CompletionItem> items = new ArrayList<>();
        for (String element : getElements(getPosition(), getClassName(), getMethodName())) {
            if (element.startsWith(filter)) {
                items.add(new CakePHP3CompletionItem(element, filter, caretOffset));
            }
        }
        return items;
    }

}
