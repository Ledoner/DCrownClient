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

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.Settings;

public class LocalArtSettingsPanel extends JPanel {

    private final int module;
    
    private DcCheckBox cbRecurse = ComponentFactory.getCheckBox(DcResources.getText("lblRecursiveDir"));
    private DcCheckBox cbEnabled = ComponentFactory.getCheckBox(DcResources.getText("lblEnabled"));
    
    private DcShortTextField txtFront = ComponentFactory.getShortTextField(255);
    private DcShortTextField txtBack = ComponentFactory.getShortTextField(255);
    private DcShortTextField txtMedia = ComponentFactory.getShortTextField(255);
    
    public LocalArtSettingsPanel(int module) {
        this.module = module;
        build();
    }

    public void save() {
        Settings settings = DcModules.get(module).getSettings();
        settings.set(DcRepository.ModuleSettings.stImportLocalArt, cbEnabled.isSelected());
        settings.set(DcRepository.ModuleSettings.stImportLocalArtRecurse, cbRecurse.isSelected());
        settings.set(DcRepository.ModuleSettings.stImportLocalArtFrontKeywords, txtFront.getText());
        settings.set(DcRepository.ModuleSettings.stImportLocalArtBackKeywords, txtBack.getText());
        settings.set(DcRepository.ModuleSettings.stImportLocalArtMediaKeywords, txtMedia.getText());        
    }
    
    public void clear() {
        if (txtMedia.getText().length() == 0 ||
            txtFront.getText().length() == 0 ||
            txtBack.getText().length() == 0) {
            
            GUI.getInstance().displayWarningMessage("msgPleaseEnterKeywords");
            return;
        } else {
            save();
        }
        
        cbRecurse = null;
        cbEnabled = null;
        txtFront = null;
        txtBack = null;
        txtMedia = null;
    }
    
    protected void build() {
        
        //**********************************************************
        //Help
        //**********************************************************
        DcLongTextField explanation = ComponentFactory.getLongTextField();
        explanation.setText(DcResources.getText("msgImportSettingsExplanation"));
        ComponentFactory.setUneditable(explanation);
        
        //**********************************************************
        //Input panel
        //**********************************************************
        JPanel pnlPatterns = new JPanel();
        pnlPatterns.setLayout(Layout.getGBL());
        pnlPatterns.setBorder(ComponentFactory.getTitleBorder(""));

        JLabel lblFront = ComponentFactory.getLabel(DcResources.getText("lblPictureFront"));
        JLabel lblBack = ComponentFactory.getLabel(DcResources.getText("lblPictureBack"));
        JLabel lblMedia = ComponentFactory.getLabel(DcResources.getText("lblPictureMedia"));

        pnlPatterns.add(  lblFront,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
        pnlPatterns.add(  txtFront,  Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        pnlPatterns.add(  lblBack,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
        pnlPatterns.add(  txtBack,  Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        pnlPatterns.add(  lblMedia,  Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));
        pnlPatterns.add(  txtMedia,  Layout.getGBC( 1, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        pnlPatterns.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblArtKeywords")));
        

        //**********************************************************
        //Main Panel
        //**********************************************************
        setLayout(Layout.getGBL());
        
        add(    explanation,   Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(15, 5, 5, 5), 0, 0));
        add(    cbEnabled,     Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));
        add(    cbRecurse,     Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));
        add(    pnlPatterns,  Layout.getGBC( 0, 4, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));
        
        Settings settings = DcModules.get(module).getSettings();
        txtBack.setText(settings.getString(DcRepository.ModuleSettings.stImportLocalArtBackKeywords));
        txtFront.setText(settings.getString(DcRepository.ModuleSettings.stImportLocalArtFrontKeywords));
        txtMedia.setText(settings.getString(DcRepository.ModuleSettings.stImportLocalArtMediaKeywords));
        cbEnabled.setSelected(settings.getBoolean(DcRepository.ModuleSettings.stImportLocalArt));
        cbRecurse.setSelected(settings.getBoolean(DcRepository.ModuleSettings.stImportLocalArtRecurse));
    }
}