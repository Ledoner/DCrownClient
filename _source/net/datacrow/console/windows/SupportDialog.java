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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFileField;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Directory;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;

public class SupportDialog extends DcDialog implements ActionListener {
    
    private static Logger logger = Logger.getLogger(SupportDialog.class.getName());

    private DcFileField fldTarget = ComponentFactory.getFileField(false, true);
    
    public SupportDialog() {
        super();
        
        setTitle(DcResources.getText("lblCreateSupportPackage"));

        build();
        
        pack();
        setSize(new Dimension(400, 200));
        setResizable(false);
        setCenteredLocation();
    }
    
    private void createPackage() {
        
        File file = fldTarget.getFile();
        
        if (file == null) {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgSelectTargetFolderFirst"));
        }
        
        try {
            File zipFileName = new File(file, "dc_support.zip");
            zipFileName.delete();
            
            DcConfig dcc = DcConfig.getInstance();
            
            File f = new File(dcc.getDataDir(), "data_crow.log");
            if (f.exists())
                addEntry(new File(zipFileName, f.getName()), f);
            
            f = new File(dcc.getDataDir(), "data_crow.log.1");
            if (f.exists())
                addEntry(new File(zipFileName, f.getName()), f);
            
            Directory dir = new Directory(DcConfig.getInstance().getApplicationSettingsDir(), true, null);
            for (String s : dir.read()) {
                f = new File(s);
                addEntry(new File(zipFileName, f.getName()), f);
            }
            
            dir = new Directory(DcConfig.getInstance().getModuleSettingsDir(), true, null);
            for (String s : dir.read()) {
                f = new File(s);
                addEntry(new File(zipFileName, f.getName()), f);
            }
            
            dir = new Directory(DcConfig.getInstance().getDatabaseDir(), true, null);
            for (String s : dir.read()) {
                f = new File(s);
                addEntry(new File(zipFileName, f.getName()), f);
            }
            
            TVFS.umount();
            
            GUI.getInstance().displayMessage(DcResources.getText("msgSupportFileCreated", zipFileName.toString()));
            
        } catch (Exception e) {
            GUI.getInstance().displayErrorMessage(DcResources.getText("msgErrorCreatingSupportFile", e.getMessage()));
            logger.error(e, e);
        }
    }
    
    private void addEntry(File zipName, File source) {
        try {
            TFile src = new TFile(source);
            TFile dst = new TFile(zipName);
            src.cp_rp(dst);
        } catch (IOException e) {
            logger.error("An error occured while adding " + source + " to the support zip file", e);
        }
    }
    
    private void build() {

        getContentPane().setLayout(Layout.getGBL());
        
        getContentPane().add(ComponentFactory.getLabel(DcResources.getText("lblTargetDirectory")), 
                Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        getContentPane().add(fldTarget, Layout.getGBC(1, 0, 1, 1, 10.0, 10.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        JPanel panelAction = new JPanel();
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        
        JButton buttonCreate = ComponentFactory.getButton(DcResources.getText("lblStart"));
        buttonCreate.addActionListener(this);
        buttonCreate.setActionCommand("create");
        
        panelAction.add(buttonCreate);
        panelAction.add(buttonClose);
        
        getContentPane().add(panelAction, Layout.getGBC(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stNewItemsDialogSize, getSize());
        setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close")) {
            close();
        } else if (ae.getActionCommand().equals("create")) {
            createPackage();
        }
    }
}
