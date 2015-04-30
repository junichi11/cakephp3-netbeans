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
package org.netbeans.modules.php.cake3;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class CakeVersion implements Versionable {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^(?<major>\\d)\\.(?<minor>\\d)\\.(?<patch>\\d)(?<dev>.*)$"); // NOI18N

    private String version = UNNKOWN;
    private int major = -1;
    private int minor = -1;
    private int patch = -1;

    private CakeVersion() {
    }

    private CakeVersion major(int major) {
        this.major = major;
        return this;
    }

    private CakeVersion minor(int minor) {
        this.minor = minor;
        return this;
    }

    private CakeVersion patch(int patch) {
        this.patch = patch;
        return this;
    }

    private CakeVersion version(String version) {
        this.version = version;
        return this;
    }

    public static CakeVersion create(FileObject file) {
        CakeVersion version = new CakeVersion();
        if (file == null || file.isFolder()) {
            return version;
        }
        try {
            List<String> lines = file.asLines(CakePHP3Constants.UTF8);
            for (String line : lines) {
                Matcher matcher = VERSION_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    continue;
                }
                int major = Integer.parseInt(matcher.group(MAJOR));
                int minor = Integer.parseInt(matcher.group(MINOR));
                int patch = Integer.parseInt(matcher.group(PATCH));
                version.major(major)
                        .minor(minor)
                        .patch(patch)
                        .version(line);
                break;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return version;
    }

    @Override
    public String getVersionNumber() {
        return version;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public int getPatch() {
        return patch;
    }

}
