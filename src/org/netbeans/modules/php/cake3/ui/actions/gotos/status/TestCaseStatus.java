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
package org.netbeans.modules.php.cake3.ui.actions.gotos.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
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
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        Set<ClassElement> classElements = getClassElements(sourceDirectory, testedClassName);
        for (ClassElement classElement : classElements) {
            FileObject fo = classElement.getFileObject();
            if (fo != null && fo != fileObject) {
                CakePHP3Module.Category category = cakeModule.getCategory(fo);
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
