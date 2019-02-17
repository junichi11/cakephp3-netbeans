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
@TemplateRegistrations({
    @TemplateRegistration(
            position = 100,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.CTP_ICON,
            displayName = "#Ctp_DisplayName",
            content = "CtpTemplate.ctp",
            description = "CtpTemplateDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 110,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Controller_DisplayName",
            content = "Controller.php",
            description = "ControllerDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 120,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Table_DisplayName",
            content = "Table.php",
            description = "TableDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 130,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Entity_DisplayName",
            content = "Entity.php",
            description = "EntityDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 200,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Helper_DisplayName",
            content = "Helper.php",
            description = "HelperDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 300,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Component_DisplayName",
            content = "Component.php",
            description = "ComponentDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 400,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Behavior_DisplayName",
            content = "Behavior.php",
            description = "BehaviorDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 500,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Shell_DisplayName",
            content = "Shell.php",
            description = "ShellDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 600,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Task_DisplayName",
            content = "Task.php",
            description = "TaskDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            position = 700,
            folder = CakePHP3Constants.CAKEPHP3_FRAMEWORK,
            iconBase = CakePHP3Constants.PHP_ICON,
            displayName = "#Cell_DisplayName",
            content = "Cell.php",
            description = "CellDescription.html",
            scriptEngine = "freemarker")
})
@NbBundle.Messages({
    "Controller_DisplayName=CakePHP3 Controller",
    "Table_DisplayName=CakePHP3 Table",
    "Entity_DisplayName=CakePHP3 Entity",
    "Component_DisplayName=CakePHP3 Component",
    "Helper_DisplayName=CakePHP3 Helper",
    "Behavior_DisplayName=CakePHP3 Behavior",
    "Shell_DisplayName=CakePHP3 Shell",
    "Task_DisplayName=CakePHP3 Task",
    "Cell_DisplayName=CakePHP3 View Cell",
    "Ctp_DisplayName=Empty ctp file"})

package org.netbeans.modules.php.cake3.resources.templates;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.modules.php.cake3.CakePHP3Constants;
import org.openide.util.NbBundle;
