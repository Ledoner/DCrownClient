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

package net.datacrow.console.wizards.moduleexport;

import java.util.Collection;

import net.datacrow.console.GUI;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.module.PanelSelectModule;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;

/**
 * @author Robert Jan van der Waals 
 */
public class PanelSelectModuleToExport extends PanelSelectModule {

    public PanelSelectModuleToExport(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object apply() {
        if (getSelectedModule() == -1) {
            GUI.getInstance().displayMessage("msgSelectModuleFirst");
            return null;
        }
        
        DcModule module = DcModules.get(getSelectedModule());
        return module == null ? DcModules.getPropertyBaseModule(getSelectedModule()) : module;
    }

    @Override
    protected Collection<DcModule> getModules() {
        return DcModules.getCustomModules();
    }
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectModuleToExport");
    }
    
    @Override
    protected boolean isModuleAllowed(DcModule module) {
        return module.isCustomModule() && module.getXmlModule() != null;
    }
}
