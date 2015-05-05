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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.modules.ModuleInfo;
import org.netbeans.modules.php.cake3.modules.ModuleUtils;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class CakePHP3GoToStatus {

    private final FileObject fileObject;
    private final int offset;
    static final int DEFAULT_OFFSET = 0;

    CakePHP3GoToStatus(FileObject fileObject, int offset) {
        this.fileObject = fileObject;
        this.offset = offset;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public int getOffset() {
        return offset;
    }

    public void scan() {
        if (fileObject == null) {
            return;
        }
        PhpModule phpModule = getPhpModule();
        if (phpModule == null) {
            return;
        }
        scan(phpModule, getFileObject(), offset);
    }

    public List<GoToItem> getAllItems() {
        List<GoToItem> items = new ArrayList<>();
        items.addAll(getControllers());
        items.addAll(getTables());
        items.addAll(getEntities());
        items.addAll(getTemplates());
        items.addAll(getComponents());
        items.addAll(getHelpers());
        items.addAll(getBehaviors());
        items.addAll(getTestCases());
        items.addAll(getFixtures());
        return items;
    }

    public List<GoToItem> getControllers() {
        return Collections.emptyList();
    }

    /**
     * Get all controllers for the base directory.
     *
     * @return
     */
    public List<GoToItem> getAllControllers() {
        return createAllItems(Category.CONTROLLER);
    }

    public List<GoToItem> getTables() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAllTables() {
        return createAllItems(Category.TABLE);
    }

    public List<GoToItem> getEntities() {
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(getFileObject());
        List<GoToItem> items = new ArrayList<>();
        for (GoToItem item : getTables()) {
            FileObject table = item.getFileObject();
            FileObject file = cakeModule.getEntity(table);
            if (file != null) {
                items.add(GoToItemFactory.create(Category.ENTITY, file, DEFAULT_OFFSET));
            }
        }
        return items;
    }

    public List<GoToItem> getAllEntities() {
        return createAllItems(Category.ENTITY);
    }

    public List<GoToItem> getTemplates() {
        return Collections.emptyList();
    }

    public List<GoToItem> getComponents() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAllComponents() {
        return createAllItems(Category.COMPONENT);
    }

    public List<GoToItem> getHelpers() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAllHelpers() {
        return createAllItems(Category.HELPER);
    }

    public List<GoToItem> getBehaviors() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAllBehaviors() {
        return createAllItems(Category.BEHAVIOR);
    }

    public List<GoToItem> getTestCases() {
        if (fileObject == null || !FileUtils.isPhpFile(fileObject)) {
            return Collections.emptyList();
        }
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fileObject);
        ModuleInfo info = cakeModule.createModuleInfo(fileObject);
        if (ModuleUtils.isTemplate(info.getCategory())) {
            return Collections.emptyList();
        }

        List<GoToItem> items = new ArrayList<>();
        List<FileObject> directories = cakeModule.getDirectories(info.getBase(), null, info.getPluginName());
        for (FileObject directory : directories) {
            Set<ClassElement> classElements = getClassElements(directory, fileObject.getName() + "Test"); // NOI18N
            for (ClassElement classElement : classElements) {
                FileObject testcase = classElement.getFileObject();
                items.add(GoToItemFactory.create(Category.TEST_CASE, testcase, DEFAULT_OFFSET));
            }
        }
        return items;
    }

    public List<GoToItem> getAllTestCases() {
        return createAllItems(Category.TEST_CASE);
    }

    public List<GoToItem> getFixtures() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAllFixtures() {
        return createAllItems(Category.FIXTURE);
    }

    public List<GoToItem> getImportants() {
        return createAllItems(Category.CONFIG);
    }

    public List<GoToItem> getViewCells() {
        return Collections.emptyList();
    }

    public List<GoToItem> getAllViewCells() {
        return createAllItems(Category.VIEW_CELL);
    }

    protected List<GoToItem> createAllItems(Category category) {
        if (fileObject == null) {
            return Collections.emptyList();
        }
        List<GoToItem> items = new ArrayList<>();
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(fileObject);
        ModuleInfo info = cakeModule.createModuleInfo(fileObject);
        List<FileObject> directories = cakeModule.getDirectories(info.getBase(), category, info.getPluginName());
        for (FileObject directory : directories) {
            for (FileObject child : directory.getChildren()) {
                if (child.isFolder() || !FileUtils.PHP_MIME_TYPE.equals(child.getMIMEType(FileUtils.PHP_MIME_TYPE))) {
                    continue;
                }
                items.add(GoToItemFactory.create(category, child, 0));
            }
        }
        return items;
    }

    /**
     * Get class elements.
     *
     * @param targetDirectory
     * @param targetName class name
     * @return class elements
     */
    public Set<ClassElement> getClassElements(FileObject targetDirectory, String targetName) {
        if (targetDirectory == null || !targetDirectory.isFolder() || StringUtils.isEmpty(targetName)) {
            return Collections.emptySet();
        }

        ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(targetDirectory));
        return indexQuery.getClasses(NameKind.prefix(targetName));
    }

    private PhpModule getPhpModule() {
        if (fileObject == null) {
            return null;
        }
        return PhpModule.Factory.forFileObject(fileObject);
    }

    protected void scan(final DefaultVisitor visitor, FileObject targetFile) throws ParseException {
        ParserManager.parse(Collections.singleton(Source.create(targetFile)), new UserTask() {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
                ParserResult result = (ParserResult) resultIterator.getParserResult();
                if (result == null) {
                    return;
                }
                if (result instanceof PHPParseResult) {
                    PHPParseResult pr = (PHPParseResult) result;
                    pr.getProgram().accept(visitor);
                }
            }
        });
    }

    /**
     * Scan a file.
     *
     * @param phpModule
     * @param fileObject
     * @param offset
     */
    protected abstract void scan(PhpModule phpModule, FileObject fileObject, int offset);

    /**
     * Get GoToItems for the caret position.
     *
     * @return GoToItems
     */
    public abstract List<GoToItem> getSmart();

}
