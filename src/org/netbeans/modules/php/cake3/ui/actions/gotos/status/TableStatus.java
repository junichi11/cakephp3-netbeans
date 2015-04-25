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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.editor.visitors.TableVisitor;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItem;
import org.netbeans.modules.php.cake3.ui.actions.gotos.items.GoToItemFactory;
import static org.netbeans.modules.php.cake3.ui.actions.gotos.status.CakePHP3GoToStatus.DEFAULT_OFFSET;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Pair;

public class TableStatus extends CakePHP3GoToStatus {

    private final List<GoToItem> tables = new ArrayList<>();
    private final List<GoToItem> behaviors = new ArrayList<>();

    public TableStatus(FileObject fileObject, int offset) {
        super(fileObject, offset);
    }

    @Override
    protected void scan(PhpModule phpModule, FileObject fileObject, int offset) {
        clear();
        TableVisitor tableVisitor = new TableVisitor(phpModule);
        try {
            scan(tableVisitor, fileObject);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        addTables(tableVisitor.getTables());
        addBehaviors(tableVisitor.getBehaviors());

    }

    private void addTables(Set<Pair<String, PhpClass>> tables) {
        addItems(Category.TABLE, tables);
    }

    private void addBehaviors(Set<Pair<String, PhpClass>> behaviors) {
        addItems(Category.BEHAVIOR, behaviors);
    }

    private void addItems(CakePHP3Module.Category category, Collection<Pair<String, PhpClass>> phpClasses) {
        for (Pair<String, PhpClass> clazz : phpClasses) {
            PhpClass phpClass = clazz.second();
            FileObject file = phpClass.getFile();
            if (file == null) {
                continue;
            }
            switch (category) {
                case BEHAVIOR:
                    behaviors.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                case TABLE:
                    tables.add(GoToItemFactory.create(category, file, DEFAULT_OFFSET));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<GoToItem> getSmart() {
        List<GoToItem> items = new ArrayList<>(getEntities());
        items.addAll(tables);
        items.addAll(behaviors);
        items.addAll(getTestCases());
        return items;
    }

    @Override
    public List<GoToItem> getEntities() {
        List<GoToItem> items = new ArrayList<>();
        CakePHP3Module cakeModule = CakePHP3Module.forFileObject(getFileObject());
        FileObject entity = cakeModule.getEntity(getFileObject());
        if (entity != null) {
            items.add(GoToItemFactory.create(Category.ENTITY, entity, DEFAULT_OFFSET));
        }
        items.addAll(super.getEntities());
        return items;
    }

    @Override
    public List<GoToItem> getTables() {
        return new ArrayList<>(tables);
    }

    @Override
    public List<GoToItem> getBehaviors() {
        return new ArrayList<>(behaviors);
    }

    private void clear() {
        tables.clear();
        behaviors.clear();
    }
}
