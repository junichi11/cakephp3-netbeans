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
package org.netbeans.modules.php.cake3.editor.completion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.filesystems.FileObject;
import org.openide.util.Pair;

/**
 *
 * @author junichi11
 */
public abstract class Parameter {

    public enum Type {

        Path("path"), // NOI18N
        Constant("constant"), // NOI18N
        UNKNOWN("unknown"); // NOI18N
        private final String type;
        private static final Map<String, Type> TYPES = new HashMap<>();

        static {
            for (Type value : values()) {
                TYPES.put(value.getType(), value);
            }
        }

        private Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static Type get(String type) {
            Type t = TYPES.get(type);
            return t == null ? UNKNOWN : t;
        }

    }

    private final int position;
    private final String className;
    private final String methodName;
    private final FileObject fileObject;
    // XXX add more encodings(HtmlHelper)?
    private static final Map<String, Map<String, Map<String, List<String>>>> CONSTANTS = new HashMap<>();
    // XXX add media?
    private static final Map<String, Map<String, Map<String, List<String>>>> PATHS = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(Parameter.class.getName());
    private static final Map<String, Map<String, Map<String, Map<String, List<String>>>>> RESOURCES = new HashMap<>();
    private static final List<Pair<Type, Map<String, Map<String, Map<String, List<String>>>>>> ALL_ITEMS = Arrays.asList(
            Pair.of(Type.Constant, CONSTANTS),
            Pair.of(Type.Path, PATHS)
    );

    static {
        RESOURCES.put("resources/constant-parameters.json", CONSTANTS); // NOI18N
        RESOURCES.put("resources/filepath-parameters.json", PATHS); // NOI18N
        for (Map.Entry<String, Map<String, Map<String, Map<String, List<String>>>>> entrySet : RESOURCES.entrySet()) {
            String key = entrySet.getKey();
            Map<String, Map<String, Map<String, List<String>>>> value = entrySet.getValue();
            loadValues(value, key);
        }
    }

    Parameter(int position, String className, String methodName, FileObject fileObject) {
        this.position = position;
        this.className = className;
        this.methodName = methodName;
        this.fileObject = fileObject;
    }

    public int getPosition() {
        return position;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public abstract List<CompletionItem> getCompletionItems(String filter, int caretOffset);

    @CheckForNull
    public static Parameter create(int parameterIndex, String className, String methodName, FileObject fileObject) {
        return ParameterFactory.getInstance().create(parameterIndex, className, methodName, fileObject);
    }

    public static Type getType(int position, String className, String methodName) {
        for (Pair<Type, Map<String, Map<String, Map<String, List<String>>>>> item : ALL_ITEMS) {
            List<String> elements = getElements(item.second(), position, className, methodName);
            if (!elements.isEmpty()) {
                return item.first();
            }
        }
        return Type.UNKNOWN;
    }

    static List<String> getElements(int position, String className, String methodName) {
        for (Pair<Type, Map<String, Map<String, Map<String, List<String>>>>> item : ALL_ITEMS) {
            List<String> elements = getElements(item.second(), position, className, methodName);
            if (!elements.isEmpty()) {
                return elements;
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getElements(Map<String, Map<String, Map<String, List<String>>>> map, int position, String className, String methodName) {
        Map<String, Map<String, List<String>>> clazz = map.get(className);
        if (clazz == null) {
            return Collections.emptyList();
        }
        Map<String, List<String>> method = clazz.get(methodName);
        if (method == null) {
            return Collections.emptyList();
        }
        List<String> params = method.get(String.valueOf(position));
        if (params == null) {
            return Collections.emptyList();
        }
        return params;
    }

    private static void loadValues(Map<String, Map<String, Map<String, List<String>>>> map, String resourcePath) {
        try (InputStream is = ConstantParameter.class.getResourceAsStream(resourcePath)) {
            try (Reader reader = new InputStreamReader(is, CakePHP3Constants.UTF8)) {
                JSONParser parser = new JSONParser();
                ContainerFactory containerFactory = new ContainerFactory() {
                    @Override
                    public List creatArrayContainer() {
                        return new LinkedList();
                    }

                    @Override
                    public Map createObjectContainer() {
                        return new LinkedHashMap();
                    }
                };
                try {
                    map.putAll((Map) parser.parse(reader, containerFactory));
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

}
