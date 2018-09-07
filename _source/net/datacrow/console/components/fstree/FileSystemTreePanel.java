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

package net.datacrow.console.components.fstree;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcTree;
import net.datacrow.console.windows.DirectoriesAsDrivesDialog;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.drivemanager.Drive;
import net.datacrow.core.drivemanager.Drives;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.filefilters.FileNameFilter;

public abstract class FileSystemTreePanel extends JPanel implements ActionListener {
    
    private Map<Drive, JScrollPane> scrollers = new HashMap<Drive, JScrollPane>();
    private Map<Drive, FileSystemTreeModel> models = new HashMap<Drive, FileSystemTreeModel>();
    private FileNameFilter filter;
    
    public FileSystemTreePanel(FileNameFilter filter) {
        setFilter(filter);
    }
    
    protected void setFilter(FileNameFilter filter) {
        if (scrollers.size() > 0) {
            scrollers.clear();
            models.clear();
            removeAll();
            revalidate();
            repaint();
        }
        
        this.filter = filter;
        build();
    }
    
    private void setSelectedDrive(Drive drv) {
        for (JScrollPane scroller : scrollers.values())
            scroller.setVisible(false);

        scrollers.get(drv).setVisible(true);
        
        revalidate();
        repaint();
    }
    
    protected abstract JMenuBar getMenu();
    
    public Collection<String> getFiles(boolean includeDirs) {
        Collection<String> selected = new ArrayList<String>();
        FileSystemTreeNode parent;
        for (FileSystemTreeModel model : models.values()) {
            parent = (FileSystemTreeNode) model.getRoot();
            addSelectedNodes(parent, selected);
        }
        
        // cleanup (remove selected directories where files have been selected from)
        File file;
        String path;
        for (String s : new ArrayList<String>(selected)) {
            file = new File(s);
            path = file.getParent();
            if (!includeDirs && file.isDirectory())
                selected.remove(s);
            if (includeDirs && file.isFile() && selected.contains(path))
                selected.remove(path);
        }
        
        return selected;
    }

    public void addSelectedNodes(FileSystemTreeNode node, Collection<String> selected) {
        FileSystemTreeNode child;
        for (Enumeration e = node.children(); e.hasMoreElements(); ) {
            child = (FileSystemTreeNode) e.nextElement();
            if (child.isSelected())
                selected.add(child.getText());
            addSelectedNodes(child, selected);
        }
    }
    
    private void addDrive() {
        DirectoriesAsDrivesDialog dlg = new DirectoriesAsDrivesDialog();
        dlg.setVisible(true);
        if (dlg.isSuccess()) {
            scrollers.clear();
            models.clear();

            removeAll();
            build();
            revalidate();
            repaint();
        }
    }
    
    protected void build() {
        setLayout(Layout.getGBL());
        
        JMenuBar menu = getMenu();
        if (menu != null) {
            add(menu, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                     ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                      new Insets(0, 0, 0, 0), 0, 0));
        }
        
        JPanel pnlDrives = new JPanel();
        pnlDrives.setLayout(Layout.getGBL());
        
        JComboBox cbDrives = ComponentFactory.getComboBox();
        
        JButton btAddDrive = ComponentFactory.getIconButton(IconLibrary._icoAdd);
        btAddDrive.setToolTipText(DcResources.getText("tpAddDrive"));
        btAddDrive.setActionCommand("addDrive");
        btAddDrive.addActionListener(this);

        pnlDrives.add(cbDrives, Layout.getGBC( 0, 1, 1, 1, 30.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(15, 5, 5, 5), 0, 0));    
        pnlDrives.add(btAddDrive, Layout.getGBC( 1, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets(15, 5, 5, 5), 0, 0));  
        
        add(pnlDrives, Layout.getGBC( 0, 1, 3, 1, 20.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));  
        
        DcTree tree;
        for (Drive drive : new Drives().getDrives()) {
            tree = new DcTree(new FileSystemTreeModel(drive.getPath(), filter));
            
            tree.setCellRenderer(new FileSystemTreeNodeRenderer());
            tree.setCellEditor(new FileSystemTreeNodeEditor());
            tree.setEditable(true);
            
            JScrollPane scroller = new JScrollPane(tree);
            scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            add(scroller, Layout.getGBC( 0, 2, 1, 1, 20.0, 20.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                         new Insets(5, 5, 5, 5), 0, 0));      
            
            scroller.setVisible(false);
            scrollers.put(drive, scroller);
            models.put(drive, (FileSystemTreeModel) tree.getModel());
            cbDrives.addItem(drive);  
        }
        
        cbDrives.setActionCommand("drvChanged");
        cbDrives.addActionListener(this);
        cbDrives.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("drvChanged")) {
            JComboBox cb = (JComboBox) e.getSource();
            setSelectedDrive((Drive) cb.getSelectedItem());
        } else if (e.getActionCommand().equals("addDrive")) {
            addDrive();
        }
    }
    
    public void clear() {
        scrollers.clear();
        scrollers = null;
        models.clear();
        models = null;
    }
}
