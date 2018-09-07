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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFileField;
import net.datacrow.core.DcRepository;
import net.datacrow.core.clients.IClient;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.filefilters.DcFileFilter;
import net.datacrow.core.utilities.ical.ICalendarExporter;
import net.datacrow.settings.DcSettings;

public class ICalendarExporterDialog extends DcDialog implements ActionListener, IClient {
    
    private DcFileField ffTarget = ComponentFactory.getFileField(true, false);
    private JButton btExport = ComponentFactory.getButton(DcResources.getText("lblExport"));
    private JButton btCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
    
    private JCheckBox cbFullExport = ComponentFactory.getCheckBox(DcResources.getText("lblFullExport"));
    
    private JTextArea textLog = ComponentFactory.getTextArea();
    private JProgressBar progressBar = new JProgressBar();
    
    private boolean stopped = false;

    public ICalendarExporterDialog() {
        super(GUI.getInstance().getMainFrame());
        setTitle(DcResources.getText("lblICalendarLoanExport"));
        setHelpIndex("dc.tools.icalendar_export");

        buildDialog();
        
        cbFullExport.setSelected(DcSettings.getBoolean(DcRepository.Settings.stICalendarFullExport));
    }
    
    private void export() {
        
        File target = ffTarget.getFile();
        
        if (target == null) {
            GUI.getInstance().displayMessage("msgSelectFileFirst");
        } else {
            ICalendarExporter exporter = new ICalendarExporter(this, target, cbFullExport.isSelected());
            exporter.start();
        }
    }

    @Override
    public void notifyProcessed() {
        
        if (stopped) return;
        
        int value = progressBar.getValue();
        
        if (value >= 10) 
            value = 0;
        
        progressBar.setValue(value + 1);
    }
    
    @Override
    public void notify(String msg) {
        
        if (stopped) return;
        
        textLog.insert(msg + '\n', 0);
        textLog.setCaretPosition(0);
    }
    
    @Override
    public void notifyError(Throwable t) {
        notify(t.getMessage());
    }

    @Override
    public void notifyWarning(String msg) {
        notify(msg);
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {
        setActionsEnabled(true);
        progressBar.setValue(10);
    }

    @Override
    public void notifyTaskStarted(int size) {
        setActionsEnabled(false);
        
        progressBar.setValue(0);        
        progressBar.setMaximum(10);
    }

    @Override
    public boolean isCancelled() {
        return stopped;
    }
    
    private void setActionsEnabled(boolean b) {
        btExport.setEnabled(b);
        ffTarget.setEnabled(b);
        cbFullExport.setEnabled(b);
    }

    private void buildDialog() {
        getContentPane().setLayout(Layout.getGBL());
        
        ffTarget.setFileFilter(new DcFileFilter("ics"));

        btCancel.setActionCommand("cancel");
        btExport.setActionCommand("export");
        
        btCancel.addActionListener(this);
        btExport.addActionListener(this);
        
        JPanel panelActions = new JPanel();
        panelActions.add(btExport);
        panelActions.add(btCancel);
        
        //**********************************************************
        //Create Backup Panel
        //**********************************************************
        JPanel panelExport = new JPanel();

        panelExport.setLayout(Layout.getGBL());
        
        panelExport.add(ComponentFactory.getLabel(DcResources.getText("lblTargetFile")), 
                Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
                new Insets( 0, 5, 5, 5), 0, 0));
        panelExport.add(ffTarget, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));
        panelExport.add(cbFullExport, Layout.getGBC( 0, 1, 2, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 0, 5, 5, 5), 0, 0));

        //**********************************************************
        //Log Panel
        //**********************************************************
        JPanel panelLog = new JPanel();
        panelLog.setLayout(Layout.getGBL());

        JScrollPane scroller = new JScrollPane(textLog);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelLog.add(scroller, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(5, 5, 5, 5), 0, 0));

        panelLog.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblLog")));

        //**********************************************************
        //Progress panel
        //**********************************************************
        JPanel panelProgress = new JPanel();
        panelProgress.setLayout(Layout.getGBL());
        panelProgress.add(progressBar, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));

        //**********************************************************
        //Main panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());

        this.getContentPane().add(panelExport,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelLog,     Layout.getGBC( 0, 1, 1, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelProgress,Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(panelActions, Layout.getGBC( 0, 3, 1, 1, 0.0, 0.0
                ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 10), 0, 0));
        
        pack();

        Dimension size = DcSettings.getDimension(DcRepository.Settings.stICalendarExportDialogSize);
        setSize(size);

        setCenteredLocation();
    }

    @Override
    public void close() {
        
        stopped = true;
        
        DcSettings.set(DcRepository.Settings.stICalendarExportDialogSize, getSize());
        DcSettings.set(DcRepository.Settings.stICalendarFullExport, cbFullExport.isSelected());
        
        ffTarget = null;
        textLog = null;
        progressBar = null;
        
        super.close();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("export"))
            export();
        else if (ae.getActionCommand().equals("cancel"))
            close();
    }
}