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
package org.netbeans.modules.php.cake3.dotcake;

import java.io.File;
import java.io.IOException;
import java.util.List;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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
