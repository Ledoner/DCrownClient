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

package net.datacrow.console.windows.charts;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class ChartsDialog extends DcDialog implements ActionListener {

    public ChartsDialog() {
        super();
        
        setIconImage(IconLibrary._icoChart.getImage());
        setTitle(DcResources.getText("lblCharts"));
        setHelpIndex("dc.charts");
        
        build();
        
        pack();
        setSize(DcSettings.getDimension(DcRepository.Settings.stChartsDialogSize));
        setResizable(true);
        setCenteredLocation();
    }
    
    private void build() {
        
        ChartPanel chartPanel = new ChartPanel(DcModules.getCurrent().getIndex());
        
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(chartPanel, Layout.getGBC(0, 0, 1, 1, 100.0, 100.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        
        
        JPanel panelAction = new JPanel();
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        panelAction.add(buttonClose);
        
        getContentPane().add(panelAction, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stChartsDialogSize, getSize());
        
        super.close();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close")) {
            close();
        }
    }
}
