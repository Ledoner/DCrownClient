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

package net.datacrow.console.components.lists.elements;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;

import net.datacrow.console.components.DcLabel;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.server.Connector;
import net.datacrow.settings.DcSettings;
import net.datacrow.settings.Settings;

/**
 * A list element which is capable of displaying a DcObject.
 * 
 * @author Robert Jan van der Waals
 */
public abstract class DcObjectListElement extends DcListElement {

    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
    protected static final int fieldHeight = 21;
    protected String key;
    protected int module;
    protected DcObject dco;
    
    public DcObjectListElement(int module) {
        this.module = module;
    }
    
    public int getModule() {
    	return module;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setDcObject(DcObject dco) {
        this.dco = dco;
        this.key = dco.getID();
        
        // when adding an existing item the renderer will take care of the loading/
        // for new items, the component needs to be fully build.
        if (dco.isNew()) build();
    }

    public DcObject getDcObject() {
        Connector connector = DcConfig.getInstance().getConnector();
        dco = dco == null ? connector.getItem(module, key) : dco;
        if (dco != null) dco.markAsUnchanged();
        return dco;
    }
    
    public abstract Collection<Picture> getPictures();
    
    private boolean loading = false;
    
    public int[] getFields() {
    	DcModule module = DcModules.get(getModule());
    	return module.isAbstract() ? new int[] {DcObject._ID} : module.getMinimalFields(getFields(getModule()));
    }
    
    public boolean load() {
        int count = getComponentCount();
        if (count == 0 && !loading) {
            DcModule module = DcModules.get(getModule());
            
            if (dco == null) {
                Connector connector = DcConfig.getInstance().getConnector();
                dco = connector.getItem(getModule(), key, getFields());
                if (module.isAbstract())
                    dco.reload();
            }

            loading = true;
            build();
            loading = false;
            return true;
        }

        return false;
    }
    
    private Collection<Integer> getFields(int module) {
        Settings settings = DcModules.get(module).getSettings();
        Collection<Integer> fields = new ArrayList<Integer>();
        for (int field : settings.getIntArray(DcRepository.ModuleSettings.stCardViewItemDescription))
            fields.add(Integer.valueOf(field));

        for (int field : settings.getIntArray(DcRepository.ModuleSettings.stCardViewPictureOrder))
            fields.add(Integer.valueOf(field));
        
        return fields;
    }
    
    public void update(DcObject dco) {
        this.dco = dco;
        clear();
        build();
    }    
    
    @Override
    public void setForeground(Color fg) {
    	for (Component c : getComponents()) {
    		c.setForeground(fg);
    	}
    }
    
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        return panel;
    }
    
    protected DcLabel getLabel(int field, boolean label, int width) {
        DcLabel lbl = new DcLabel();
        if (label) {
            lbl.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontBold));
            lbl.setText(dco.getLabel(field));
        } else {
            lbl.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
            lbl.setText(dco.getDisplayString(field));
        }
        
        lbl.setPreferredSize(new Dimension(width, fieldHeight));
        
        return lbl;
    }       
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        for (int i = 0; i < getComponents().length; i++) {
            getComponents()[i].setBackground(color);
        }
    }
    
    @Override
    public void clear() {
        // DO NOT DESTROY THE COMPONENTS. This component is re-used!
        // DO NOT ENABLE THIS: super.clear(); 
        
        removeAll();
        
        if (dco != null && !dco.isNew()) {
            dco.release();
            dco = null;
        }
            
        revalidate();
        repaint();
    }
    
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
        dco = null;
        key = null;
	}
}
