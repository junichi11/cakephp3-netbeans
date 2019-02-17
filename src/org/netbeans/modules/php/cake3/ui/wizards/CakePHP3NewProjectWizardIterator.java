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
package org.netbeans.modules.php.cake3.ui.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modules.php.api.PhpVersion;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleGenerator;
import org.netbeans.modules.php.cake3.commands.CakePHP3ProjectGenerator;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@TemplateRegistration(folder = "Project/PHP", displayName = "#CakePHP3NewProject_displayName", description = "CakePHP3NewProjectDescription.html", iconBase = "org/netbeans/modules/php/cake3/resources/cakephp_icon_16.png", position = 1900)
@Messages("CakePHP3NewProject_displayName=CakePHP3 Framework Application")
public class CakePHP3NewProjectWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;

    public CakePHP3NewProjectWizardIterator() {
    }

    public static CakePHP3NewProjectWizardIterator createIterator() {
        return new CakePHP3NewProjectWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new CakePHP3NewProjectWizardPanel(),};
    }

    private String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(CakePHP3NewProjectWizardIterator.class, "LBL_CreateProjectStep")
        };
    }

    @Override
    public Set<FileObject> instantiate(/*ProgressHandle handle*/) throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<>();
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty(CakePHP3NewProjectWizardPanel.PROP_PROJ_DIR));
        dirF.mkdirs();
        String name = (String) wiz.getProperty(CakePHP3NewProjectWizardPanel.PROP_NAME);

        // install via composer
        try {
            CakePHP3ProjectGenerator projectGenerator = CakePHP3ProjectGenerator.getDefault();
            projectGenerator.generate(dirF, name);
        } catch (InvalidPhpExecutableException ex) {
            // composer path is not set
            // check it in visual panel
            Exceptions.printStackTrace(ex);
        }
        FileObject dir = FileUtil.toFileObject(dirF);

        // Always open top dir as a project:
        resultSet.add(dir);

        // generate PhpModule
        PhpModuleGenerator generator = Lookup.getDefault().lookup(PhpModuleGenerator.class);
        if (generator != null) {
            PhpModuleGenerator.CreateProperties properties = new PhpModuleGenerator.CreateProperties()
                    .setName(name)
                    .setPhpVersion(PhpVersion.PHP_54)
                    .setCharset(Charset.forName("UTF-8")) // NOI18N
                    .setProjectDirectory(dirF)
                    .setSourcesDirectory(dirF);
            PhpModule phpModule = generator.createModule(properties);
            CakePHP3Preferences.setEnabled(phpModule, true);
        }

        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        return resultSet;
    }

    @Override
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                // Step name (actually the whole list for reference).
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
            }
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty(CakePHP3NewProjectWizardPanel.PROP_PROJ_DIR, null);
        this.wiz.putProperty(CakePHP3NewProjectWizardPanel.PROP_NAME, null);
        this.wiz.putProperty(CakePHP3NewProjectWizardPanel.PROP_PROJECT_TYPE, null);
        this.wiz = null;
        panels = null;
    }

    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[]{index + 1, panels.length});
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }

}
