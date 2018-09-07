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

package net.datacrow.console.windows;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcPictureField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.resources.DcResources;

public class PictureDialog extends DcDialog implements ActionListener {
    
    private DcPictureField pf = ComponentFactory.getPictureField(true, true);
    
    public PictureDialog(DcImageIcon ii) {
        super();
 
        setTitle(DcResources.getText("lblPictureViewer"));
        
        getContentPane().setLayout(Layout.getGBL());
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        panelActions.add(buttonClose);
        
        getContentPane().add(pf, Layout.getGBC(0, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));        
        getContentPane().add(panelActions, Layout.getGBC(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets(0, 0, 5, 5), 0, 0));
        
        pf.setValue(ii);
        
        pack();
        
        setSize(500, 500);
        setCenteredLocation();
        setModal(true);
        setVisible(true);
    }
    
    @Override
    public void close() {
        pf = null;
        super.close();
    }
    
    public boolean isPictureChanged() {
        return pf != null ? pf.isChanged() : false;
    }
    
    public DcImageIcon getImage() {
        return (DcImageIcon) pf.getValue();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            setVisible(false);
    }    
}
