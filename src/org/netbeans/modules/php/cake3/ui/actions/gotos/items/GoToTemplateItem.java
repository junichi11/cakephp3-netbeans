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

import javax.swing.Icon;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

public class GoToTemplateItem extends GoToDefaultItem {

    public GoToTemplateItem(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    public GoToTemplateItem(FileObject fileObject, int offset, String baseName) {
        super(fileObject, offset, baseName);
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon(CakePHP3Constants.GOTO_TEMPLATE_ICON, true);
    }

}
