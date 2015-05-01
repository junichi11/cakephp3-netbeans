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
