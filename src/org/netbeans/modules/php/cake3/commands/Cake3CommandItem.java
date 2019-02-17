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
package org.netbeans.modules.php.cake3.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junichi11
 */
public class Cake3CommandItem {

    private final String command;
    private final String description;
    private final String displayName;
    private final List<Cake3CommandItem> subcommands = new ArrayList<>();

    public Cake3CommandItem(String command, String description, String displayName) {
        this.command = command;
        this.description = description;
        this.displayName = displayName;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Cake3CommandItem> getSubcommands() {
        return subcommands;
    }

    public void addSubcommand(Cake3CommandItem subcommand) {
        this.subcommands.add(subcommand);
    }
}
