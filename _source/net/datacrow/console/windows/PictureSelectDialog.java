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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcPictureField;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;

public class PictureSelectDialog extends DcDialog implements ActionListener {
    
    private JComboBox cbFields = ComponentFactory.getComboBox();
    private DcObject dco;
    private DcImageIcon image;
    
    public PictureSelectDialog(JFrame parent, DcObject dco, DcImageIcon image) {
        super(parent);

        setTitle(DcResources.getText("lblWhichPictureIsThis"));
        
        this.dco = dco;
        this.image = image;
        
        build();
        
        setSize(640, 480);
        setCenteredLocation();
        toFront();
        setModal(true);
    }

    public void setImage() {
        DcField field = (DcField) cbFields.getSelectedItem();
        dco.setValue(field.getIndex(), image);
    }
    
    @Override
    public void close() {
        cbFields.removeAllItems();
        cbFields = null;
        dco = null;
        image = null;
        
        super.close();
    }
    
    public void build() {
        
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton buttonOk = ComponentFactory.getButton(DcResources.getText("lblOK"));
        JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        
        buttonOk.setActionCommand("Ok");
        buttonCancel.setActionCommand("Cancel");
        
        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
        
        panelActions.add(buttonCancel);
        panelActions.add(buttonOk);
        
        for (DcField field : dco.getFields()) {
            if (field.getValueType() == DcRepository.ValueTypes._PICTURE)
                cbFields.addItem(field);
        }
        
        DcPictureField picFld = ComponentFactory.getPictureField(true, false);
        picFld.setValue(image);
        
        getContentPane().setLayout(Layout.getGBL());
        getContentPane().add(ComponentFactory.getLabel("Picture field"),  
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));        
        getContentPane().add(cbFields,  
                Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));     
        getContentPane().add(picFld,  
                Layout.getGBC( 0, 1, 2, 1, 80.0, 80.0
               ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions,  
                Layout.getGBC( 0, 2, 2, 1, 1.0, 1.0
               ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        
        setResizable(true);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Ok")) {
            setImage();
            close();
        } else if (e.getActionCommand().equals("Cancel")) {
            close();
        }
    }
}
