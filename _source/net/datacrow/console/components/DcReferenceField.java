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

package net.datacrow.console.components;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.itemforms.IItemFormListener;
import net.datacrow.console.windows.itemforms.ItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.server.Connector;

public class DcReferenceField extends JComponent implements IComponent, ActionListener, IItemFormListener {

    private DcObjectComboBox cb;
    private JButton btCreate = ComponentFactory.getIconButton(IconLibrary._icoOpenNew);
    
    private final int referenceModIdx;
    private boolean allowCreate;
    
    public DcReferenceField(int referenceModIdx) {
        super();
        
        setFont(ComponentFactory.getStandardFont());
        
        this.referenceModIdx = referenceModIdx;
        Connector connector = DcConfig.getInstance().getConnector();
        this.allowCreate = connector.getUser().isEditingAllowed(DcModules.get(referenceModIdx));
        
        cb = ComponentFactory.getObjectCombo(referenceModIdx);
        
        setLayout(Layout.getGBL());
        add(cb, Layout.getGBC( 0, 0, 1, 1, 100.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));        
        
        if (connector.getUser().isEditingAllowed(DcModules.get(referenceModIdx)))
            add(btCreate, Layout.getGBC( 1, 0, 1, 1, 0.0, 0.0
                    ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                     new Insets( 0, 0, 0, 0), 0, 0));        
        
        btCreate.addActionListener(this);
        btCreate.setActionCommand("create");
        btCreate.setEnabled(allowCreate);
    }

    @Override
    public void setEditable(boolean b) {
        btCreate.setVisible(b);
        btCreate.setEnabled(b && allowCreate);
        cb.setEditable(b);
    }
    
    @Override
    public Object getValue() {
        return cb.getSelectedItem();
    }
    
    public JComboBox getComboBox() {
        return cb;
    }
    
    @Override
    public void setValue(Object o) {
        cb.setValue(o);
    }
    
    @Override
    public void clear() {
        btCreate = null;
        cb = null;
        
        removeAll();
    }
    
    @Override
    public void refresh() {
        cb.refresh();
    }

    @Override
    public void notifyItemSaved(DcObject dco) {
        setValue(dco);
    }

    private void create() {
        DcObject dco = DcModules.get(referenceModIdx).getItem();
        ItemForm itemForm = new ItemForm(false, false, dco, true);
        itemForm.setListener(this);
        itemForm.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("create"))
            create();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    }  
}