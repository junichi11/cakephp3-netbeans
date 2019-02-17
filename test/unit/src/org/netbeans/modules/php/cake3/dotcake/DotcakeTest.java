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
package org.netbeans.modules.php.cake3.dotcake;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.php.cake3.dotcake.Dotcake.BuildPathCategory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class DotcakeTest extends NbTestCase {

    private FileObject dotcakeDirectory;

    public DotcakeTest(String name) {
        super(name);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    @Override
    public void setUp() {
        if (dotcakeDirectory == null) {
            File dataDir = getDataDir();
            FileObject dataDirectory = FileUtil.toFileObject(dataDir);
            dotcakeDirectory = dataDirectory.getFileObject("dotcake");
        }
    }

    @After
    @Override
    public void tearDown() {
        dotcakeDirectory = null;
    }

    /**
     * Test of fromJson method, of class Dotcake.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testFromJson() throws IOException {
        FileSystem fileSystem = FileUtil.createMemoryFileSystem();
        FileObject root = fileSystem.getRoot();
        FileObject dotcakeFile = dotcakeDirectory.getFileObject(".cake");

        // normal .cake file
        Dotcake dotcake = Dotcake.fromJson(dotcakeFile);
        assertNotNull(dotcake);
        if (dotcakeFile != null) {
            dotcakeFile = null;
        }

        // empty file
        dotcakeFile = root.createData(".cake");
        dotcake = Dotcake.fromJson(dotcakeFile);
        assertNull(dotcake);
        if (dotcakeFile != null) {
            dotcakeFile.delete();
            dotcakeFile = null;
        }

        dotcakeFile = root.createData(".dotcake");
        dotcake = Dotcake.fromJson(dotcakeFile);
        assertNull(dotcake);
        if (dotcakeFile != null) {
            dotcakeFile.delete();
            dotcakeFile = null;
        }

        dotcake = Dotcake.fromJson(null);
        assertNull(dotcake);
        if (dotcakeFile != null) {
            dotcakeFile.delete();
            dotcakeFile = null;
        }
    }

    /**
     * Test of isDotcake method, of class Dotcake.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testIsDotcake() throws IOException {
        FileSystem fileSystem = FileUtil.createMemoryFileSystem();
        FileObject root = fileSystem.getRoot();
        FileObject dotcakeFile = root.createData(".cake");
        assertTrue(Dotcake.isDotcake(dotcakeFile));
        if (dotcakeFile != null) {
            dotcakeFile.delete();
            dotcakeFile = null;
        }

        dotcakeFile = root.createData(".cake2");
        assertFalse(Dotcake.isDotcake(dotcakeFile));
        if (dotcakeFile != null) {
            dotcakeFile.delete();
            dotcakeFile = null;
        }

        assertFalse(Dotcake.isDotcake(null));

    }

    /**
     * Test of getBuildPaths method, of class Dotcake.
     */
    @Test
    public void testGetBuildPaths() {
        FileObject dotcakeFile = dotcakeDirectory.getFileObject(".cake");
        Dotcake dotcake = Dotcake.fromJson(dotcakeFile);
        List<String> result = dotcake.getBuildPaths(BuildPathCategory.TABLES);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.ENTITIES);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.BEHAVIORS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.CONTROLLERS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.COMPONENTS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.TEMPLATES);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.VIEWS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.HELPERS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.CONSOLES);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.SHELLS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.TASKS);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.LOCALES);
        assertTrue(!result.isEmpty());
        result = dotcake.getBuildPaths(BuildPathCategory.PLUGINS);
        assertTrue(!result.isEmpty());

        result = dotcake.getBuildPaths(BuildPathCategory.UNKNOWN);
        assertTrue(result.isEmpty());
        result = dotcake.getBuildPaths(null);
        assertTrue(result.isEmpty());
    }

}
