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

import java.util.Objects;
import javax.swing.Icon;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

class GoToDefaultItem implements GoToItem {

    private final FileObject fileObject;
    private final int offset;
    private final String baseName;
    private String offsetName = ""; // NOI18N

    public GoToDefaultItem(FileObject fileObject, int offset) {
        this(fileObject, offset, ""); // NOI18N
    }

    public GoToDefaultItem(FileObject fileObject, int offset, String baseName) {
        this.fileObject = fileObject;
        this.offset = offset;
        this.baseName = baseName;
    }

    @Override
    public String getBaseName() {
        return baseName;
    }

    @Override
    public FileObject getFileObject() {
        return fileObject;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon(CakePHP3Constants.CAKE_ICON_16, false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GoToDefaultItem other = (GoToDefaultItem) obj;
        if (!Objects.equals(this.fileObject, other.fileObject)) {
            return false;
        }
        return this.offset == other.offset;
    }

    public void setOffsetName(String offsetName) {
        this.offsetName = offsetName;
    }

    @Override
    public String toString() {
        FileObject parent = getFileObject().getParent();
        String nameWithExt = getFileObject().getNameExt();
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(baseName)) {
            sb.append("[").append(baseName).append("] "); // NOI18N
        }
        if (parent != null) {
            sb.append(parent.getName());
            sb.append("/"); // NOI18N
        }
        sb.append(nameWithExt);
        if (!StringUtils.isEmpty(offsetName)) {
            sb.append(":").append(offsetName); // NOI18N
        }
        return sb.toString();
    }

}
