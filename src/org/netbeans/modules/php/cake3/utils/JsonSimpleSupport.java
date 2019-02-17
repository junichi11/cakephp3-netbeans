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
package org.netbeans.modules.php.cake3.utils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author junichi11
 */
public final class JsonSimpleSupport {

    private static final Logger LOGGER = Logger.getLogger(JsonSimpleSupport.class.getName());

    private JsonSimpleSupport() {
    }

    public static <T> T fromJson(Reader reader, Class<T> type) throws IOException {
        JSONParser parser = new JSONParser();
        if (reader == null || type == null) {
            return null;
        }
        Object object = null;
        try {
            object = parser.parse(reader);
        } catch (ParseException ex) {
            LOGGER.log(Level.INFO, "Can't parse json file");
        }
        if (object == null) {
            return null;
        }
        return fromJson(object, type);
    }

    private static <T> T fromJson(Object o, Class<T> type) {
        if (type == String.class
                || type == Integer.class
                || type == Map.class
                || type == Long.class
                || type == List.class) {
            return type.cast(o);
        }
        JSONObject jsonObject = (JSONObject) o;
        T instance = null;
        try {
            instance = type.newInstance();
            for (Object key : jsonObject.keySet()) {
                try {
                    Field field = type.getDeclaredField((String) key);
                    Object value = fromJson(jsonObject.get(key), field.getType());
                    field.setAccessible(true);
                    field.set(instance, value);
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return instance;
    }

}
