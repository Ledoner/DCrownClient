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

package net.datacrow.console.clients;

import java.util.Collection;

import net.datacrow.console.GUI;
import net.datacrow.core.DcConfig;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.data.DcIconCache;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;

public class UIClient implements IClient {

    public static final int _UPDATE = 0;
    public static final int _INSERT = 1;
    public static final int _DELETE = 2;
    
    private boolean updateRelatedModules = false;
    private boolean updateCurrentView = false;
    
    private DcObject dco;
    private int type;
    
    public UIClient(
            int type,
            DcObject dco, 
            boolean dispatchToAllModules,
            boolean updateCurrentView) {
        
        this.type = type;
        this.updateRelatedModules = dispatchToAllModules;
        this.updateCurrentView = updateCurrentView;
        
        if (dco != null) {
        	// Make sure the IDs are 
            dco.setIDs();
            // Use a clone of the original
            DcObject clone = dco.clone();
            this.dco = clone;
        }
    }

    @Override
    public void notify(String msg) {}
    
    @Override
    public void notifyWarning(String msg) {}

    @Override
    public void notifyError(Throwable e) {}

    @Override
    public void notifyTaskCompleted(
    		boolean success, 
    		String taskID) {
        
        if (!success) return;
        if (!DcConfig.getInstance().isDataCrowStarted()) return;
        
        switch (type) {
        case _UPDATE:
            update();
            break;
        case _INSERT:
            add();
            break;
        case _DELETE:
            remove();
            break;
        }
    }
    
    private void add() {
        
        GUI gui = GUI.getInstance();
        
        // Note that in the item form (close(boolean b)) the potential parent module's
        // quick view is already updated. No need to do that here.
        String ID = dco.getID();

        if (dco.getModule().isTopModule()) {
            if (dco.getModule().hasSearchView()) {
                
                gui.getSearchView(dco.getModule().getIndex()).add(dco);
                if (dco.getModule().hasInsertView()) {
                    gui.getInsertView(dco.getModule().getIndex()).remove(ID);
                }
            }
        
            IMasterView mv;
            for (DcModule module : DcModules.getAbstractModules(dco.getModule()))
                if (    gui.isSearchViewInitialized(module.getIndex()) &&
                        module.hasSearchView() &&
                        gui.getSearchView(module.getIndex()).isLoaded() && 
                        !module.isChildModule()) {
                    mv = gui.getSearchView(module.getIndex());
                    mv.add(dco);
                    if (updateRelatedModules && mv.getGroupingPane() != null && mv.getGroupingPane().isEnabled())
                        mv.getGroupingPane().getCurrent().setSelected(dco);
            }
        }
        
        if (dco.getModule().isChildModule() && dco.isLastInLine()) {
            String parentID = dco.getParentID();
            IMasterView parentVw = gui.getSearchView(dco.getModule().getParent().getIndex());
            if (parentVw.getCurrent().getSelectedItemKeys().contains(parentID))
                parentVw.getCurrent().refreshQuickView();
        } 
    }
    
    private void remove() {
        String ID = dco.getID();
        GUI gui = GUI.getInstance();
        
        if (dco.getModule().hasSearchView()) {
            IMasterView mv = gui.getSearchView(dco.getModule().getIndex());
            mv.remove(ID);
        } 
        
        if (updateRelatedModules) {
            for (DcModule module : DcModules.getReferencingModules(dco.getModule().getIndex())) {
                if (    gui.isSearchViewInitialized(module.getIndex()) && 
                        module.hasSearchView() && dco.isLastInLine()) {
                    gui.getSearchView(module.getIndex()).refreshQuickView();
                }
            }
            
            if (dco.getModule().isChildModule()) {
                DcModule parentMod = dco.getModule().getParent();
                if (parentMod.hasSearchView() && gui.isSearchViewInitialized(parentMod.getIndex()))
                    gui.getSearchView(dco.getModule().getParent().getIndex()).refreshQuickView();
            }
            
            IMasterView mv;
            for (DcModule module : DcModules.getAbstractModules(dco.getModule())) {
                if (    gui.isSearchViewInitialized(module.getIndex()) &&
                        module.hasSearchView()) {
                    
                    mv = gui.getSearchView(module.getIndex());
                    if (mv.isLoaded()) 
                        mv.remove(ID);
                }
            }
        }  

        DcIconCache.getInstance().removeIcon(ID);
    }
    
    public void update() {
        String ID = dco.getID();
        DcIconCache.getInstance().updateIcon(ID);
        
        GUI gui = GUI.getInstance();
        
        if (dco.getModule().hasSearchView()) {
            
            IMasterView mv = gui.getSearchView(dco.getModule().getIndex());
            
            if (updateCurrentView) 
                mv.update(dco);

            if (dco.isLastInLine() && mv.getGroupingPane() != null)
                mv.getGroupingPane().getCurrent().setSelected(dco);
        }
        
        if (updateRelatedModules) {
            Collection<DcModule> modules = DcModules.getReferencingModulesAll(dco.getModule().getIndex());
            
            IMasterView mv;
            for (DcModule module : modules) {
                
                if (module.getType() == DcModule._TYPE_MAPPING_MODULE) continue;
                
                if (    gui.isSearchViewInitialized(module.getIndex()) && 
                        gui.getSearchView(module.getIndex()).isLoaded()) {
                    
                    mv = gui.getSearchView(module.getIndex());
                    
                    // update the tree of this module to reflect name changes, etc.
                    if (    mv.getGroupingPane() != null && 
                            mv.getGroupingPane().isEnabled() &&
                            mv.getGroupingPane().isLoaded())
                        mv.getGroupingPane().updateTreeNodes(dco);
                    
                    // only do this for the last item in queue
                    if (dco.isLastInLine())
                        mv.refreshQuickView();
                }
            }
        }

        if (updateRelatedModules) {
            
            IMasterView mv;
            for (DcModule module : DcModules.getAbstractModules(dco.getModule())) {
                
                if (    gui.isSearchViewInitialized(module.getIndex()) && 
                        gui.getSearchView(module.getIndex()).isLoaded()) {
                    
                    mv = gui.getSearchView(module.getIndex());
                    mv.update(dco);
                    
                    if (mv.getGroupingPane() != null && 
                        mv.getGroupingPane().isEnabled())
                        mv.getGroupingPane().getCurrent().setSelected(dco);
                    
                    // update the tree of this module to reflect name changes, etc.
                    if (    mv.getGroupingPane() != null && 
                            mv.getGroupingPane().isEnabled() &&
                            mv.getGroupingPane().isLoaded())
                        mv.getGroupingPane().updateTreeNodes(dco);
                    
                    // only do this for the last item in queue
                    if (dco.isLastInLine())
                        mv.refreshQuickView();
                }
            }
        }
        
        dco = null;
    }


    @Override
    public void notifyTaskStarted(int taskSize) {}

    @Override
    public void notifyProcessed() {}

    @Override
    public boolean isCancelled() {
        return false;
    }
}
