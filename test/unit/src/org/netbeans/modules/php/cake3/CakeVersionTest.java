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
package org.netbeans.modules.php.cake3;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class CakeVersionTest extends NbTestCase {

    private FileObject versionsDirectory;

    public CakeVersionTest(String name) {
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
        if (versionsDirectory == null) {
            File dataDir = getDataDir();
            FileObject dataDirectory = FileUtil.toFileObject(dataDir);
            versionsDirectory = dataDirectory.getFileObject("versions");
        }
    }

    @After
    @Override
    public void tearDown() {
        versionsDirectory = null;
    }

    /**
     * Test of create method, of class CakeVersion.
     */
    @Test
    public void testVersionFile() {
        testVersionFile("VERSION-3.0.0.txt", new int[]{3, 0, 0}, "3.0.0");
        testVersionFile("VERSION-3.1.0-dev.txt", new int[]{3, 1, 0}, "3.1.0-dev");
        testVersionFile("VERSION-none.txt", new int[]{-1, -1, -1}, "UNKNOWN");
    }

    private void testVersionFile(String fileName, int[] numbers, String fullVersion) {
        FileObject versionFile = versionsDirectory.getFileObject(fileName);
        CakeVersion version = CakeVersion.create(versionFile);
        assertEquals(numbers[0], version.getMajor());
        assertEquals(numbers[1], version.getMinor());
        assertEquals(numbers[2], version.getPatch());
        assertEquals(fullVersion, version.getVersionNumber());
    }

}
