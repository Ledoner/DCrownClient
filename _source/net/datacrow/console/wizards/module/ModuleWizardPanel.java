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

import javax.swing.JPanel;

import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.core.modules.xml.XmlModule;

public abstract class ModuleWizardPanel extends JPanel implements IWizardPanel {

    private XmlModule module;
    private Wizard wizard;

    public ModuleWizardPanel(Wizard wizard) {
        this.wizard = wizard;
    }
    
    public void setModule(XmlModule module) {
        this.module = module;
    }
    
    public XmlModule getModule() {
        return module == null ? new XmlModule() : module;
    }
    
    protected Wizard getWizard() {
        return wizard;
    }
    
    @Override
	public void setVisible(boolean b) {
		super.setVisible(b);
    	if (b) onActivation();
    	else onDeactivation();
    }

	@Override
    public void onDeactivation() {}
	
    @Override
    public void onActivation() {}    

    @Override
    public abstract String getHelpText();
}
