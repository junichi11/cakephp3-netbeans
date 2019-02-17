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
package org.netbeans.modules.php.cake3.preferences;

import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.phpmodule.PhpModule;

/**
 *
 * @author junichi11
 */
public final class CakePHP3Preferences {

    private static final String ENABLED = "enabled"; // NOI18N
    private static final String ROOT_PATH = "root-path"; // NOI18N
    private static final String SRC = "src"; // NOI18N
    private static final String IMAGE_URL = "image-url"; // NOI18N
    private static final String CSS_URL = "css-url"; // NOI18N
    private static final String JS_URL = "js-url"; // NOI18N
    private static final String WWW_ROOT = "www-root"; // NOI18N
    private static final String NAMESPACE = "namespace"; // NOI18N
    private static final String CTP_EXT = "ctp-ext"; // NOI18N
    private static final String DOTCAKE = "dotcake"; // NOI18N

    public static boolean isEnabled(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(ENABLED, false);
    }

    public static void setEnabled(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(ENABLED, isEnabled);
    }

    public static String getCtpExt(PhpModule phpModule) {
        return getPreferences(phpModule).get(CTP_EXT, "ctp"); // NOI18N
    }

    public static void setCtpExt(PhpModule phpModule, String ext) {
        getPreferences(phpModule).put(CTP_EXT, ext);
    }

    public static String getNamespace(PhpModule phpModule) {
        return getPreferences(phpModule).get(NAMESPACE, "App"); // NOI18N
    }

    public static void setNamespace(PhpModule phpModule, String namespace) {
        getPreferences(phpModule).put(NAMESPACE, namespace);
    }

    public static String getRootPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(ROOT_PATH, ""); // NOI18N
    }

    public static void setRootPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(ROOT_PATH, path);
    }

    public static String getSrcName(PhpModule phpModule) {
        return getPreferences(phpModule).get(SRC, "src");
    }

    public static void setSrcName(PhpModule phpModule, String srcName) {
        getPreferences(phpModule).put(SRC, srcName);
    }

    public static String getImageUrl(PhpModule phpModule) {
        return getPreferences(phpModule).get(IMAGE_URL, "img"); // NOI18N
    }

    public static void setImageUrl(PhpModule phpModule, String url) {
        getPreferences(phpModule).put(IMAGE_URL, url);
    }

    public static String getCssUrl(PhpModule phpModule) {
        return getPreferences(phpModule).get(CSS_URL, "css"); // NOI18N
    }

    public static void setCssUrl(PhpModule phpModule, String url) {
        getPreferences(phpModule).put(CSS_URL, url);
    }

    public static String getJsUrl(PhpModule phpModule) {
        return getPreferences(phpModule).get(JS_URL, "js"); // NOI18N
    }

    public static void setJsUrl(PhpModule phpModule, String url) {
        getPreferences(phpModule).put(JS_URL, url);
    }

    public static String getWWWRootPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(WWW_ROOT, "webroot"); // NOI18N
    }

    public static void setWWWRootPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(WWW_ROOT, path);
    }

    public static String getDotcakePath(PhpModule phpModule) {
        return getPreferences(phpModule).get(DOTCAKE, ""); // NOI18N
    }

    public static void setDotcakePath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(DOTCAKE, path);
    }

    private static Preferences getPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(CakePHP3Preferences.class, true);
    }
}
