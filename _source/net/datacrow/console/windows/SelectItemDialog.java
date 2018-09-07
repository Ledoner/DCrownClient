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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.components.lists.DcObjectList;
import net.datacrow.console.components.lists.elements.DcListElement;
import net.datacrow.console.components.lists.elements.DcObjectListElement;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.settings.DcSettings;

public class SelectItemDialog extends DcDialog implements ActionListener, KeyListener, MouseListener {
    
    private Collection<DcObject> selected;
    private Vector<DcListElement> elements;
    private DcObjectList list;
    
    private DcObject dco;
    
    private boolean saved = false;
    
    public SelectItemDialog(DcModule module, String title) {
        super();
        
        setTitle(title);
        buildDialog();
        
        Connector connector = DcConfig.getInstance().getConnector();
        List<DcObject> dcos = connector.getItems(module.getIndex(), null);
        list.add(dcos);
        
        // store the elements for filtering purposes
        elements = new Vector<DcListElement>();
        elements.addAll(list.getElements());
        
        pack();
        
        setSize(DcSettings.getDimension(DcRepository.Settings.stSelectItemDialogSize));
        setCenteredLocation();
        setModal(true);
    }
    
    public DcObject getItem() {
        return dco;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    @Override
    public void close() {
        DcSettings.set(DcRepository.Settings.stSelectItemDialogSize, getSize());
        setVisible(false);
    }
    
    @Override
    public void dispose() {
        if (list != null)
            list.clear();
        
        list = null;
        
        if (elements != null)
            elements.clear();
        
        elements = null;

        if (selected != null)
            selected.clear();
        
        selected = null;
    }    
    
    private void buildDialog() {
        getContentPane().setLayout(Layout.getGBL());
        
        JTextField txtFilter = ComponentFactory.getShortTextField(255);
        txtFilter.addKeyListener(this);

        list = new DcObjectList(DcObjectList._LISTING, false, true);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addMouseListener(this);

        JScrollPane scrollerLeft = new JScrollPane(list);
        scrollerLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollerLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JButton buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
        
        JPanel panelActions = new JPanel();
        panelActions.add(buttonSave);
        panelActions.add(buttonClose);
        
        buttonSave.addActionListener(this);
        buttonSave.setActionCommand("save");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");

        getContentPane().add(txtFilter,     Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));
        getContentPane().add(scrollerLeft,  Layout.getGBC( 0, 2, 1, 1, 40.0, 40.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 0, 0, 0, 0), 0, 0));
        getContentPane().add(panelActions,  Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 0, 5, 0), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("close"))
            close();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        DcShortTextField txtFilter = (DcShortTextField) e.getSource();
        String filter = txtFilter.getText();
        
        if (filter.trim().length() == 0) {
            list.setListData(elements);
        } else {
            Vector<DcListElement> newElements = new Vector<DcListElement>();
            String displayValue;
            for (DcListElement element : elements) {
                displayValue = ((DcObjectListElement) element).getDcObject().toString();
                if (displayValue.toLowerCase().startsWith(filter.toLowerCase()))
                    newElements.add(element);
            }
            list.setListData(newElements);                
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2) {
            dco = list.getSelectedItem();
            close();
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
}