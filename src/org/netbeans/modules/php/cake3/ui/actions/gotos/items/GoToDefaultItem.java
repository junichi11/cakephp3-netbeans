/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
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
