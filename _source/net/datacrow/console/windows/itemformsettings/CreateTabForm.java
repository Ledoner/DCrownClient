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

package net.datacrow.console.windows.itemformsettings;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcPictureField;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.windows.DcFrame;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;
import net.datacrow.tabs.Tab;
import net.datacrow.tabs.Tabs;

public class CreateTabForm extends DcFrame implements ActionListener {
    
    private MaintainTabsDialog dlg;
    private int module;
    
    private DcShortTextField txtName = ComponentFactory.getShortTextField(255);
    private DcPictureField fldIcon = ComponentFactory.getPictureField(false, false);
    
    public CreateTabForm(MaintainTabsDialog dlg, int module) {
        super(DcResources.getText("lblCreateTab"), IconLibrary._icoAdd);
        
        this.dlg = dlg;
        this.module = module;
        
        build();
    }
    
    private void save() {
        Tab tab = new Tab(module, txtName.getText(), (DcImageIcon) fldIcon.getValue());
        Tabs.getInstance().addTab(tab);
        
        dlg.refresh();
        
        setVisible(false);
        close();
    }
    
    @Override
    public void close() {
        super.close();
        
        txtName = null;
        fldIcon = null;
        dlg = null;
    }
    
    private void build() {
        // Actions panel
        JPanel panelActions = new JPanel();
        
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        
        buttonClose.setActionCommand("close");
        buttonClose.addActionListener(this);
        
        buttonSave.setActionCommand("save");
        buttonSave.addActionListener(this);
        
        panelActions.add(buttonSave);
        panelActions.add(buttonClose);
        
        // Fields panel
        JPanel panelFields = new JPanel();
        panelFields.setLayout(Layout.getGBL());
        
        fldIcon.setValue(IconLibrary._icoInformation);
        
        panelFields.add(ComponentFactory.getLabel(DcResources.getText("lblName")), 
                             Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                             new Insets(5, 5, 5, 5), 0, 0));
        panelFields.add(txtName, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                             new Insets(5, 5, 5, 5), 0, 0));
        
        panelFields.add(ComponentFactory.getLabel(DcResources.getText("lblIcon")), 
                Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panelFields.add(fldIcon, Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
               ,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        
        // Main panel
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(panelFields, Layout.getGBC( 0, 0, 1, 1, 50.0, 50.0
                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                             new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                             new Insets(0, 0, 0, 10), 0, 0));
        
        pack();
        setSize(DcSettings.getDimension(DcRepository.Settings.stExpertFormSize));
        setCenteredLocation();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("close"))
            close();
        else if (e.getActionCommand().equals("save"))
            save();
    }
}
