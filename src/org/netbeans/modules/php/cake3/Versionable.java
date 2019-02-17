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

/**
 *
 * @author junichi11
 */
public interface Versionable {

    public static final String UNNKOWN = "UNKNOWN"; // NOI18N
    public static final String MAJOR = "major"; // NOI18N
    public static final String MINOR = "minor"; // NOI18N
    public static final String PATCH = "patch"; // NOI18N

    /**
     * Get a full version number.
     *
     * @return a full version number
     */
    public String getVersionNumber();

    /**
     * Get a major version number.
     *
     * @return a major version number
     */
    public int getMajor();

    /**
     * Get a minor version number.
     *
     * @return a minor version number
     */
    public int getMinor();

    /**
     * Get a patch version number.
     *
     * @return a patch version number
     */
    public int getPatch();
}
