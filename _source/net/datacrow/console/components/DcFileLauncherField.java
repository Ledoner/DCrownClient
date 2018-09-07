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

package net.datacrow.console.components;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.BrowserDialog;
import net.datacrow.console.windows.drivemanager.DriveManagerSingleItemMatcher;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.drivemanager.DriveManager;
import net.datacrow.core.drivemanager.FileInfo;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.util.Utilities;
import net.datacrow.util.launcher.FileLauncher;

import org.apache.log4j.Logger;

public class DcFileLauncherField extends JComponent implements IComponent, ActionListener, MouseListener {

    private static Logger logger = Logger.getLogger(DcFileLauncherField.class.getName());
    
    private ItemForm parent;
    
    private DcShortTextField text;
    private JButton buttonBrowse;
    private File file;

    public DcFileLauncherField() {
        text = ComponentFactory.getTextFieldDisabled();

        setBounds(0,0,0,0);

        buttonBrowse = ComponentFactory.getIconButton(IconLibrary._icoOpen);
        buttonBrowse.addActionListener(this);
        buttonBrowse.setActionCommand("showFileOpenDialog");
        
        JButton buttonLaunch = ComponentFactory.getIconButton(IconLibrary._icoOpenApplication);
        buttonLaunch.addActionListener(this);
        buttonLaunch.setActionCommand("launchFile");
        
        setLayout(Layout.getGBL());
        add( text,         Layout.getGBC( 0, 0, 1, 1, 80.0, 80.0
                ,GridBagConstraints.WEST, GridBagConstraints.BOTH,
                 new Insets(0, 0, 0, 0), 0, 0));
        add( buttonBrowse, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0));
        add( buttonLaunch, Layout.getGBC( 2, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.EAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 0, 0), 0, 0));    
        
        text.addMouseListener(this);
    }
    
    @Override
    public void clear() {
        text = null;
        file = null;
        buttonBrowse = null;
        parent = null;
    }

    public File getFile() {
        return file;
    }

    public String getFileAsString() {
        return file == null ? "" : CoreUtilities.getOriginalFilename(file.toString());
    }

    public void setFile(File file) {
        this.file =  file;
        text.setText(file == null ? "" : CoreUtilities.getValidPath(file == null ? "" : file.toString()));
    }
    
    @Override
    public void setValue(Object value) {
        if (value != null && value.toString().length() > 0) {
            File file = new File(value.toString());
            setFile(file);
        } else {
            setFile(null);
        }
    }

    @Override
    public Object getValue() {
        return getFileAsString();
    }    
    
    @Override
    public void setEditable(boolean b) {
        buttonBrowse.setEnabled(b);
    }

    private void showFileOpenDialog() {
        BrowserDialog dialog = new BrowserDialog(DcResources.getText("lblFileBrowser"), null);
        File fileNew = dialog.showOpenFileDialog(this, null);
        file = fileNew != null ? fileNew : file;
        text.setText(getFileAsString());

        dialog.dispose();
        dialog = null;
    }
    
    private void launch() {
        if (!CoreUtilities.isEmpty(text.getText())) {
            String filename = CoreUtilities.getValidPath(text.getText());
            new FileLauncher(filename).launch();
        }
    }
    
    private void locateFile(final int precision) {
        new Thread(new Runnable() { 
            @Override
            public void run() {
                DriveManagerSingleItemMatcher matcher = 
                    new DriveManagerSingleItemMatcher(parent.getOriginalItem(), precision);
                matcher.start();
                try {
                    matcher.join();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                }
                
                FileInfo info = matcher.getResult();
                if (info != null)
                    setFile(new File(info.getFilename()));
            }
        }).start();        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("launchFile")) {
            launch();
        } else if (e.getActionCommand().equals("showFileOpenDialog")) {
            showFileOpenDialog();
        } else if (e.getActionCommand().equals("clear")) {
            setFile(null);
        } else if (e.getActionCommand().equals("deleteFile")) {
            file.delete(); 
            setFile(null);
        } else if (e.getActionCommand().equals("locateFileHP")) {
            locateFile(DriveManager._PRECISION_HIGHEST);
        } else if (e.getActionCommand().equals("locateFileMP")) {
            locateFile(DriveManager._PRECISION_MEDIUM);
        } else if (e.getActionCommand().equals("locateFileLP")) {
            locateFile(DriveManager._PRECISION_LOWEST);
        } else if (e.getActionCommand().equals("moveFile")) {
            BrowserDialog dialog = new BrowserDialog(DcResources.getText("msgSelectnewLocation"), null);
            File newDir = dialog.showSelectDirectoryDialog(this, null);
        
            if (newDir != null) {
                try {
                    File newFile = new File(newDir, file.getName());
                    Utilities.rename(file, newFile, true);
                    setFile(newFile);
                } catch (IOException e1) {
                    logger.error(e1, e1);
                }
            }
        }
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    }   
    
    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (parent == null) {
            Container c = getTopLevelAncestor();
            parent = c instanceof ItemForm ? (ItemForm) c : null;
        }
        
        if (SwingUtilities.isRightMouseButton(e)) {
            
            if (!buttonBrowse.isEnabled()) return;
            
            DcPopupMenu popup = new DcPopupMenu();
            
            JMenuItem miClear = ComponentFactory.getMenuItem(IconLibrary._icoDelete, DcResources.getText("lblClearField"));
            miClear.addActionListener(this);
            miClear.setActionCommand("clear");
            miClear.setEnabled(file != null);

            JMenuItem miDelete = ComponentFactory.getMenuItem(IconLibrary._icoDelete, DcResources.getText("lblDeleteFile"));
            miDelete.addActionListener(this);
            miDelete.setActionCommand("deleteFile");
            miDelete.setEnabled(file != null && file.exists());

            JMenuItem miMove = ComponentFactory.getMenuItem(DcResources.getText("lblMoveFile"));
            miMove.addActionListener(this);
            miMove.setActionCommand("moveFile");
            miMove.setEnabled(file != null && file.exists());

            JMenuItem miLocateHP = ComponentFactory.getMenuItem(IconLibrary._icoDriveScanner, DcResources.getText("lblLocateFile", DcResources.getText("lblMatchOnHashAndSize")));
            miLocateHP.addActionListener(this);
            miLocateHP.setActionCommand("locateFileHP");
            miLocateHP.setEnabled(file != null && !file.exists() && parent != null);
            
            JMenuItem miLocateMP = ComponentFactory.getMenuItem(IconLibrary._icoDriveScanner, DcResources.getText("lblLocateFile", DcResources.getText("lblMatchOnFilenameAndSize")));
            miLocateMP.addActionListener(this);
            miLocateMP.setActionCommand("locateFileMP");
            miLocateMP.setEnabled(file != null && !file.exists() && parent != null);            

            JMenuItem miLocateLP = ComponentFactory.getMenuItem(IconLibrary._icoDriveScanner, DcResources.getText("lblLocateFile", DcResources.getText("lblMatchOnFilename")));
            miLocateLP.addActionListener(this);
            miLocateLP.setActionCommand("locateFileLP");
            miLocateLP.setEnabled(file != null && !file.exists() && parent != null);            
            
            popup.add(miClear);
            popup.addSeparator();
            popup.add(miDelete);
            
            Connector connector = DcConfig.getInstance().getConnector();
            if (connector.getUser().isAdmin()) {
                popup.add(miMove);
                popup.add(miLocateLP);
                popup.add(miLocateMP);
                popup.add(miLocateHP);
            }
            
            popup.show(this, e.getX(), e.getY());
        } 

        if (e.getClickCount() == 2)
            launch();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}  
    
    @Override
    public void refresh() {}
}
