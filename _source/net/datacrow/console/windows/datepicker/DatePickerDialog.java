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

package net.datacrow.console.windows.datepicker;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.resources.DcResources;

public class DatePickerDialog extends DcDialog implements ActionListener {
    
    private Calendar original;
    private Calendar calendar;
    private MonthPanel monthPanel;
    private NavigationPanel navigationPanel;

    public DatePickerDialog(JFrame parent) {
        super(parent);
        build();
    }
    
    public DatePickerDialog() {
        super();
        build();
    }

    public Calendar getDate() {
        return calendar;
    }
    
    public void setDate(Date dt) {
        if (dt != null) {
            if (calendar == null)
                calendar = new GregorianCalendar();
            
            calendar.setTime(dt);
            updateScreen(calendar);
            
            original = Calendar.getInstance();
            original.setTime(dt);
        }
    }    
    
    protected void setDate(Calendar cal) {
        this.calendar = cal;
    }     
    
    protected Calendar getCalendar() {
        return calendar;
    }

    protected void updateScreen(Calendar c) {
        navigationPanel.setCurrentMonth(c);
        setMonthPanel(c);
    }

    protected void setMonthPanel(Calendar c) {
        if (c != null)
            calendar.setTime(c.getTime());
        
        if (monthPanel != null) {
            monthPanel.clear();
            getContentPane().remove(monthPanel);
        }
        
        monthPanel = new MonthPanel(this, calendar);
        ComponentFactory.setBorder(monthPanel);
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(monthPanel, Layout.getGBC( 0, 1, 1, 1, 90.0, 90.0
                            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                             new Insets( 5, 5, 5, 5), 0, 0));

        repaint();
        monthPanel.revalidate();
    }

    protected void dayPicked(int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        setDate(calendar.getTime());
        close();
    }

    @Override
    public void close() {
        if (monthPanel != null) 
            monthPanel.clear();

        if (navigationPanel != null)
            navigationPanel.clear();
        
        super.close();
    }    
    
    private void build() {
        setTitle(DcResources.getText("lblPickADate"));
        
        setModal(true);
        setResizable(false);
        calendar = new GregorianCalendar();
        navigationPanel = new NavigationPanel(this);

        /** actions */
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(this);
        
        JButton buttonClear = ComponentFactory.getButton(DcResources.getText("lblClear"));
        buttonClear.setActionCommand("clear");
        buttonClear.addActionListener(this);
        
        actionsPanel.add(buttonClear);
        actionsPanel.add(buttonCancel);        
        
        /** main */
        getContentPane().setLayout(Layout.getGBL());

        getContentPane().add(navigationPanel, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));

        getContentPane().add(actionsPanel,    Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
               ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 0, 0, 0, 0), 0, 0));
        
        updateScreen(calendar);
        
        pack();
        setSize(new Dimension(350, 300));
        setCenteredLocation();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("save"))
            close();
        
        if (e.getActionCommand().equals("cancel")) {
            calendar = original;
            close();
        }

        if (e.getActionCommand().equals("clear")) {
            calendar = null;
            close();
        }

    }
}
