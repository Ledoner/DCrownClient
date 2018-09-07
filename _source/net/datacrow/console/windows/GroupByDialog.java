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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.panels.FieldSelectionPanel;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class GroupByDialog extends DcDialog implements ActionListener {

    private FieldSelectionPanel panelSorting;
    private int module;
    
    public GroupByDialog(int module) {
        super(GUI.getInstance().getMainFrame());
        
        setTitle(DcResources.getText("lblGrouping"));
        
        this.module = module;
        
        Collection<DcField> fields = new ArrayList<DcField>();
        for (DcField field : DcModules.get(module).getFields()) {
            
            if ( field.isSystemField() ||
                !field.isEnabled() ||
                (field.isUiOnly() &&
                 field.getIndex() != DcObject._SYS_MODULE &&
                 field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION &&
                 field.getFieldType() != ComponentFactory._REFERENCEFIELD)) 
                continue;
            
            fields.add(field);
        }
        
        this.panelSorting = new FieldSelectionPanel(DcModules.get(module), fields);
        
        int[] groupBy = DcModules.get(module).getSettings().getIntArray(DcRepository.ModuleSettings.stGroupedBy);
        if (groupBy != null)
            this.panelSorting.setSelectedFields(groupBy);
        
        build();
        
        setSize(DcSettings.getDimension(DcRepository.Settings.stGroupByDialogSize));
        setCenteredLocation();
    }
    
    @Override
    public void close() {
        panelSorting.clear();
        panelSorting = null;
        DcSettings.set(DcRepository.Settings.stGroupByDialogSize, getSize());
        super.close();
    }
    
    private void groupBy() {
        List<DcField> fields = panelSorting.getSelectedFields();
        int[] groupBy = new int[fields.size()];

        int counter = 0;
        for (DcField field : fields)
            groupBy[counter++] = field.getIndex();
        
        close();
        
        DcModules.get(module).setSetting(DcRepository.ModuleSettings.stGroupedBy, groupBy);
        
        GUI.getInstance().getSearchView(module).getCurrent().applyGrouping();
    }
    
    private void build() {
        
        getContentPane().setLayout(Layout.getGBL());
        
        //**********************************************************
        //Action Panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        JButton buttonApply = ComponentFactory.getButton(DcResources.getText("lblApply"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        buttonApply.addActionListener(this);
        buttonApply.setActionCommand("groupBy");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        
        panelActions.add(buttonApply);
        panelActions.add(buttonClose);
        
        //**********************************************************
        //Main Panel
        //**********************************************************
        getContentPane().add(panelSorting,  Layout.getGBC( 0, 0, 1, 1, 40.0, 40.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        
        pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("groupBy"))
            groupBy();
    }
}