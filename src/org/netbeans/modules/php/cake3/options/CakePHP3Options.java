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
package org.netbeans.modules.php.cake3.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.composer.api.Composer;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public class CakePHP3Options {

    private static final String PREFERENCES_PATH = "cakephp3"; // NOI18N
    private static final String EXTERNAL_DRAG_AND_DROP = "external.drop"; // NOI18N
    private static final String AVAILABLE_CUSTOM_NODES = "available-custom-nodes"; // NOI18N
    private static final String COMPOSER_PREFERENCES_PATH = "composer"; // NOI18N
    private static final CakePHP3Options INSTANCE = new CakePHP3Options();
    public static final List<String> DEFAULT_AVAILABLE_NODES = Arrays.asList(
            "Console", // NOI18N
            "Controller", // NOI18N
            "Component", // NOI18N
            "Model", // NOI18N
            "View", // NOI18N
            "Helper", // NOI18N
            "webroot" // NOI18N
    );
    public static final List<String> ALL_AVAILABLE_NODES = new ArrayList<>(DEFAULT_AVAILABLE_NODES);

    static {
        ALL_AVAILABLE_NODES.add("Element"); // NOI18N
        ALL_AVAILABLE_NODES.add("Entity"); // NOI18N
        ALL_AVAILABLE_NODES.add("Form"); // NOI18N
        ALL_AVAILABLE_NODES.add("Shell"); // NOI18N
        ALL_AVAILABLE_NODES.add("Table"); // NOI18N
        ALL_AVAILABLE_NODES.add("Template"); // NOI18N
        ALL_AVAILABLE_NODES.add("app/plugins"); // NOI18N
        Collections.sort(ALL_AVAILABLE_NODES);
    }

    private CakePHP3Options() {
    }

    public static CakePHP3Options getInstance() {
        return INSTANCE;
    }

    public boolean isExternalDragAndDrop() {
        return getPreferences().getBoolean(EXTERNAL_DRAG_AND_DROP, false);
    }

    public void setExternalDragAndDrop(boolean isEnabled) {
        getPreferences().putBoolean(EXTERNAL_DRAG_AND_DROP, isEnabled);
    }

    public String getComposerPath() {
        return getComposerPreferences().get("composer.path", ""); // NOI18N
    }

    public List<String> getAvailableCustomNodes() {
        String nodes = getPreferences().get(AVAILABLE_CUSTOM_NODES, null);
        if (nodes == null) {
            return DEFAULT_AVAILABLE_NODES;
        }
        return StringUtils.explode(nodes, "|"); // NOI18N
    }

    public void setAvailableCustomNodes(List<String> nodes) {
        getPreferences().put(AVAILABLE_CUSTOM_NODES, StringUtils.implode(nodes, "|")); // NOI18N
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(CakePHP3Options.class).node(PREFERENCES_PATH);
    }

    private Preferences getComposerPreferences() {
        return NbPreferences.forModule(Composer.class).node(COMPOSER_PREFERENCES_PATH); // NOI18N
    }
}
