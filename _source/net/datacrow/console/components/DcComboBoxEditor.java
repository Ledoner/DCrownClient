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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.lang.reflect.Method;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;

public class DcComboBoxEditor extends BasicComboBoxEditor {
    
    protected JTextField fld;
    private Object oldValue;

    public DcComboBoxEditor() {
        fld = new BorderlessTextField("", 9);
        fld.setBorder(null);
    }

    @Override
    public Component getEditorComponent() {
        return fld;
    }

    /** 
     * Sets the item that should be edited. 
     *
     * @param anObject the displayed value of the editor
     */
    @Override
    public void setItem(Object anObject) {
        if ( anObject != null )  {
            fld.setText(anObject.toString());
            oldValue = anObject;
        } else {
            fld.setText("");
        }
    }

    @Override
    public Object getItem() {
        Object newValue = fld.getText();
        
        if (oldValue != null && !(oldValue instanceof String))  {
            // The original value is not a string. Should return the value in it's
            // original type.
            if (newValue.equals(oldValue.toString()))  {
                return oldValue;
            } else {
                // Must take the value from the editor and get the value and cast it to the new type.
                Class<?> cls = oldValue.getClass();
                try {
                    Method method = cls.getMethod("valueOf", new Class[]{String.class});
                    newValue = method.invoke(oldValue, new Object[] { fld.getText()});
                } catch (Exception ex) {
                    // Fail silently and return the newValue (a String object)
                }
            }
        }
        return newValue;
    }

    @Override
    public void selectAll() {
        fld.selectAll();
        fld.requestFocus();
    }

    // This used to do something but now it doesn't.  It couldn't be
    // removed because it would be an API change to do so.
    @Override
    public void focusGained(FocusEvent e) {}
    
    // This used to do something but now it doesn't.  It couldn't be
    // removed because it would be an API change to do so.
    @Override
    public void focusLost(FocusEvent e) {}

    @Override
    public void addActionListener(ActionListener l) {
        fld.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        fld.removeActionListener(l);
    }

    static class BorderlessTextField extends DcShortTextField {
        public BorderlessTextField(String value,int n) {
            super(500);
            setMinimumSize(new Dimension(100, ComponentFactory.getPreferredFieldHeight()));
        }

        // workaround for 4530952
        @Override
        public void setText(String s) {
            if (getText().equals(s)) {
                return;
            }
            super.setText(s);
        }

        @Override
        public void setBorder(Border b) {}
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(GUI.getInstance().setRenderingHint(g));
        }
    }
    
//    /**
//     * A subclass of BasicComboBoxEditor that implements UIResource.
//     * BasicComboBoxEditor doesn't implement UIResource
//     * directly so that applications can safely override the
//     * cellRenderer property with BasicListCellRenderer subclasses.
//     * <p>
//     * <strong>Warning:</strong>
//     * Serialized objects of this class will not be compatible with
//     * future Swing releases. The current serialization support is
//     * appropriate for short term storage or RMI between applications running
//     * the same version of Swing.  As of 1.4, support for long term storage
//     * of all JavaBeans<sup><font size="-2">TM</font></sup>
//     * has been added to the <code>java.beans</code> package.
//     * Please see {@link java.beans.XMLEncoder}.
//     */
//    public static class UIResource extends BasicComboBoxEditor implements javax.swing.plaf.UIResource {
//    }
}
