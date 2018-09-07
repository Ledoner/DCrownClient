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

package net.datacrow.console.windows.settings;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcColorSelector;
import net.datacrow.console.components.panels.FieldSelectionPanel;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class CardViewSettingsDialog extends DcDialog implements ActionListener {
    
    private FieldSelectionPanel fsp;
    private CardViewPictureSettingsPanel cisp;
    
    public CardViewSettingsDialog() {
        super();
        
        setTitle(DcResources.getText("lblViewSettings"));
        setHelpIndex("dc.settings.cardview");

        DcModule module = DcModules.getCurrent();
    
        build();
        pack();
        
        setSize(module.getSettings().getDimension(DcRepository.ModuleSettings.stCardViewSettingsDialogSize));
        setCenteredLocation();
    }

    private void save() {
        DcModule module = DcModules.getCurrent();
        
        List<DcField> f = fsp.getSelectedFields();
        int[] fields = new int[f.size()];
        int idx = 0;
        for (DcField field : f) {
            fields[idx++] = field.getIndex();
        }
        
        module.setSetting(DcRepository.ModuleSettings.stCardViewItemDescription, fields);
        cisp.save();
        
        if (module.hasSearchView())
            GUI.getInstance().getSearchView(module.getIndex()).applySettings();

        if (module.hasInsertView())
            GUI.getInstance().getInsertView(module.getIndex()).applySettings();
        
        close();
    }
    
    @Override
    public void close() {
        DcModules.getCurrent().setSetting(DcRepository.ModuleSettings.stCardViewSettingsDialogSize, getSize());
        
        if (fsp != null) fsp.clear();
        if (cisp != null) cisp.clear();
        
        fsp = null;
        cisp = null;
        
        super.close();
    }

    private void build() {
        setLayout(Layout.getGBL());
        
        //**********************************************************
        //Tabbed pane
        //**********************************************************
        JTabbedPane tp = ComponentFactory.getTabbedPane();
        
        DcModule module = DcModules.getCurrent();
        fsp = new FieldSelectionPanel(module, false, true, true);
        
        int[] fields = (int[]) module.getSetting(DcRepository.ModuleSettings.stCardViewItemDescription);
        fsp.setSelectedFields(fields);
        
        DcColorSelector csTextBg = ComponentFactory.getColorSelector(DcRepository.Settings.stCardViewBackgroundColor);
        DcColorSelector csOdd = ComponentFactory.getColorSelector(DcRepository.Settings.stOddRowColor);
        DcColorSelector csEven = ComponentFactory.getColorSelector(DcRepository.Settings.stEvenRowColor);
        
        csOdd.setValue(DcSettings.getColor(DcRepository.Settings.stOddRowColor));
        csEven.setValue(DcSettings.getColor(DcRepository.Settings.stEvenRowColor));
        csTextBg.setValue(DcSettings.getColor(DcRepository.Settings.stCardViewBackgroundColor));
        
        cisp = new CardViewPictureSettingsPanel(DcModules.getCurrent().getIndex());
        
        tp.addTab(DcResources.getText("lblDescription"), fsp);
        tp.addTab(DcResources.getText("lblPictures"), cisp);
        tp.addTab(DcResources.getText("lblEvenColor"), csEven);
        tp.addTab(DcResources.getText("lblOddColor"), csOdd);
        tp.addTab(DcResources.getText("lblBackgroundColor"), csTextBg);
        
        //**********************************************************
        //Action Panel
        //**********************************************************
        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        JPanel panelActions = new JPanel();
        panelActions.add(buttonSave);
        panelActions.add(buttonClose);
        
        buttonSave.addActionListener(this);
        buttonClose.addActionListener(this);
        buttonSave.setActionCommand("save");
        buttonClose.setActionCommand("close");

        //**********************************************************
        //Main Panel
        //**********************************************************        
        getContentPane().add(tp, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0
                 ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                  new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 5), 0, 0));        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("save")) {
            save();
        } else if (e.getActionCommand().equals("close")) {
            close();
        }
    }
}
