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

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcAssociateModule;
import net.datacrow.core.modules.DcMediaModule;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcPropertyModule;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcMediaObject;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcProperty;
import net.datacrow.core.resources.DcResources;

public class PanelModuleType extends ModuleWizardPanel {

    private static final int _OTHERMODULE = 0;
    private static final int _MEDIAMODULE = 1;
    private static final int _PROPERTYMODULE = 2;
    private static final int _ASSOCIATEMODULE = 3;
    
    private int type = -1;
    
    public PanelModuleType(Wizard wizard) {
        super(wizard);
        build();
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectModuleType");
    }

    @Override
    public void destroy() {} 
    
    @Override
    public Object apply() {
        
        XmlModule module = getModule();
        
        if (type == -1) {
            GUI.getInstance().displayWarningMessage("msgSelectModuleTypeFirst");
            return null;
        }
        
        if (type == _OTHERMODULE) {
            module.setModuleClass(DcModule.class);
            module.setObject(DcObject.class);
            module.setDefaultSortFieldIdx(DcObject._ID);
            module.setNameFieldIdx(DcObject._ID);
        } else if (type == _MEDIAMODULE) {
            module.setModuleClass(DcMediaModule.class);
            module.setObject(DcMediaObject.class);
            module.setDefaultSortFieldIdx(DcMediaObject._A_TITLE);
            module.setNameFieldIdx(DcMediaObject._A_TITLE);
        } else if (type == _PROPERTYMODULE) {
            module.setModuleClass(DcPropertyModule.class);
            module.setObject(DcProperty.class);
            module.setCanBeLend(false);
            module.setDefaultSortFieldIdx(DcProperty._A_NAME);
            module.setNameFieldIdx(DcProperty._A_NAME);
        } else if (type == _ASSOCIATEMODULE) {
            module.setModuleClass(DcAssociateModule.class);
            module.setObject(DcAssociate.class);
            module.setCanBeLend(false);
            module.setDefaultSortFieldIdx(DcAssociate._A_NAME);
            module.setNameFieldIdx(DcAssociate._A_NAME);
        }

        return module;
    }
    
    private void build() {
        final ButtonGroup buttonGroup = new ButtonGroup();

        class ModuleTypeSelectionListener implements MouseListener {
            @Override
            public void mouseClicked(MouseEvent arg0) {}
            @Override
            public void mouseEntered(MouseEvent arg0) {}
            @Override
            public void mouseExited(MouseEvent arg0) {}
            @Override
            public void mousePressed(MouseEvent arg0) {}

            @Override
            public void mouseReleased(MouseEvent arg0) {
                String command = buttonGroup.getSelection().getActionCommand();
                type = Integer.parseInt(command);
                try {
                    getWizard().next();
                } catch (WizardException wi) {
                    GUI.getInstance().displayWarningMessage(wi.getMessage());
                }            
            }
        }          
        
        setLayout(Layout.getGBL());
        
        JRadioButton rbPlainMod = ComponentFactory.getRadioButton(
                DcResources.getText("lblPlainModule"),  IconLibrary._icoModuleTypePlain, "" + _OTHERMODULE);
        JRadioButton rbMediaMod = ComponentFactory.getRadioButton(
                DcResources.getText("lblMediaModule"),  IconLibrary._icoModuleTypeMedia, "" + _MEDIAMODULE);
        JRadioButton rbPropertyMod = ComponentFactory.getRadioButton(
                DcResources.getText("lblPropertyModule"), IconLibrary._icoModuleTypeProperty32, "" + _PROPERTYMODULE);
        JRadioButton rbAssociateMod = ComponentFactory.getRadioButton(
                DcResources.getText("lblAssociateModule"), IconLibrary._icoModuleTypeAssociate, "" + _ASSOCIATEMODULE);
        
        rbPlainMod.addMouseListener(new ModuleTypeSelectionListener());
        rbMediaMod.addMouseListener(new ModuleTypeSelectionListener());
        rbPropertyMod.addMouseListener(new ModuleTypeSelectionListener());
        rbAssociateMod.addMouseListener(new ModuleTypeSelectionListener());
        
        buttonGroup.add(rbPlainMod);
        buttonGroup.add(rbMediaMod);
        buttonGroup.add(rbPropertyMod);
        buttonGroup.add(rbAssociateMod);

        add(rbPropertyMod,Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));        
        add(rbMediaMod, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));        
        add(rbPlainMod, Layout.getGBC(0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));     
        add(rbAssociateMod, Layout.getGBC(0, 3, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0)); 
    }
}
