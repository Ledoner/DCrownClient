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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.resources.DcResources;

import org.apache.log4j.Logger;


public class DcTimeField extends JComponent implements IComponent {
    
    private static Logger logger = Logger.getLogger(DcTimeField.class.getName());
    
    private DcNumberField fldMinutes = ComponentFactory.getNumberField();
    private DcNumberField fldSeconds = ComponentFactory.getNumberField();
    private DcNumberField fldHours = ComponentFactory.getNumberField();
    
    public DcTimeField() {
    	buildComponent();
    }
    
    @Override
    public void setValue(Object o) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        
        int value = 0;
        if (o instanceof String) {
            try {
            	value = Integer.parseInt(o.toString());
            } catch (Exception e) {
                logger.warn("Invalid value " + value, e);
            }
        } else {
        	value = ((Long) o).intValue();
        }

        int minutes = 0;
        int seconds = 0;
        int hours = 0;
        int days = 0;

        if (value != 0) {
        	cal.set(Calendar.SECOND, value);
        	minutes = cal.get(Calendar.MINUTE);
        	seconds = cal.get(Calendar.SECOND);
            hours = cal.get(Calendar.HOUR_OF_DAY);
            days = cal.get(Calendar.DATE) - 1;
            
        }
        fldMinutes.setValue(minutes);
        fldSeconds.setValue(seconds);
        fldHours.setValue(hours + (days > 0 ? days * 24 : 0));
    }
    
    @Override
    public void clear() {
        fldHours = null;
        fldMinutes = null;
        fldSeconds = null;
    }
    
    /**
     * Retrieves the value as seconds
     */
    @Override
    public Object getValue() {
    	int seconds = fldSeconds.getValue() != null ? ((Long) fldSeconds.getValue()).intValue() : 0;
        int minutes = fldMinutes.getValue() != null ? ((Long) fldMinutes.getValue()).intValue() : 0;
        int hours = fldHours.getValue() != null ? ((Long) fldHours.getValue()).intValue() : 0;
        
        int value = seconds == -1 ? 0 : seconds;
        value+= minutes == -1 ? 0 : minutes * 60;
        value+= hours == -1 ? 0 : hours * 60 * 60;
        
        return value <= 0 ? null : value;
    }
    
    @Override
    public void setEditable(boolean b) {
        setEnabled(b);
    }
    
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
    	fldHours.setEnabled(b);
        fldMinutes.setEnabled(b);
        fldSeconds.setEnabled(b);
    }
    
    private void buildComponent() {
    	setLayout(Layout.getGBL());

        JLabel labelMinutes = ComponentFactory.getLabel(DcResources.getText("lblMinutes"));
        JLabel labelSeconds = ComponentFactory.getLabel(DcResources.getText("lblSeconds"));
        JLabel labelHours = ComponentFactory.getLabel(DcResources.getText("lblHours"));
        
        add(fldHours,   Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets( 0, 0, 0, 5), 0, 0));
        add(labelHours, Layout.getGBC(1, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                        new Insets( 0, 0, 0, 0), 0, 0));
        add(fldMinutes, Layout.getGBC( 2, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets( 0, 0, 0, 5), 0, 0));
        add(labelMinutes,Layout.getGBC(3, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					    new Insets( 0, 0, 0, 0), 0, 0));
        add(fldSeconds, Layout.getGBC( 4, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets( 0, 0, 0, 5), 0, 0));
        add(labelSeconds,Layout.getGBC(5, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					    new Insets( 0, 0, 0, 0), 0, 0));
    }
    
    @Override
    public void refresh() {}  
}
