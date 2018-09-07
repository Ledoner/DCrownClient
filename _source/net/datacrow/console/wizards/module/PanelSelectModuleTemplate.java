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

import net.datacrow.console.GUI;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.xml.XmlModule;
import net.datacrow.core.resources.DcResources;

/**
 * @author Robert Jan van der Waals 
 */
public class PanelSelectModuleTemplate extends PanelSelectModule {

    public PanelSelectModuleTemplate(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object apply() {
        if (getSelectedModule() == -1) {
            GUI.getInstance().displayMessage("msgSelectModuleFirst");
            return null;
        }
        return new XmlModule(DcModules.get(getSelectedModule()).getXmlModule());
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectModuleToCopy");
    }
    
    @Override
    protected boolean isModuleAllowed(DcModule module) {
        return module.getXmlModule() != null && (module.isTopModule() || module.isChildModule()) && !module.isAbstract();
    }
}
