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

package net.datacrow.console.wizards.module;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;

public class PanelSelectModule extends ModuleWizardPanel {

    private int selectedModule = -1;
    
    private Map<Integer, JRadioButton> components = new HashMap<Integer, JRadioButton>();
    
    public PanelSelectModule(Wizard wizard) {
        super(wizard);
        build();
    }

    @Override
    public Object apply() {
        if (selectedModule == -1) {
            GUI.getInstance().displayMessage("msgSelectModuleFirst");
            return null;
        }
        
        return DcModules.get(selectedModule).getXmlModule();
    }

    public int getSelectedModule() {
        return selectedModule;
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectModuleToAlter");
    }
    
    @Override
    public void destroy() {
        if (components != null) {
            components.clear();
            components = null;
        }        
    }  
    
    protected JRadioButton getRadioButton(int module) {
        return components.get(module);
    }
    
    protected boolean isModuleAllowed(DcModule module) {
        return  module.getType() == DcModule._TYPE_PROPERTY_MODULE  && 
                module.getXmlModule() != null || 
                (  (module.isTopModule() || module.isChildModule()) && 
                    module.getType() != DcModule._TYPE_PROPERTY_MODULE && 
                    module.getType() != DcModule._TYPE_MAPPING_MODULE &&
                   !module.isAbstract());
    }
    
    protected Collection<DcModule> getModules() {
        return DcModules.getAllModules();
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        
        final ButtonGroup bg = new ButtonGroup();
        class ModuleSelectionListener implements MouseListener {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {
                String command = bg.getSelection().getActionCommand();
                selectedModule = Integer.parseInt(command);
                try {
                    
                    if (getWizard().isAtTheEnd())
                        getWizard().finish();
                    else
                        getWizard().next();
                    
                } catch (WizardException wi) {
                    GUI.getInstance().displayWarningMessage(wi.getMessage());
                }
            }
        } 

        int y = 0;
        int x = 0;
        
        JRadioButton rb;
        for (DcModule module : getModules()) {
            if (isModuleAllowed(module)) {
                rb = ComponentFactory.getRadioButton(module.getLabel(), module.getIcon16(), "" + module.getIndex());
                rb.addMouseListener(new ModuleSelectionListener());
                bg.add(rb);
                
                components.put(module.getIndex(), rb);
                
                add(rb, Layout.getGBC( x, y++, 1, 1, 1.0, 1.0
                   ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                    new Insets( 0, 5, 5, 5), 0, 0));
                
                if (y == 7) {
                    ++x;
                    y = 0;
                }
            }
        }
    }
}
