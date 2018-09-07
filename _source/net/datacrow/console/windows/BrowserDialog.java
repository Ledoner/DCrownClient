/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.console.windows;

import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import net.datacrow.console.GUI;
import net.datacrow.console.components.DcFileChooser;
import net.datacrow.console.components.panels.BackupFilePreviewPanel;
import net.datacrow.core.DcRepository;
import net.datacrow.settings.DcSettings;

public class BrowserDialog extends JFrame {

    private final DcFileChooser browser = new DcFileChooser();

    public BrowserDialog(String title) {
        browser.setMultiSelectionEnabled(false);
        browser.setDialogTitle(title);
        this.setTitle(title);
    }
    
    public BrowserDialog(String title, FileFilter filter) {
        this(title);
        browser.setDialogTitle(title);
        
        if (filter != null) 
            browser.setFileFilter(filter);
    }
    
    public void setPreview(JPanel panel) {
        BackupFilePreviewPanel preview = new BackupFilePreviewPanel();
        browser.setAccessory(preview);
        browser.addPropertyChangeListener(preview);
    }

    @Override
    public void setFont(Font font) {
        if (browser != null)
            browser.setFont(font);
    }

    public File showSelectDirectoryDialog(Component c, File file) {
        setCurrentDirectory(file);
        browser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (c != null) { 
        	browser.showOpenDialog(c);
        } else {
        	browser.showOpenDialog(GUI.getInstance().getRootFrame());
        }        
        
        File f = browser.getSelectedFile(); 
        rememberUsedDirectory(f);
        return f;
    }
    
    public File showCreateFileDialog(Component c, File file) {
        setCurrentDirectory(file);
        browser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (c != null) { 
        	browser.showSaveDialog(c);
        } else {
        	browser.showSaveDialog(GUI.getInstance().getRootFrame());
        }

        File f = browser.getSelectedFile(); 
        rememberUsedDirectory(f);
        return f;
    }
    
    public File showOpenFileDialog(Component c, File file) {
        setCurrentDirectory(file);
        browser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        if (c != null) { 
        	browser.showOpenDialog(c);
        } else {
        	browser.showOpenDialog(GUI.getInstance().getRootFrame());
        }

        File f = browser.getSelectedFile(); 
        rememberUsedDirectory(f);
        return f;
    }
    
    private void setCurrentDirectory(File file) {
        if (file != null) {
            browser.setCurrentDirectory(file);
        } else {
            String s = DcSettings.getString(DcRepository.Settings.stLastDirectoryUsed);
            browser.setCurrentDirectory(new File(s));
        }
    }
    
    private void rememberUsedDirectory(File file) {
        if (file != null && file.getParentFile() != null) {
            DcSettings.set(DcRepository.Settings.stLastDirectoryUsed, file.getParentFile().toString());
        }        
    }
}
