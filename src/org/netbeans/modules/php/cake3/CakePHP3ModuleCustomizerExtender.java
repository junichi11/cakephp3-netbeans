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
package org.netbeans.modules.php.cake3;

import java.io.File;
import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
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
        CakePHP3CustomizerValidator validator = new CakePHP3CustomizerValidator();
        FileObject srcDir = FileUtil.toFileObject(sourceDirectory);
        String rootPath = getPanel().getRoot();
        String srcPath = String.format("%s/%s", rootPath, getPanel().getSrc()).replaceAll("/+", "/");
        String wwwRootPath = String.format("%s/%s", rootPath, getPanel().getWWWRoot()).replaceAll("/+", "/");
        String cssPath = String.format("%s/%s", wwwRootPath, getPanel().getCss()).replaceAll("/+", "/");
        String imgPath = String.format("%s/%s", wwwRootPath, getPanel().getImg()).replaceAll("/+", "/");
        String jsPath = String.format("%s/%s", wwwRootPath, getPanel().getJs()).replaceAll("/+", "/");
        ValidationResult result = validator.validateRootPath(srcDir, rootPath)
                .validateSrc(srcDir, srcPath)
                .validateWWWRoot(srcDir, wwwRootPath)
                .validateCss(srcDir, cssPath)
                .validateImg(srcDir, imgPath)
                .validateJs(srcDir, jsPath)
                .getResult();
        if (result.hasErrors()) {
            errorMessage = result.getErrors().get(0).getMessage();
            return;
        }
        if (result.hasWarnings()) {
            errorMessage = result.getWarnings().get(0).getMessage();
            return;
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
        }
        return panel;
    }

}
