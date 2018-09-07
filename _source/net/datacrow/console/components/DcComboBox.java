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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JToolTip;

import org.apache.log4j.Logger;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.components.renderers.ComboBoxRenderer;

public class DcComboBox extends JComboBox implements IComponent {

    private static Logger logger = Logger.getLogger(DcComboBox.class.getName());
    
    public DcComboBox(DefaultComboBoxModel model) {
        super(model);
         
        setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        setRenderer(ComboBoxRenderer.getInstance());
        ComponentFactory.setBorder(this);
    }
    
    public DcComboBox(Object[] items) {
        super(items);
        
        setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        setRenderer(ComboBoxRenderer.getInstance());
        setEditor(new DcComboBoxEditor());
        ComponentFactory.setBorder(this);
    }  
	
    public DcComboBox() {
        super();
        
        setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        setPreferredSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        ComponentFactory.setBorder(this);
        setRenderer(ComboBoxRenderer.getInstance());
        setEditor(new DcComboBoxEditor());
        ComponentFactory.setBorder(this);
    }
    
    @Override
    public void clear() {
        removeAllItems();
    }

    @Override
    public Object getValue() {
        return getSelectedItem();
    }
    
    public void setUneditable() {
        try {
            ((DcComboBoxEditor) getEditor()).fld.setEditable(false);
        } catch (Exception e) {
            logger.debug(e, e);
        }
    }
    
    @Override
    public void setEditable(boolean b) {
        ((DcComboBoxEditor) getEditor()).fld.setEditable(b);
        super.setEditable(b);
        super.setEnabled(b);
    }

    @Override
    public void setValue(Object value) {
        setSelectedItem(value);
    }

    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(GUI.getInstance().setRenderingHint(g));
    } 
    
    @Override
    public void setFont(Font arg0) {
        super.setFont(ComponentFactory.getStandardFont());
    }
    
    @Override
    public void refresh() {}
}
