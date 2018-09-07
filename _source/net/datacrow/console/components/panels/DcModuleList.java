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

package net.datacrow.console.components.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.components.DcList;
import net.datacrow.console.components.DcMultiLineToolTip;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class DcModuleList extends DcList implements ListSelectionListener {

    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    private static Logger logger = Logger.getLogger(DcModuleList.class.getName());
    
    protected int currentIndex = -1;
    protected boolean listenForChanges = true;
    
    protected Map<Integer, List<ModulePanel>> elements;
    
	public DcModuleList() {
        super();
        
        elements = new HashMap<Integer, List<ModulePanel>>();
        
        addModules();
        addListSelectionListener(this);
        setCellRenderer(new ModuleCellRenderer());
	}

	public void clear() {
	    elements.clear();
	    elements = null;
	}
	
    @Override
    public void setBackground(Color color) {
        super.setBackground(Color.WHITE);
    }
    
    @Override
    public void setFont(Font font) {
        for (JPanel panel : getData()) 
            panel.setFont(font);
    }    
    
	public void addModules() {
        if (elements != null) elements.clear();
        
        DcModule referencedMod;
        List<ModulePanel> c;
        for (DcModule module : DcModules.getAllModules()) {
            try {
                
                c = null;
                
                if (module.isSelectableInUI() && module.isEnabled()) {
                    
                    c = new ArrayList<ModulePanel>();
                    c.add(new ModulePanel(module, ModulePanel._ICON32));
                    
                    for (DcField field : module.getFields()) {
                        referencedMod = DcModules.getReferencedModule(field);
                        if (    referencedMod.isEnabled() &&
                        		referencedMod.getIndex() != module.getIndex() && 
                                referencedMod.getType() != DcModule._TYPE_PROPERTY_MODULE &&
                                referencedMod.getType() != DcModule._TYPE_EXTERNALREFERENCE_MODULE &&
                                referencedMod.getIndex() != DcModules._CONTACTPERSON &&
                                referencedMod.getIndex() != DcModules._CONTAINER) {
                            
                            c.add(new ModulePanel(referencedMod, ModulePanel._ICON16));
                        }
                    }
                    elements.put(module.getIndex(), c);
                }
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        setModules(DcSettings.getInt(DcRepository.Settings.stModule));
	}
    
    protected void setModules(int current) {
        listenForChanges = false;
        
        int module = current; 
        if (!elements.containsKey(current)) {
            for (DcModule m : DcModules.getModules()) {
                if (    m.getType() != DcModule._TYPE_MAPPING_MODULE && 
                        m.hasReferenceTo(module) && m.isTopModule())
                    module = m.getIndex();
            }
        }
        
        Vector<ModulePanel> v = new Vector<ModulePanel>();
        ModulePanel panel;
        for (List<ModulePanel> c : elements.values()) {
            panel = c.get(0);
            if (panel.getModule() == module)
                v.addAll(c);
            else
                v.add(panel);
        }
        
        setListData(v);
        
        for (ModulePanel panel2 : v) {
            if (panel2.getModule() == current)
                setSelectedValue(panel2, true);                
        }
        
        listenForChanges = true;
    }
    
    public void setSelectedModule(int module) {
        if ((DcModules.get(module).isTopModule() && !DcModules.get(module).hasDependingModules()))
            setModules(module);
    }
    
    private void setActiveModule() {
        listenForChanges = false;
        Object item = getSelectedValue();
        if (item instanceof ModulePanel) {
            int module = ((ModulePanel) item).getModule();
            if (module != currentIndex) {
                currentIndex = module;
                setSelectedModule(currentIndex);
                if (GUI.getInstance().getMainFrame() != null) {
                    GUI.getInstance().getMainFrame().changeModule(module);
                }
            }
        }
        listenForChanges = true;
    }
    
    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }    
    
    protected static class ModulePanel extends JPanel {
        private final int module;
        
        public static final int _ICON16 = 0;
        public static final int _ICON32 = 1;
        
        public ModulePanel(DcModule module, int icon) {
            super(layout);
            this.module = module.getIndex();

            if (icon == _ICON16) {
                JLabel label = ComponentFactory.getLabel("");
                label.setPreferredSize(new Dimension(12, 12));
                label.setMinimumSize(new Dimension(12, 12));
                label.setMaximumSize(new Dimension(12, 12));
                
                label.setForeground(Color.BLACK);
            
                add(label);
                add(ComponentFactory.getLabel(module.getIcon16()));
            } else {
                add(ComponentFactory.getLabel(module.getIcon32()));
            }

            add(ComponentFactory.getLabel(module.getLabel()));
        }
        
        public int getModule() {
            return module;
        }
        
        @Override
        public JToolTip createToolTip() {
            return new DcMultiLineToolTip();
        }        
        
        @Override
        public String getToolTipText() {
            return DcModules.get(module).getDescription();
        }
        
        @Override
        public void setFont(Font font) {
            Component[] components = getComponents();
            for (int i = 0; i < components.length; i++) {
                components[i].setFont(font);
                components[i].setForeground(ComponentFactory.getCurrentForegroundColor());
            }
        }          
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (listenForChanges)
            setActiveModule();
    }
    
    private static class ModuleCellRenderer implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            JComponent component = (JComponent) value;
            
            if (value instanceof ModulePanel) {
                int module = ((ModulePanel) value).getModule();
                component.setToolTipText(DcModules.get(module).getDescription());
            }
            
            Color selectionColor = DcSettings.getColor(DcRepository.Settings.stSelectionColor);
            component.setBackground(isSelected ? selectionColor : Color.WHITE);
            return component;
        }
    }    
}