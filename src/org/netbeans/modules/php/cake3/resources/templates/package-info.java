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
