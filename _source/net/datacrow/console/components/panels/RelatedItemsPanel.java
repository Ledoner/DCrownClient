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

package net.datacrow.console.components.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcPanel;
import net.datacrow.console.components.DcPopupMenu;
import net.datacrow.console.components.DcShortTextField;
import net.datacrow.console.components.lists.DcListModel;
import net.datacrow.console.components.lists.DcObjectList;
import net.datacrow.console.components.lists.elements.DcListElement;
import net.datacrow.console.components.lists.elements.DcObjectListElement;
import net.datacrow.console.windows.itemforms.DcMinimalisticItemForm;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.console.ISimpleItemView;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class RelatedItemsPanel extends DcPanel implements MouseListener, ISimpleItemView, ActionListener, KeyListener, IClient {
    
    private static Logger logger = Logger.getLogger(RelatedItemsPanel.class.getName());
    
    private DcObjectList list = new DcObjectList(DcObjectList._LISTING, false, true);
    private List<DcListElement> all = new ArrayList<DcListElement>();
    
    private boolean cancelled = false;
    private DcObject dco;
    
    @Override
    public void setParentID(String ID) {}
    
    @Override
    public String getParentID() {
        return null;
    }

    public RelatedItemsPanel(DcObject dco) {
        this.dco = dco;

        setIcon(IconLibrary._icoRelations);
        setTitle(DcResources.getText("lblRelatedItems"));

        build();
//        load();
    }

    private void open() {
        DcObject dco = list.getSelectedItem();
        if (dco != null) {
            dco.markAsUnchanged();
            DcMinimalisticItemForm itemForm = new DcMinimalisticItemForm(false, true, dco, this);
            itemForm.setVisible(true);
        }
    }
    
    public void removeReferences() {
        Collection<DcObject> items = list.getSelectedItems();
        if (items != null && items.size() > 0)  
            new RemoveReferences(this, dco, list.getSelectedItems()).start();
    }
    
    public void open(boolean edit) {
        DcObject dco = list.getSelectedItem();
        if (dco != null) {
            dco.markAsUnchanged();
            DcMinimalisticItemForm itemForm = new DcMinimalisticItemForm(!edit, true, dco, this);
            itemForm.setVisible(true);
        }
    }
    
    public void setData(List<DcObject> items) {
        final List<DcObject> c = new ArrayList<DcObject>();
        c.addAll(items);
        Collections.sort(c);
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(
                    new Thread(new Runnable() { 
                        @Override
                        public void run() {
                            list.add(c);
                            all.addAll(list.getElements());
                        }
                    }));
        } else {
            list.add(c);
            all.addAll(list.getElements());
        }
    }
    
    @Override
    public void load() {
        SwingUtilities.invokeLater(
                new Thread(new Runnable() { 
                    @Override
                    public void run() {
                        list.clear();
                        all.clear();
                        
                        Connector connector = DcConfig.getInstance().getConnector();
                        List<DcObject> items = connector.getReferencingItems(dco.getModuleIdx(), dco.getID());
                        setData(items);
                    }
                }));
    }
    
    @Override
    public void notify(String msg) {
        logger.info(msg);
    }
    
    @Override
    public void notifyError(Throwable t) {
        logger.error(t, t);
    }

    @Override
    public void notifyWarning(String msg) {
        logger.warn(msg);
    }
    
    @Override
    public void notifyProcessed() {
        updateProgressBar();
    }

    @Override
    public void clear() {
        if (all != null) all.clear();
        if (list != null) list.clear();
        
        all = null;
        list = null;
        cancelled = true;
        
        super.clear();
    }

    private void build() {
        list.addMouseListener(this);
        
        JScrollPane scroller = new JScrollPane(list);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scroller.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        setLayout(Layout.getGBL());
        
        JTextField txtFilter = ComponentFactory.getShortTextField(255);
        txtFilter.addKeyListener(this);        

        JPanel panel = new JPanel();
        panel.setLayout(Layout.getGBL());
        panel.add(ComponentFactory.getLabel(DcResources.getText("lblFilter")), Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 0, 0, 0, 0), 0, 0));
        panel.add(txtFilter, Layout.getGBC( 1, 0, 1, 1, 100.0, 100.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 0, 0, 0), 0, 0));
        
        add(panel, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 10, 5, 0, 5), 0, 0));
        add(scroller,  Layout.getGBC( 0, 2, 1, 1, 100.0, 100.0
                ,GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                 new Insets(5, 5, 5, 5), 0, 0));
        add(getProgressPanel(), Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        DcObjectList list = (DcObjectList) e.getSource();
        
        if (SwingUtilities.isRightMouseButton(e)) {
            if (list.getSelectedIndex() == -1) {
                int index = list.locationToIndex(e.getPoint());
                list.setSelectedIndex(index);
            }
            
            if (list.getSelectedIndex() > -1) {
                JPopupMenu menu = new RelatedItemsPopupMenu();                
                menu.setInvoker(list);
                menu.show(list, e.getX(), e.getY());
            }
        }
        
        if (e.getClickCount() == 2 && list.getSelectedIndex() > -1) 
            open();
    }
        
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("open_readonly"))
            open(false);        
        else if (e.getActionCommand().equals("open_edit"))
            open(true);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        DcShortTextField txtFilter = (DcShortTextField) e.getSource();
        String filter = txtFilter.getText();
        
        if (filter.trim().length() == 0) {
            ((DcListModel) list.getModel()).clear();
            list.addElements(all);
        } else {
            List<DcListElement> filtered = new ArrayList<DcListElement>();
            for (DcListElement el : all) {
                DcObjectListElement element = (DcObjectListElement) el;
                if (element.getDcObject().toString().toLowerCase().contains(filter.toLowerCase()))
                    filtered.add(el);
            }
        
            // DO NOT USE LIST.CLEAR() - will cleared the stored elements and force a reload of the items.
            ((DcListModel) list.getModel()).clear();
            list.addElements(filtered);
        }
    }  
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}
    
    private class RelatedItemsPopupMenu extends DcPopupMenu  implements ActionListener {
        
        public RelatedItemsPopupMenu() {

            JMenuItem menuOpen = new JMenuItem(DcResources.getText("lblOpenItem", ""), IconLibrary._icoOpen);
            JMenuItem menuEdit = new JMenuItem(DcResources.getText("lblEditItem", ""), IconLibrary._icoOpen);
            JMenuItem menuRemoveRef = new JMenuItem(DcResources.getText("lblRemoveAsReference", ""), IconLibrary._icoDelete);
            
            menuOpen.addActionListener(this);
            menuOpen.setActionCommand("open");
            
            menuEdit.addActionListener(this);
            menuEdit.setActionCommand("edit");
            
            menuRemoveRef.addActionListener(this);
            menuRemoveRef.setActionCommand("removeRef");
            
            add(menuOpen);
            add(menuEdit);
            addSeparator();
            add(menuRemoveRef);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("open"))
                open(false);
            else if (e.getActionCommand().equals("edit"))
                open(true);
            else if (e.getActionCommand().equals("removeRef"))
                removeReferences();
        }
    }
    
    private class RemoveReferences extends Thread {
        
        private IClient client;
        private Collection<DcObject> items;
        private DcObject parent;
        
        public RemoveReferences(IClient client, DcObject parent, Collection<DcObject> items) {
            this.client = client;
            this.items = items;
            this.parent = parent;
        }

        @Override
        public void run() {

            try {
                client.notifyTaskStarted(items.size());
                
                Connector connector = DcConfig.getInstance().getConnector();
                
                int moduleIdx = parent.getModule().getIndex();
                for (DcObject dco : items) {
                    
                    if (client.isCancelled()) break;
                    
                    client.notifyProcessed();
                    
                    dco.markAsUnchanged();
                    DcObject removal;
                    
                    for (DcField field : dco.getFields()) {
                        removal = null;
                        
                        if (field.getReferenceIdx() != moduleIdx) continue;
                        
                        if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                            
                            dco.load(new int[] {DcObject._ID, field.getIndex()});
                            
                            Collection<DcObject> mappings = (Collection<DcObject>) dco.getValue(field.getIndex());
                            
                            if (mappings == null) continue;
                           
                            for (DcObject mapping : mappings) { // loop through mappings
                                if (mapping.getValue(DcMapping._B_REFERENCED_ID).equals(parent.getID())) {
                                    removal = mapping;
                                    break;
                                }
                            }
                           
                            if (removal != null) {
                                mappings.remove(removal);
                                dco.setChanged(field.getIndex(), true);
                                
                                try {
                                    connector.saveItem(dco);
                                } catch (ValidationException ve) {
                                    logger.error(ve, ve);
                                }
                            }
                        } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                            dco.setValue(field.getIndex(), null);
                            try {
                                connector.saveItem(dco);
                            } catch (ValidationException ve) {
                                logger.error(ve, ve);
                            }
                        }
                    }
                    
                                        
                    try {
                        sleep(300);
                    } catch (Exception ignore) {}
                }
            } finally {
                client.notifyTaskCompleted(true, null);
                items.clear();
            }            
        }
    }

    @Override
    public Collection<DcObject> getItems() {
        return null;
    }

    @Override
    public void setItems(List<DcObject> items) {
    }

    @Override
    public void hideDialogActions(boolean b) {}
    
    @Override
    public void applySettings() {}

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {
        load();
    }

    @Override
    public void notifyTaskStarted(int taskSize) {
        cancelled = false;
        initProgressBar(taskSize);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
