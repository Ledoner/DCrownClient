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

package net.datacrow.console.components.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import net.datacrow.console.components.panels.DcModuleList;
import net.datacrow.console.windows.security.ModulePermissionPanel;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;

import org.apache.log4j.Logger;

public class SecurityModuleList extends DcModuleList {
	
	private static Logger logger = Logger.getLogger(SecurityModuleList.class.getName());
	
	private ModulePermissionPanel modulePermissionPanel;
	
	public SecurityModuleList(ModulePermissionPanel modulePermissionPanel) {
		this.modulePermissionPanel = modulePermissionPanel;
	}
	
	@Override
	public void clear() {
	    super.clear();
		modulePermissionPanel = null;
	}
	
	private void setSelected() {
        listenForChanges = false;
        Object item = getSelectedValue();
        if (item instanceof ModulePanel) {
            int module = ((ModulePanel) item).getModule();
            if (module != currentIndex) {
                currentIndex = module;
                modulePermissionPanel.setSelected(currentIndex);
                setSelectedModule(currentIndex);
            }
        }
        listenForChanges = true;		
	}
	
	@Override
    public void valueChanged(ListSelectionEvent lse) {
        if (listenForChanges) setSelected();
    }
    
	@Override
    public void setSelectedModule(int module) {
        if ( ((DcModules.get(module).isTopModule() || DcModules.get(module).isChildModule())  && 
              !DcModules.get(module).hasDependingModules()))
            setModules(module);
    }

	
    @Override
	public void addModules() {
        if (elements != null) elements.clear();
        
        DcModule referencedMod;
        DcModule referencedMod2;
        Collection<DcModule> managedModules = DcModules.getSecuredModules();
        for (DcModule module : managedModules) {
            try {
            	if (module.isSelectableInUI() || module.isChildModule()) {

            	    List<ModulePanel> c = new ArrayList<ModulePanel>();
	                c.add(new ModulePanel(module, ModulePanel._ICON32));
	                
	                for (DcField field : module.getFields()) {
	                    referencedMod = DcModules.getReferencedModule(field);
                    	if (    managedModules.contains(referencedMod) &&
                    			referencedMod.isEnabled() &&
                        		referencedMod.getIndex() != module.getIndex() && 
                                referencedMod.getIndex() != DcModules._CONTACTPERSON &&
                                referencedMod.getIndex() != DcModules._CONTAINER) {
	                    	
	                        c.add(new ModulePanel(referencedMod, ModulePanel._ICON16));
	                        
	                        // elegant? no.. 
	                        for (DcField field2 : referencedMod.getFields()) {
	                            referencedMod2 = DcModules.getReferencedModule(field2);
	                            if (    managedModules.contains(referencedMod2) &&
	                                    referencedMod2.isEnabled() &&
	                                    referencedMod2.getIndex() != referencedMod.getIndex() && 
	                                    referencedMod2.getIndex() != DcModules._CONTACTPERSON &&
	                                    referencedMod2.getIndex() != DcModules._CONTAINER) {
	                                
	                                c.add(new ModulePanel(referencedMod2, ModulePanel._ICON16));
	                            }
	                        }
                    	}
	                }
	                
	                elements.put(module.getIndex(), c);
            	}
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        
        for (DcModule module : DcModules.getSecuredModules()) {
            setModules(module.getIndex());
        	break;
        }
	}
}
