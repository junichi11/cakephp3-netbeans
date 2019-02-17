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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public class ModuleUtilsTest {

    public ModuleUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isTemplate method, of class ModuleUtils.
     */
    @Test
    public void testIsTemplate() {
        assertTrue(ModuleUtils.isTemplate(Category.TEMPLATE));
        assertTrue(ModuleUtils.isTemplate(Category.TEMPLATE_CELL));
        assertTrue(ModuleUtils.isTemplate(Category.EMAIL));
        assertTrue(ModuleUtils.isTemplate(Category.ELEMENT));
        assertTrue(ModuleUtils.isTemplate(Category.ERROR));
        assertTrue(ModuleUtils.isTemplate(Category.PAGES));
        assertTrue(ModuleUtils.isTemplate(Category.LAYOUT));

        assertFalse(ModuleUtils.isTemplate(Category.BEHAVIOR));
        assertFalse(ModuleUtils.isTemplate(Category.COMPONENT));
        assertFalse(ModuleUtils.isTemplate(Category.CONFIG));
        assertFalse(ModuleUtils.isTemplate(Category.CONSOLE));
        assertFalse(ModuleUtils.isTemplate(Category.CONTROLLER));
        assertFalse(ModuleUtils.isTemplate(Category.CSS));
        assertFalse(ModuleUtils.isTemplate(Category.DIR));
        assertFalse(ModuleUtils.isTemplate(Category.ENTITY));
        assertFalse(ModuleUtils.isTemplate(Category.FIXTURE));
        assertFalse(ModuleUtils.isTemplate(Category.HELPER));
        assertFalse(ModuleUtils.isTemplate(Category.IMG));
        assertFalse(ModuleUtils.isTemplate(Category.JS));
        assertFalse(ModuleUtils.isTemplate(Category.LOCALE));
        assertFalse(ModuleUtils.isTemplate(Category.MODEL));
        assertFalse(ModuleUtils.isTemplate(Category.SHELL));
        assertFalse(ModuleUtils.isTemplate(Category.TABLE));
        assertFalse(ModuleUtils.isTemplate(Category.TASK));
        assertFalse(ModuleUtils.isTemplate(Category.TEST));
        assertFalse(ModuleUtils.isTemplate(Category.TEST_CASE));
        assertFalse(ModuleUtils.isTemplate(Category.UNKNOWN));
        assertFalse(ModuleUtils.isTemplate(Category.VIEW));
        assertFalse(ModuleUtils.isTemplate(Category.VIEW_CELL));
        assertFalse(ModuleUtils.isTemplate(Category.WEBROOT));
    }

    /**
     * Test of toClassName method, of class ModuleUtils.
     */
    @Test
    public void testToClassName() {
        assertEquals("TreeBehavior", ModuleUtils.toClassName("Tree", Category.BEHAVIOR));
        assertEquals("UsersController", ModuleUtils.toClassName("Users", Category.CONTROLLER));
        assertEquals("UsersComponent", ModuleUtils.toClassName("Users", Category.COMPONENT));
        assertEquals("UsersFixture", ModuleUtils.toClassName("Users", Category.FIXTURE));
        assertEquals("HtmlHelper", ModuleUtils.toClassName("Html", Category.HELPER));
        assertEquals("BakeShell", ModuleUtils.toClassName("Bake", Category.SHELL));
        assertEquals("UsersTable", ModuleUtils.toClassName("Users", Category.TABLE));
        assertEquals("BakeTask", ModuleUtils.toClassName("Bake", Category.TASK));
        assertEquals("BakeTest", ModuleUtils.toClassName("Bake", Category.TEST_CASE));
        assertEquals("JsonView", ModuleUtils.toClassName("Json", Category.VIEW));
        assertEquals("InboxCell", ModuleUtils.toClassName("Inbox", Category.VIEW_CELL));

        List<Category> categories = Arrays.asList(
                Category.CONFIG,
                Category.CSS,
                Category.DIR,
                Category.ELEMENT,
                Category.EMAIL,
                Category.ENTITY,
                Category.ERROR,
                Category.IMG,
                Category.JS,
                Category.LAYOUT,
                Category.LOCALE,
                Category.MODEL,
                Category.PAGES,
                Category.TEMPLATE,
                Category.TEMPLATE_CELL,
                Category.TEST,
                Category.UNKNOWN,
                Category.WEBROOT
        );
        for (Category category : categories) {
            assertEquals("tests", ModuleUtils.toClassName("tests", category));
        }

        for (Category value : Category.values()) {
            assertEquals("", ModuleUtils.toCommonName("", value));
        }
    }

    /**
     * Test of toCommonName method, of class ModuleUtils.
     */
    @Test
    public void testToCommonName() {
        assertEquals("Tree", ModuleUtils.toCommonName("TreeBehavior", Category.BEHAVIOR));
        assertEquals("Users", ModuleUtils.toCommonName("UsersController", Category.CONTROLLER));
        assertEquals("Users", ModuleUtils.toCommonName("UsersComponent", Category.COMPONENT));
        assertEquals("Users", ModuleUtils.toCommonName("UsersFixture", Category.FIXTURE));
        assertEquals("Html", ModuleUtils.toCommonName("HtmlHelper", Category.HELPER));
        assertEquals("Bake", ModuleUtils.toCommonName("BakeShell", Category.SHELL));
        assertEquals("Users", ModuleUtils.toCommonName("UsersTable", Category.TABLE));
        assertEquals("Bake", ModuleUtils.toCommonName("BakeTask", Category.TASK));
        assertEquals("Bake", ModuleUtils.toCommonName("BakeTest", Category.TEST_CASE));
        assertEquals("Json", ModuleUtils.toCommonName("JsonView", Category.VIEW));
        assertEquals("Inbox", ModuleUtils.toCommonName("InboxCell", Category.VIEW_CELL));

        List<Category> categories = Arrays.asList(
                Category.CONFIG,
                Category.CSS,
                Category.DIR,
                Category.ELEMENT,
                Category.EMAIL,
                Category.ENTITY,
                Category.ERROR,
                Category.IMG,
                Category.JS,
                Category.LAYOUT,
                Category.LOCALE,
                Category.MODEL,
                Category.PAGES,
                Category.TEMPLATE,
                Category.TEMPLATE_CELL,
                Category.TEST,
                Category.UNKNOWN,
                Category.WEBROOT
        );
        for (Category category : categories) {
            assertEquals("tests", ModuleUtils.toCommonName("tests", category));
        }

        for (Category value : Category.values()) {
            assertEquals("", ModuleUtils.toCommonName("", value));
        }
    }

    /**
     * Test of isChild method, of class ModuleUtils.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testIsChild_FileObject_FileObject() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject root = fs.getRoot();
        FileObject parent1 = root.createFolder("parent1");
        FileObject child1 = parent1.createData("child1.txt");
        FileObject parent2 = root.createFolder("parent2");
        assertTrue(ModuleUtils.isChild(parent1, child1));

        assertFalse(ModuleUtils.isChild(parent2, child1));
        assertFalse(ModuleUtils.isChild(null, child1));
        assertFalse(ModuleUtils.isChild(parent2, (FileObject) null));
        assertFalse(ModuleUtils.isChild(child1, parent1));
        child1.delete();
        parent1.delete();
        parent2.delete();
    }

    /**
     * Test of appendPluignName method, of class ModuleUtils.
     */
    @Test
    public void testAppendPluignName() {
        assertEquals("DebugKit.Toolbar", ModuleUtils.appendPluignName("DebugKit", "Toolbar"));

        assertEquals("Toolbar", ModuleUtils.appendPluignName("", "Toolbar"));
        assertEquals("Toolbar", ModuleUtils.appendPluignName(null, "Toolbar"));
        assertEquals("", ModuleUtils.appendPluignName("", ""));
        assertEquals("", ModuleUtils.appendPluignName("DebugKit", ""));
        assertEquals(null, ModuleUtils.appendPluignName("DebugKit", null));
    }

    /**
     * Test of pluginSplit method, of class ModuleUtils.
     */
    @Test
    public void testPluginSplit() {
        assertEquals(Pair.of("DebugKit", "Toolbar"), ModuleUtils.pluginSplit("DebugKit.Toolbar"));
        assertEquals(Pair.of("", "Toolbar"), ModuleUtils.pluginSplit("Toolbar"));

        assertEquals(Pair.of("", ""), ModuleUtils.pluginSplit(null));
        assertEquals(Pair.of("", ""), ModuleUtils.pluginSplit(""));
    }

    /**
     * Test of cellMethodSplit method, of class ModuleUtils.
     */
    @Test
    public void testMethodSplit() {
        assertEquals(Pair.of("Inbox", "expanded"), ModuleUtils.cellMethodSplit("Inbox::expanded"));
        assertEquals(Pair.of("Inbox", ""), ModuleUtils.cellMethodSplit("Inbox"));

        assertEquals(Pair.of("Inbox.expanded", ""), ModuleUtils.cellMethodSplit("Inbox.expanded"));
        assertEquals(Pair.of("", ""), ModuleUtils.cellMethodSplit(null));
        assertEquals(Pair.of("", ""), ModuleUtils.cellMethodSplit(""));
    }
}
