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

package net.datacrow.console.windows.drivemanager;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.drivemanager.JobAlreadyRunningException;
import net.datacrow.core.resources.DcResources;

public class JobStatusPanel extends JPanel implements ActionListener {
    
    private final JButton buttonStop = ComponentFactory.getIconButton(IconLibrary._icoStop);
    private final JButton buttonStart = ComponentFactory.getIconButton(IconLibrary._icoStart);
    private final JTextField fldStatus = ComponentFactory.getTextFieldDisabled(); 
    
    private final DriveManagerPanel dmp;

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        
        if (buttonStop != null) {
            buttonStop.setFont(font);
            buttonStart.setFont(font);
            fldStatus.setFont(font);
        }
    }
    
    public JobStatusPanel(DriveManagerPanel dmp) {
        this.dmp = dmp;
        
        setBorder(ComponentFactory.getTitleBorder(DcResources.getText("msgJobStatus", dmp.getTitle())));
        setLayout(Layout.getGBL());
        
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        buttonStart.setActionCommand("start");
        buttonStop.setActionCommand("stop");
        
        buttonStart.addActionListener(this);
        buttonStop.addActionListener(this);
        
        panelActions.add(buttonStart);
        panelActions.add(buttonStop);

        add(fldStatus, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 0, 0), 0, 0));
        add(panelActions, Layout.getGBC(1, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
    }

    protected void setMessage(String msg) {
        fldStatus.setText(msg);
    }
    
    protected void isRunning(boolean b) {
        buttonStop.setEnabled(b);
        buttonStart.setEnabled(!b);
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals("start")) 
                dmp.start();
            else if (e.getActionCommand().equals("stop"))
                dmp.stop();
        } catch (JobAlreadyRunningException jare) {
            GUI.getInstance().displayMessage(jare.getMessage());
        }
    }
}
