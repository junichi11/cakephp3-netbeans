/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.cake3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.cake3.modules.CakePHP3Module;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
public final class ConfigurationFiles extends FileChangeAdapter implements ImportantFilesImplementation {

    private final PhpModule phpModule;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    // @GuardedBy("this")
    private boolean isInitialized = false;

    public ConfigurationFiles(PhpModule phpModule) {
        assert phpModule != null;
        this.phpModule = phpModule;
    }

    @Override
    public Collection<FileInfo> getFiles() {
        List<FileObject> directories = getConfigDirectories();
        List<FileInfo> files = new ArrayList<>();
        for (FileObject directory : directories) {
            FileObject[] children = directory.getChildren();
            for (FileObject child : children) {
                if (child.isFolder()) {
                    continue;
                }
                files.add(new FileInfo(child));
            }
        }
        return files;
    }

    private synchronized List<FileObject> getConfigDirectories() {
        CakePHP3Module cakeModule = CakePHP3Module.forPhpModule(phpModule);
        List<FileObject> directories = cakeModule.getDirectories(CakePHP3Module.Base.APP, CakePHP3Module.Category.CONFIG, null);
        if (!isInitialized) {
            isInitialized = true;
            for (FileObject directory : directories) {
                addListener(FileUtil.toFile(directory));
            }
        }
        return directories;
    }

    private void addListener(File path) {
        try {
            FileUtil.addRecursiveListener(this, path);
        } catch (IllegalArgumentException ex) {
            // noop, already listening
            assert false : path;
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    //~ FS
    @Override
    public void fileRenamed(FileRenameEvent fe) {
        fireChange();
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        fireChange();
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        fireChange();
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        fireChange();
    }

}
