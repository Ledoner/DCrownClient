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

package net.datacrow.console.wizards.tool;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.DcRepository;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class ToolSelectWizard extends Wizard {

    private JCheckBox cb = ComponentFactory.getCheckBox(DcResources.getText("lblRunOnStartup"));
    
    public ToolSelectWizard() {
        super();
        
        add(cb,  Layout.getGBC(1, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(5, 5, 5, 10), 0, 0));
        
        cb.setSelected(DcSettings.getBoolean(DcRepository.Settings.stShowToolSelectorOnStartup));
        
        setHelpIndex("dc.tools.wizard");
        setSize(DcSettings.getDimension(DcRepository.Settings.stToolSelectWizard));
        setCenteredLocation();
    }
    
    @Override
    protected List<IWizardPanel> getWizardPanels() {
        List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
        panels.add(new ModuleSelectPanel(this));
        panels.add(new ToolSelectPanel(this));
        return panels;
    }

    @Override
    protected void saveSettings() {
        DcSettings.set(DcRepository.Settings.stToolSelectWizard, getSize());
        DcSettings.set(DcRepository.Settings.stShowToolSelectorOnStartup, cb.isSelected());
    }

    @Override
    public void finish() throws WizardException {
        close();
    }

    @Override
    public void next() throws WizardException {

        ToolSelectBasePanel panel = (ToolSelectBasePanel) getCurrent();
        Tool tool = panel.getTool();
        tool = tool == null ? new Tool() : tool;
        
        super.next();

        panel = (ToolSelectBasePanel) getCurrent();
        panel.setTool(tool);
        panel.onActivation();
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("lblToolSelectWizard");
    }

    @Override
    protected void initialize() {}

    @Override
    protected boolean isRestartSupported() {
        return false;
    }
}
