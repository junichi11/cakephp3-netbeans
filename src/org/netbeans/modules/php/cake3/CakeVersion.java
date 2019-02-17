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
