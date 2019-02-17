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
package org.netbeans.modules.php.cake3.modules;

import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Base;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module.Category;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public interface ModuleInfo {

    public FileObject getFileObject();

    public Base getBase();

    public Category getCategory();

    public String getPluginName();

}
