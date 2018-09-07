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

import net.datacrow.console.wizards.Wizard;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.resources.DcResources;

public class PanelSelectParentModule extends PanelSelectModule {

    public PanelSelectParentModule(Wizard wizard) {
        super(wizard);
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgSelectParentModule");
    }
    
    @Override
    public void destroy() {} 
    
    
    
    @Override
    protected boolean isModuleAllowed(DcModule module) {
        return module.isTopModule() && 
              !module.isAbstract() &&
              !module.isParentModule() && !module.isChildModule();
    }
}