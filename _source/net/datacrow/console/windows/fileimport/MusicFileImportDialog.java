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

package net.datacrow.console.windows.fileimport;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcRadioButton;
import net.datacrow.core.DcRepository;
import net.datacrow.core.fileimporter.FileImporter;
import net.datacrow.core.resources.DcResources;

public class MusicFileImportDialog extends FileImportDialog {

    private DcRadioButton radioDoNotUseDir;
    private DcRadioButton radio1stDirAlbum;
    private DcRadioButton radio1stDirArtist;
    private DcRadioButton radio1stDirAlbum2ndDirArtist;
    
    public MusicFileImportDialog(FileImporter importer) {
        super(importer);
    }
    
    @Override
    public int getDirectoryUsage() {
        return radioDoNotUseDir.isSelected() ? 0 : radio1stDirAlbum.isSelected() ? 1 :
               radio1stDirArtist.isSelected() ? 2 : 3;        
    }
    
    @Override
    public void close() {
        super.close();
        radioDoNotUseDir = null;
        radio1stDirAlbum = null;
        radio1stDirArtist = null;
        radio1stDirAlbum2ndDirArtist = null;
    }
    
    @Override
    protected JPanel getDirectoryUsagePanel() {
        //**********************************************************
        //Directory Usage panel
        //**********************************************************
        JPanel panelDirs = new JPanel();
        panelDirs.setLayout(Layout.getGBL());
        panelDirs.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblDirectoryUsage")));
        
        radioDoNotUseDir = ComponentFactory.getRadioButton(DcResources.getText("lblDoNotUseDirInfo"), null);
        radio1stDirAlbum = ComponentFactory.getRadioButton(DcResources.getText("lblUseDirAsAlbumName"), null);
        radio1stDirArtist = ComponentFactory.getRadioButton(DcResources.getText("lblUseDirAsArtistName"), null);
        radio1stDirAlbum2ndDirArtist = ComponentFactory.getRadioButton(DcResources.getText("lblUseDirAsArtistSubDirAsAlbum"), null);

        ButtonGroup group = new ButtonGroup();
        group.add(radioDoNotUseDir);
        group.add(radio1stDirAlbum);
        group.add(radio1stDirArtist);
        group.add(radio1stDirAlbum2ndDirArtist);
        
        panelDirs.add(radioDoNotUseDir, Layout.getGBC( 0, 3, 3, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 5, 0, 5), 0, 0));
        panelDirs.add(radio1stDirAlbum, Layout.getGBC( 0, 4, 3, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 5, 0, 5), 0, 0));
        panelDirs.add(radio1stDirArtist, Layout.getGBC( 0, 5, 3, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 5, 0, 5), 0, 0));
        panelDirs.add(radio1stDirAlbum2ndDirArtist, Layout.getGBC( 0, 6, 3, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 5, 0, 5), 0, 0));

        int dirUsage = settings.getInt(DcRepository.ModuleSettings.stFileImportDirectoryUsage);
        radioDoNotUseDir.setSelected(dirUsage <= 0);
        radio1stDirAlbum.setSelected(dirUsage == 1);
        radio1stDirArtist.setSelected(dirUsage == 2);
        radio1stDirAlbum2ndDirArtist.setSelected(dirUsage == 3);
        return panelDirs;
    }    
}
