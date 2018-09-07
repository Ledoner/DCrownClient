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

package net.datacrow.console.windows.filerenamer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcLongTextField;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.console.menu.DcFileRenamerPreviewPopupMenu;
import net.datacrow.console.menu.FileRenamerMenu;
import net.datacrow.console.windows.DcDialog;
import net.datacrow.core.DcRepository;
import net.datacrow.core.filerenamer.FilePattern;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;

public class FileRenamerPreviewDialog extends DcDialog implements ActionListener, MouseListener {

    private DcTable table = ComponentFactory.getDCTable(true, false);
    private JProgressBar progressBar = new JProgressBar();
    private JButton buttonStart = ComponentFactory.getButton(DcResources.getText("lblStart"));
    private JButton buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));

    private PreviewGenerator generator;
    
    private FileRenamerDialog parent;
    
    private boolean affirmative = false;
    
    public FileRenamerPreviewDialog(FileRenamerDialog parent, 
                                    Collection<DcObject> objects, 
                                    FilePattern pattern,
                                    File baseDir) {
        super(parent);
        
        this.parent = parent;
        
        JMenu menu = new FileRenamerMenu(this);
        JMenuBar menuBar = ComponentFactory.getMenuBar();
        menuBar.add(menu);
        setJMenuBar(menuBar);        
        
        setHelpIndex("dc.tools.filerenamer");
        setTitle(DcResources.getText("lblFileRenamePreview"));
        
        build(pattern.getModule());
        generatePreview(objects, pattern, baseDir);

        setSize(DcSettings.getDimension(DcRepository.Settings.stFileRenamerPreviewDialogSize));
        setCenteredLocation();
        setModal(true);
    }
    
    private void generatePreview(Collection<DcObject> items, FilePattern pattern, File baseDir) {
        if (generator != null) 
            generator.cancel();
        
        generator = new PreviewGenerator(this, items, pattern, baseDir);
        generator.start();
    }
    
    protected void addPreviewResult(DcObject dco, String oldFilename, String newFilename) {
        table.addRow(new Object[] {dco, oldFilename, newFilename});
    }
    
    protected void setBusy(boolean b) {
        buttonStart.setEnabled(!b);
    }

    protected void initProgressBar(int max) {
        progressBar.setValue(0);
        progressBar.setMaximum(max);
    }
    
    protected void updateProgressBar() {
        progressBar.setValue(progressBar.getValue() + 1);
    }
    
    public boolean isAffirmative() {
        return affirmative;
    }
    
    public Collection<DcObject> getObjects() {
    	Collection<DcObject> items = new ArrayList<DcObject>();
        for (int row = 0; row < table.getRowCount(); row++) {
        	items.add((DcObject) table.getValueAt(row, 0));
        }
        return items;
    }
    
    public void clear() {
        DcSettings.set(DcRepository.Settings.stFileRenamerPreviewDialogSize, getSize());
        
        table.clear();
        table = null;
        
        if (generator != null)
            generator.cancel();
        
        generator = null;
        buttonCancel = null;
        buttonStart = null;
        progressBar = null;
        
        parent = null;
        
        super.close();
    }
    
    @Override
    public void close() {
        setVisible(false);
        parent.notifyJobStopped();
        
        if (generator != null)
        	generator.cancel();
    }

    private void build(int module) {
        
        //**********************************************************
        //Help panel
        //**********************************************************
        
        DcLongTextField textHelp = ComponentFactory.getLongTextField();
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(Layout.getGBL());

        textHelp.setBorder(null);
        JScrollPane scroller = new JScrollPane(textHelp);
        scroller.setBorder(null);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        textHelp.setEditable(false);
        textHelp.setText(DcResources.getText("msgFileRenamePreviewHelp"));
        textHelp.setMargin(new Insets(5,5,5,5));

        scroller.setPreferredSize(new Dimension(100, 60));
        scroller.setMinimumSize(new Dimension(100, 60));
        scroller.setMaximumSize(new Dimension(800, 60));
        
        helpPanel.add(scroller, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets(5, 5, 5, 5), 0, 0));
        
        
        //**********************************************************
        //Preview panel
        //**********************************************************
        JPanel panelPreview = new JPanel();
        
        table.setColumnCount(3);
        TableColumn tcDescription = table.getColumnModel().getColumn(0);
        JTextField textField = ComponentFactory.getTextFieldDisabled();
        tcDescription.setCellEditor(new DefaultCellEditor(textField));
        tcDescription.setHeaderValue(DcModules.get(module).getObjectName());

        TableColumn tcOldFilename = table.getColumnModel().getColumn(1);
        textField = ComponentFactory.getTextFieldDisabled();
        tcOldFilename.setCellEditor(new DefaultCellEditor(textField));
        tcOldFilename.setHeaderValue(DcResources.getText("lblOldFilename"));
        
        TableColumn tcNewFilename = table.getColumnModel().getColumn(2);
        textField = ComponentFactory.getTextFieldDisabled();
        tcNewFilename.setCellEditor(new DefaultCellEditor(textField));
        tcNewFilename.setHeaderValue(DcResources.getText("lblNewFilename"));
        
        table.addMouseListener(this);
        table.applyHeaders();
        
        panelPreview.setLayout(Layout.getGBL());
        panelPreview.add(new JScrollPane(table), Layout.getGBC( 0, 0, 5, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));

        
        // **********************************************************
        //Action panel
        //**********************************************************
        JPanel panelAction = new JPanel();
        
        buttonStart.setEnabled(false);
        
        buttonStart.addActionListener(this);
        buttonCancel.addActionListener(this);
        
        buttonStart.setActionCommand("confirm");
        buttonCancel.setActionCommand("cancel");
        
        panelAction.add(buttonStart);
        panelAction.add(buttonCancel);
        
        //**********************************************************
        //Main panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());

        this.getContentPane().add(helpPanel,    Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelPreview,  Layout.getGBC( 0, 1, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelAction,   Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(progressBar,   Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("confirm")) {
            affirmative = true;
            close();
        } else if (ae.getActionCommand().equals("remove")) {
            int[] rows = table.getSelectedRows();
            if (rows != null && rows.length > 0)
                table.remove(rows);
            else 
                GUI.getInstance().displayMessage("msgNoItemsSelectedToRemove");
        } else if (ae.getActionCommand().equals("cancel")) {
            close();
        }
    }
    
    private static class PreviewGenerator extends Thread {

        private Collection<DcObject> objects;
        private FilePattern pattern;
        private FileRenamerPreviewDialog dlg;
        private File baseDir;
        
        private boolean keepOnRunning = true;
        
        public PreviewGenerator(FileRenamerPreviewDialog dlg, 
                                Collection<DcObject> objects, 
                                FilePattern pattern, 
                                File baseDir) {
            this.dlg = dlg;
            this.objects = objects;
            this.pattern = pattern;
            this.baseDir = baseDir;
        }

        public void cancel() {
            keepOnRunning = false;
        }
        
        @Override
        public void run() {
            dlg.initProgressBar(objects.size());
            dlg.setBusy(true);
            
            for (DcObject dco : objects) {

                if (!keepOnRunning) break;
                
                if (dco.getFilename() != null) {
	                File oldFile = new File(dco.getFilename());
	                String newFilename = pattern.getFilename(dco, oldFile, baseDir);
	                
	                dlg.addPreviewResult(dco, oldFile.toString(), newFilename);
                }
                dlg.updateProgressBar();
            }

            dlg.setBusy(false);
            objects = null;
            pattern = null;
            dlg = null;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            
            DcFileRenamerPreviewPopupMenu popupmenu = new DcFileRenamerPreviewPopupMenu(this);
            popupmenu.validate();
            popupmenu.show(table, e.getX(), e.getY());
        }
    }
}
