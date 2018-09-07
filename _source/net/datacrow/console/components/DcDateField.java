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
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToolTip;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.datepicker.DatePickerDialog;
import net.datacrow.core.IconLibrary;

import org.apache.log4j.Logger;

public class DcDateField extends JComponent implements IComponent, ActionListener {

    private static Logger logger = Logger.getLogger(DcDateField.class.getName());
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, d MMMMM yyyy");
    
    private JTextField text;
    private JButton button;

    public DcDateField() {

        text = ComponentFactory.getTextFieldDisabled();
        button = ComponentFactory.getIconButton(IconLibrary._icoCalendar);
        
        this.setLayout(Layout.getGBL());
        
        button.addActionListener(this);

        add( text,   Layout.getGBC( 0, 0, 1, 1, 80.0, 80.0
                    ,GridBagConstraints.WEST, GridBagConstraints.BOTH,
                     new Insets(0, 0, 0, 0), 0, 0));
        add( button, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.EAST, GridBagConstraints.NONE,
                     new Insets(0, 0, 0, 0), 0, 0));
    }

    private SimpleDateFormat getDateFormat() {
        return sdf;
    }
    
    @Override
    public void setEditable(boolean b) {
        button.setEnabled(b);
    }
    
    @Override 
    public void setEnabled(boolean b) {
        button.setEnabled(b);
        text.setEnabled(b);
    }
    
    @Override
    public void clear() {
        text = null;
        button = null;
    }
    
    @Override
    public void setValue(Object value) {
        if (value instanceof Date) {
            setValue((Date) value);
        } else if (value instanceof String) {
            try {
                setValue(getDateFormat().parse((String) value));
            } catch (Exception e) {
                logger.warn("Could not set [" + value + "]. Not a valid date", e);
            }
        }
    }      
    
    public void setValue(Date date) {
        if (date == null)
            text.setText("");
        else 
            text.setText(getDateFormat().format(date));
    }
    
    @Override
    public Object getValue() {
        String date = text.getText();
        
        if (date != null && date.length() > 0) {
	        try {
	            return getDateFormat().parse(date);
	        } catch (Exception e) {
	            logger.warn("Could not parse [" + date + "]. Not a valid date", e);
	        }
        }
        
        return null;
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
    public void actionPerformed(ActionEvent e) {
        
        Component top = null;
        Component parent = getParent();
        while (parent != null) {
            parent = parent.getParent();
            top = parent != null ? parent : top;
        }
        
        DatePickerDialog dp;
        if (top != null && top instanceof JFrame)
            dp = new DatePickerDialog((JFrame) top);
        else
            dp = new DatePickerDialog();
            
        dp.setDate((Date) getValue());
        dp.setVisible(true);
        
        setValue(dp.getDate() != null ? dp.getDate().getTime() : null);
    }
    
    @Override
    public void refresh() {}
}