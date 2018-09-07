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

package net.datacrow.console.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;

public class CachedChildView extends View implements ActionListener {

    private ArrayList<DcObject> children = new ArrayList<DcObject>();
    
    public CachedChildView(MasterView mv, int type, IViewComponent vc, int index) {
        super(mv, type, vc, index);
    }
    
    @Override
    public void remove(String[] keys) {
        super.remove(keys);
        
        DcObject dco = null;
        
        for (DcObject child : children) {
            for (String key : keys) {
                if (child.getID().equals(key))
                    dco = child;
            }
        }
        children.remove(dco);
    }

    @Override
    public void open(boolean readonly) {}    
    
    @Override
    public void clear() {
        super.clear(false);
        children.clear();
    }
    
    public void clearView() {
        super.clear(false);
    }
    
    @Override
    public void add(List<DcObject> c) {
        for (DcObject dco : c) {
            if (!children.contains(dco)) {
                //dco.setIDs();
                children.add(dco);
            }
        }
    }
    
    @Override
    public void setParentID(String parentID, boolean show) {
        if (parentID != null && (vc.getItemCount() == 0 || !parentID.equals(getParentID()))) {
            syncCache();
            super.setParentID(parentID, show);
            if (show) 
                loadChildren();
        }
    }     
    
    protected void syncCache() {
        DcObject[] c = children.toArray(new DcObject[0]);
        DcObject dcoCached;
        boolean exists = false;
        for (DcObject dco : vc.getItems()) {
            exists = false;
            for (int j = 0; j < c.length; j++) {
                dcoCached = c[j];
                if (dcoCached.getID().equals(dco.getID())) {
                    dcoCached.copy(dco, true, true);
                    exists = true;
                    break;
                }
            }

            if (!exists)
                children.add(dco);
        }
    }
    
    public void removeChildren(String parentID) {
        Collection<DcObject> c = getChildren(parentID);
        children.removeAll(c);
    }

    @Override
    public void loadChildren() {
        clearView();
        List<DcObject> c = getChildren(getParentID());
        vc.add(c);
    }
    
    public List<DcObject> getChildren(String parentID) {
        syncCache();
        List<DcObject> c = new ArrayList<DcObject>();
        DcObject dco;
        for (int i = 0; i < children.size(); i++) {
            dco = children.get(i);
            if (dco.getParentID().equals(parentID))
                c.add(dco);
        }
        return c;
    }

    @Override
    public Collection<Component> getAdditionalActions() {
        Collection<Component> components = new ArrayList<Component>();
        
        JButton buttonAddChild = ComponentFactory.getButton(DcResources.getText("lblAddChild", getModule().getObjectName()));
        buttonAddChild.addActionListener(this);
        buttonAddChild.setActionCommand("addChild");
        buttonAddChild.setMnemonic('T');
        components.add(buttonAddChild);
        
        return components;
    }

    private void addChild() {
        if (getParentID() != null && !getParentID().equals("")) {
            DcObject dco = getModule().getItem();
            dco.setValue(dco.getParentReferenceFieldIndex(), getParentID());
            add(dco);
            children.add(dco);
        } else {
        	GUI.getInstance().displayErrorMessage(DcResources.getText("msgAddSelectParent"));
        }  
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("addChild"))
            addChild();
    }
}