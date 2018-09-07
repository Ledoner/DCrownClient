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

package net.datacrow.console.windows.resourceeditor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.resources.DcLanguageResource;
import net.datacrow.core.resources.DcResources;

public class CreateLanguageDialog extends DcDialog implements ActionListener{
    
    private DcShortTextField txtName = ComponentFactory.getShortTextField(25);
    private JComboBox cbLanguages = ComponentFactory.getLanguageCombobox();
    
    private String language; 
    
    public CreateLanguageDialog(ResourceEditorDialog frm) {
        super(frm);
        setTitle(DcResources.getText("lblAddLanguage"));
        build();
        pack();
        setCenteredLocation();
        setModal(true);
    }
    
    public String getSelectedLanguage() {
        return language;
    }
    
    @Override
    public String getHelpIndex() {
        return "dc.tools.resourceeditor";
    }
    
    public void save() {
        Collection<String> languages = DcResources.getLanguages();
        
        String name = txtName.getText().trim(); 
        if (name.length() == 0) {
            GUI.getInstance().displayWarningMessage("msgLanguageNameMustBeFilled");
        } else if (languages.contains(name)) {
            GUI.getInstance().displayWarningMessage("msgLanguageWithNameAlreadyExists");
        } else {
            language = name.replaceAll(" ", "");
            DcLanguageResource lr = new DcLanguageResource(name);
            lr.merge(DcResources.getLanguageResource((String) cbLanguages.getSelectedItem()));
            DcResources.addLanguageResource(language, lr);
            close();
        }
    }

    private void build() {
        getContentPane().setLayout(Layout.getGBL());
        
        //**********************************************************
        //Input panel
        //**********************************************************
        JPanel panelInput = new JPanel();
        panelInput.setLayout(Layout.getGBL());
        
        JLabel lblName = ComponentFactory.getLabel(DcResources.getText("lblName"));
        JLabel lblBasedOn = ComponentFactory.getLabel(DcResources.getText("lblBasedOn"));
        
        panelInput.add(lblName, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelInput.add(txtName, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelInput.add(lblBasedOn, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelInput.add(cbLanguages, Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        
        
        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        
        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        
        panelActions.add(buttonSave);
        panelActions.add(buttonClose);

        getContentPane().add(panelInput, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("save"))
            save();
    } 
}
