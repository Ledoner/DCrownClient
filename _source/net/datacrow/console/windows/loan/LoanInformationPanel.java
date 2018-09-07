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

package net.datacrow.console.windows.loan;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.datacrow.console.Layout;
import net.datacrow.console.components.DcPanel;
import net.datacrow.console.components.DcProgressBar;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.DcConfig;
import net.datacrow.core.console.ISimpleItemView;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Loan;
import net.datacrow.core.objects.helpers.Item;
import net.datacrow.core.objects.helpers.Media;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class LoanInformationPanel extends DcPanel implements ISimpleItemView, MouseListener {
	
    private static Logger logger = Logger.getLogger(LoanInformationPanel.class.getName());
    
    private DcTable table = new DcTable(DcModules.get(DcModules._ITEM), true, false);
    private DcObject person;
    
    private DcProgressBar pb = new DcProgressBar();
    
    private LoanFilter filter;
    
    public LoanInformationPanel() {
    	this(null);
    }
    
    public LoanInformationPanel(DcObject person) {
        super(DcResources.getText("lblLendItems"));
        this.person = person;
        build();
    }
    
    public List<DcObject> getSelectedItems() {
        List<DcObject> items = new ArrayList<DcObject>();
        for (int row : table.getSelectedIndices()) {
            items.add(table.getItemAt(row));
        }
        return items;
    }
    
    @Override
    public void setParentID(String ID) {}

    @Override
    public String getParentID() {
        return null;
    }

    public void setFilter(LoanFilter filter) {
        this.filter = filter;
    }
    
    public void setProcessed(int i) {
        pb.setValue(i);
    }
    
    public void setMaximum(int max) {
        pb.setMinimum(0);
        pb.setMaximum(max);
        pb.setValue(0);
    }
    
    public void cancel() {
        if (filter != null && filter.isAlive())
            filter.cancel();
    }
    
    @Override
    public void load() {
        table.clear();
        
        if (filter != null && filter.isAlive())
            filter.cancel();
        
        if (filter == null || filter.isAlive()) {
            filter = new LoanFilter();
            if (person != null) filter.setPerson(person);
        }
        
        try {
            filter.setListener(this);
            filter.start();    
        } catch (Exception exp) {
            filter = new LoanFilter();
            
            if (person != null) filter.setPerson(person);
            
            filter.setListener(this);
            filter.start();
        }
    }
    
    protected void addItem(DcObject dco) {
        table.add(dco);
    }
    
    @Override
    public void clear() {
        if (table != null)
            table.clear();
        
        person = null;
        table = null;
        
        if (filter != null)
            filter.cancel();
        
        super.clear();
    }
    
    private void build() {
        table.activate();
        table.addMouseListener(this);
        table.setDynamicLoading(false);

        JScrollPane sp = new JScrollPane(table);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        setLayout(Layout.getGBL());
        
        JPanel panelProgress = new JPanel();
        panelProgress.setLayout(Layout.getGBL());
        panelProgress.add(pb, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
        
        add(sp,  Layout.getGBC( 0, 0, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(0, 0, 0, 0), 0, 0));
        add(panelProgress,  Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(0, 0, 0, 0), 0, 0));        
        
        if (person == null)
            table.setVisibleColumns(new int[] {Item._SYS_MODULE, Item._SYS_DISPLAYVALUE, Item._SYS_LENDBY, Item._SYS_LOANSTARTDATE, Item._SYS_LOANENDDATE, Item._SYS_LOANDUEDATE, Item._SYS_LOANSTATUS, Item._SYS_LOANSTATUSDAYS});
        else 
            table.setVisibleColumns(new int[] {Item._SYS_MODULE, Item._SYS_DISPLAYVALUE, Item._SYS_LOANSTARTDATE, Item._SYS_LOANENDDATE, Item._SYS_LOANDUEDATE, Item._SYS_LOANSTATUS, Item._SYS_LOANSTATUSDAYS});
    }
    
    private void openDefault() {
        
        DcObject dco = table.getSelectedItem();
        
        if (dco == null) return;
        
        Connector connector = DcConfig.getInstance().getConnector();
        Loan loan = connector.getCurrentLoan(dco.getID());
        if (loan == null || loan.isAvailable(dco.getID())) {
            return;
        } else {
            Collection<DcObject> items = new ArrayList<DcObject>();
            items.add(dco);
            
            try {
                LoanForm form = new LoanForm(items);
                form.setVisible(true);
            } catch (Exception exp) {
                logger.warn(exp, exp);
            }
        }
    }
    
    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        DcObject dco = table.getSelectedItem();
        
        if (dco == null) return;
        
        Connector connector = DcConfig.getInstance().getConnector();
        if (!connector.getUser().isAuthorized(dco.getModule())) {
            e.consume();
            return;
        }
        
        if (SwingUtilities.isRightMouseButton(e)) {
            // Only change the selection for single item selections
            // or when nothing has been selected as yet.
            if (table.getSelectedIndices() == null ||
                table.getSelectedIndices().length == 1) {
                int index = table.locationToIndex(e.getPoint());
                table.setSelected(index);
            }

            if (table.getSelectedIndex() > -1) {
                int col = table.getColumnIndexForField(Media._SYS_MODULE);
                
                Object value = table.getValueAt(table.getSelectedIndex(), col, true);
                DcModule module = (DcModule) value;
                
                dco = connector.getItem(module.getIndex(), dco.getID());
                
                LoanInformationPanelPopupMenu menu = new LoanInformationPanelPopupMenu(dco, getSelectedItems()); 
                menu.setInvoker(table);
                menu.show(table, e.getX(), e.getY());
            }
        } else if (e.getClickCount() == 2) {
            openDefault();
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

    @Override
    public Collection<DcObject> getItems() {
        return null;
    }
    
    @Override
    public void applySettings() {}

    @Override
    public void setItems(List<DcObject> items) {}

    @Override
    public void hideDialogActions(boolean b) {}
}
