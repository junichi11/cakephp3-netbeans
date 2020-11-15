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
package org.netbeans.modules.php.cake3;

import java.io.File;
import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.cake3.modules.CakePHPModuleFactory;
import org.netbeans.modules.php.cake3.preferences.CakePHP3Preferences;
import org.netbeans.modules.php.cake3.ui.customizer.CakePHP3CustomizerPanel;
import org.netbeans.modules.php.cake3.validators.CakePHP3CustomizerValidator;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CakePHP3ModuleCustomizerExtender extends PhpModuleCustomizerExtender {

    private String errorMessage;
    private CakePHP3CustomizerPanel panel;
    private final boolean isEnabled;
    private final String namespace;
    private final String root;
    private final String src;
    private final String wwwRoot;
    private final String css;
    private final String img;
    private final String js;
    private final String dotcake;
    private final File sourceDirectory;

    public CakePHP3ModuleCustomizerExtender(PhpModule phpModule) {
        isEnabled = CakePHP3Preferences.isEnabled(phpModule);
        namespace = CakePHP3Preferences.getNamespace(phpModule);
        root = CakePHP3Preferences.getRootPath(phpModule);
        src = CakePHP3Preferences.getSrcName(phpModule);
        wwwRoot = CakePHP3Preferences.getWWWRootPath(phpModule);
        css = CakePHP3Preferences.getCssUrl(phpModule);
        img = CakePHP3Preferences.getImageUrl(phpModule);
        js = CakePHP3Preferences.getJsUrl(phpModule);
        dotcake = CakePHP3Preferences.getDotcakePath(phpModule);
        FileObject srcDir = phpModule.getSourceDirectory();
        sourceDirectory = srcDir != null ? FileUtil.toFile(srcDir) : null;
    }

    @NbBundle.Messages({
        "CakePHP3ModuleCustomizerExtender.displayName=CakePHP3"
    })
    @Override
    public String getDisplayName() {
        return Bundle.CakePHP3ModuleCustomizerExtender_displayName();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getPanel().removeChangeListener(listener);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        validate();
        return errorMessage;
    }

    private void validate() {
        if (getPanel().isCakePHP3Enabled()) {
            CakePHP3CustomizerValidator validator = new CakePHP3CustomizerValidator();
            FileObject srcDir = FileUtil.toFileObject(sourceDirectory);
            String rootPath = getPanel().getRoot();
            String srcPath = String.format("%s/%s", rootPath, getPanel().getSrc()).replaceAll("/+", "/");
            String wwwRootPath = String.format("%s/%s", rootPath, getPanel().getWWWRoot()).replaceAll("/+", "/");
            String cssPath = String.format("%s/%s", wwwRootPath, getPanel().getCss()).replaceAll("/+", "/");
            String imgPath = String.format("%s/%s", wwwRootPath, getPanel().getImg()).replaceAll("/+", "/");
            String jsPath = String.format("%s/%s", wwwRootPath, getPanel().getJs()).replaceAll("/+", "/");
            String dotcakePath = getPanel().getDotcakePath();
            ValidationResult result = validator.validateRootPath(srcDir, rootPath)
                    .validateSrc(srcDir, srcPath)
                    .validateWWWRoot(srcDir, wwwRootPath)
                    .validateCss(srcDir, cssPath)
                    .validateImg(srcDir, imgPath)
                    .validateJs(srcDir, jsPath)
                    .validateDotcake(srcDir, dotcakePath)
                    .getResult();
            if (result.hasErrors()) {
                errorMessage = result.getErrors().get(0).getMessage();
                return;
            }
            if (result.hasWarnings()) {
                errorMessage = result.getWarnings().get(0).getMessage();
                return;
            }
        }

        // everything ok
        errorMessage = null;
    }

    @Override
    public EnumSet<Change> save(PhpModule phpModule) {
        CakePHP3CustomizerPanel p = getPanel();
        CakePHP3Preferences.setEnabled(phpModule, p.isCakePHP3Enabled());
        CakePHP3Preferences.setRootPath(phpModule, p.getRoot());
        CakePHP3Preferences.setSrcName(phpModule, p.getSrc());
        CakePHP3Preferences.setNamespace(phpModule, p.getNamespace());
        CakePHP3Preferences.setWWWRootPath(phpModule, p.getWWWRoot());
        CakePHP3Preferences.setCssUrl(phpModule, p.getCss());
        CakePHP3Preferences.setImageUrl(phpModule, p.getImg());
        CakePHP3Preferences.setJsUrl(phpModule, p.getJs());
        CakePHP3Preferences.setDotcakePath(phpModule, p.getDotcakePath());
        // release CakePHP3Module
        CakePHPModuleFactory.getInstance().remove(phpModule);
        return EnumSet.of(Change.FRAMEWORK_CHANGE);
    }

    private CakePHP3CustomizerPanel getPanel() {
        if (panel == null) {
            panel = new CakePHP3CustomizerPanel();
            panel.setCakePHP3Enabled(isEnabled);
            panel.setNameSpace(namespace);
            panel.setRoot(root);
            panel.setSrc(src);
            panel.setWWWRoot(wwwRoot);
            panel.setCss(css);
            panel.setImg(img);
            panel.setJs(js);
            panel.setDotcakePath(dotcake);
        }
        return panel;
    }

}
