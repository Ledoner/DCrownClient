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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.components.lists.DcFieldList;
import net.datacrow.console.components.lists.elements.DcFieldListElement;
import net.datacrow.console.components.lists.elements.DcListElement;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.resources.DcResources;

public class FieldSelectionPanel extends JPanel implements KeyListener {
    
    private Vector<DcListElement> elements = new Vector<DcListElement>();;
    private DcFieldList listRight;
    private DcFieldList listLeft;
    
    private DcModule module;
    
    private IFieldSelectionListener listener;
    
    /**
     * Initializes an empty field selector. Use {@link #setFields(Collection)} to add the fields manually.
     * @param module
     */
    public FieldSelectionPanel(DcModule module) {
        this.module = module;
        build();
    }
    
    public FieldSelectionPanel(DcModule module, Collection<DcField> fields) {
        this(module);

        for (DcField field : fields)
            listLeft.add(field);

        elements.addAll(listLeft.getElements());
    }
    
    public void setFieldSelectionListener(IFieldSelectionListener listener) {
        this.listener = listener;
    }
    
    public FieldSelectionPanel(DcModule module, boolean allowPictureFields, boolean allowUiFields, boolean allowMultiRefFields) {
        this(module);

        for (DcField field : module.getFields()) {
            if (    field.isEnabled() && 
                    (       (!field.isUiOnly() || allowUiFields) || 
                            (field.isUiOnly() && allowMultiRefFields && field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION)) &&
                    (allowPictureFields ||
                    (field.getValueType() != DcRepository.ValueTypes._PICTURE &&
                     field.getValueType() != DcRepository.ValueTypes._ICON)))

                listLeft.add(field);
        }

        elements.addAll(listLeft.getElements());
    }
    
    public void setFields(Collection<DcField> fields) {
        elements.clear();
        listLeft.clear();
        listRight.clear();
        
        for (DcField field : fields)
            listLeft.add(field);
        
        elements.addAll(listLeft.getElements());
    }

    public List<DcField> getSelectedFields() {
        return listRight.getFields();
    }
    
    public void setSelectedFields(Collection<DcField> fields) {
        reset();
        for (DcField field : fields) {
            listLeft.remove(field);
            listRight.add(field);
        }
    } 
    
    public void setSelectedFields(DcField[] fields) {
        reset();
        for (int i = 0; i < fields.length; i++) {
            listLeft.remove(fields[i]);
            listRight.add(fields[i]);
        }
    }    
    
    public void setSelectedFields(int[] fields) {
        reset();
        for (int i = 0; i < fields.length; i++) {
            for (DcField field : module.getFields()) {
                if (field.getIndex() == fields[i]) {
                    listLeft.remove(field);
                    listRight.add(field);
                    break;
                }
            }
        }
    }
    
    private void reset() {
        listRight.setListData(new Vector<DcListElement>());
    }
    
    public void clear() {
        if (listLeft != null)
            listLeft.clear();
        
        listLeft = null;
        
        if (listRight != null)
            listRight.clear();
        
        listRight = null;
        
        if (elements != null)
            elements.clear();
        
        elements = null;
        module = null;
        listener = null;
    }    
    
    private void build() {
        setLayout(Layout.getGBL());
        
        JTextField txtFilter = ComponentFactory.getShortTextField(255);
        txtFilter.addKeyListener(this);

        listLeft = new DcFieldList();
        listLeft.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listLeft.addMouseListener(new ListMouseListener(ListMouseListener._RIGHT));

        listRight = new DcFieldList();
        listRight.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listRight.addMouseListener(new ListMouseListener(ListMouseListener._LEFT));

        NavigationPanel panelNav = new NavigationPanel(listRight);
        JScrollPane scrollerLeft = new JScrollPane(listLeft);
        scrollerLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollerLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JScrollPane scrollerRight = new JScrollPane(listRight);
        scrollerRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollerRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel panelFilter = new JPanel();
        panelFilter.setLayout(Layout.getGBL());
        
        panelFilter.add(ComponentFactory.getLabel(DcResources.getText("lblFilter")), 
                 Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));
        panelFilter.add(txtFilter,     Layout.getGBC( 1, 0, 1, 1, 20.0, 20.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));
        
        add(panelFilter,  Layout.getGBC( 0, 0, 2, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 0, 0, 0), 0, 0));
        add(ComponentFactory.getLabel(DcResources.getText("lblAvailableFields")),  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 0, 0, 0), 0, 0));
        add(ComponentFactory.getLabel(DcResources.getText("lblSelectedFields")), Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 0, 0, 0), 0, 0));
        add(scrollerLeft,  Layout.getGBC( 0, 2, 1, 1, 20.0, 20.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 0, 0, 0), 0, 0));
        add(scrollerRight, Layout.getGBC( 1, 2, 1, 1, 20.0, 20.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 0, 0, 0), 0, 0));

        add(panelNav,  Layout.getGBC(2, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));

        scrollerLeft.setMinimumSize(new Dimension(360, 100));
        scrollerLeft.setPreferredSize(new Dimension(360, 100));
        scrollerRight.setMinimumSize(new Dimension(360, 100));
        scrollerRight.setPreferredSize(new Dimension(360, 100));
    }
    
    private class ListMouseListener implements MouseListener {
        
        public static final int _LEFT = 0;
        public static final int _RIGHT = 1;
        
        int direction;
        
        public ListMouseListener(int direction) {
            this.direction = direction;
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                if (direction == _LEFT) {
                    DcField field = listRight.getSelected();
                    
                    if (field == null) return;
                    
                    if (listener != null) listener.fieldDeselected(field);
                    
                    listLeft.add(field);
                    listRight.remove();
                    listRight.clearSelection();
                } else {
                    DcField field =  listLeft.getSelected();
                    
                    if (field == null) return;
                    
                    if (listener != null) listener.fieldSelected(field);
                    
                    listRight.add(field);
                    listLeft.remove();
                    listLeft.clearSelection();
                }
            }  
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseClicked(MouseEvent e) {}
    } 
    
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    private Vector<DcListElement> getAvailableElements() {
        List<DcListElement> selected = listRight.getElements();;
        Vector<DcListElement> available = new Vector<DcListElement>();
        
        // loop through all elements and ignore the ones that are selected
        for (DcListElement elem : elements) {
            if (!selected.contains(elem)) 
                available.add(elem);
        }
        
        return available;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        DcShortTextField txtFilter = (DcShortTextField) e.getSource();
        String filter = txtFilter.getText();
        
        if (filter.trim().length() == 0) {
            listLeft.setListData(getAvailableElements());
        } else {
            Vector<DcListElement> newElements = new Vector<DcListElement>();

            String displayValue;
            for (DcListElement element : getAvailableElements()) {
                displayValue = ((DcFieldListElement) element).getField().getLabel();
                if (displayValue.toLowerCase().startsWith(filter.toLowerCase()))
                    newElements.add(element);
            }
            listLeft.setListData(newElements);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clear();
        removeAll();
    }
}
