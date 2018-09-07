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

package net.datacrow.console.wizards.itemexport;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFileField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.resources.DcResources;

public class ModuleExportDefinitionDialog extends DcDialog implements ActionListener {

    private DcFileField ffTarget;
    private JComboBox cbModules;
    private JComboBox cbExporters;
    
    private boolean canceled = false;
    
    public ModuleExportDefinitionDialog() {
        super();
        
        setTitle("BLABLA");
        
        build();
    }
    
    public boolean isCanceled() {
        return canceled;
    }

    private void build() {
        setLayout(Layout.getGBL());
        
        //**********************************************************
        //Input
        //**********************************************************
        add(ComponentFactory.getLabel(DcResources.getText("lblModule")), 
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 5, 5, 5), 0, 0));
        cbModules = ComponentFactory.getComboBox();
        add(cbModules, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));
        add(ComponentFactory.getLabel(DcResources.getText("lblTargetFile")), 
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 5, 5, 5), 0, 0));
        ffTarget = ComponentFactory.getFileField(false, false);
        add(ffTarget, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));
        add(ComponentFactory.getLabel(DcResources.getText("lblExporter")), 
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 5, 5, 5), 0, 0));
        cbExporters = ComponentFactory.getComboBox();
        add(cbExporters, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));        
        
        //**********************************************************
        //Actions
        //**********************************************************
        JPanel panelActions = new JPanel();
        JButton btOk = ComponentFactory.getButton(DcResources.getText("lblOK"));
        JButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        btOk.addActionListener(this);
        btOk.setActionCommand("ok");
        btCancel.addActionListener(this);
        btCancel.setActionCommand("ok");
        
        panelActions.add(btCancel);
        panelActions.add(btOk);
        
        add(panelActions, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 0, 5, 5, 5), 0, 0));      
        
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("moduleChanged")) {
            
        } else if (ae.getActionCommand().equals("ok")) {
            canceled = false;
            close();
        } else if (ae.getActionCommand().equals("cancel")) {
            canceled = true;
            close();
        }
    }
}
