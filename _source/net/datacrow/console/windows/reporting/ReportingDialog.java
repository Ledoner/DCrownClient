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

package net.datacrow.console.windows.reporting;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcFileField;
import net.datacrow.console.windows.DcFrame;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.clients.IItemExporterClient;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.reporting.Report;
import net.datacrow.core.reporting.ReportGenerator;
import net.datacrow.core.reporting.ReportType;
import net.datacrow.core.reporting.Reports;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.launcher.FileLauncher;

import org.apache.log4j.Logger;

/**
 * @author Robert Jan van der Waals
 */
public class ReportingDialog extends DcFrame implements IItemExporterClient, ActionListener {

    private static Logger logger = Logger.getLogger(ReportingDialog.class.getName());
    
    private JButton buttonRun = ComponentFactory.getButton(DcResources.getText("lblRun"));
    private JButton buttonStop = ComponentFactory.getButton(DcResources.getText("lblStop"));
    private JButton buttonClose = ComponentFactory.getButton(DcResources.getText("lblClose"));
    private JButton buttonResults = ComponentFactory.getButton(DcResources.getText("lblOpenReport"));

    private JComboBox cbReports = ComponentFactory.getComboBox();
    private JComboBox cbReportType = ComponentFactory.getComboBox();

    private JTextArea textLog = ComponentFactory.getTextArea();
    private JProgressBar progressBar = new JProgressBar();
    private DcFileField fileField;
    
    private boolean canceled = false;
    private List<String> items;

    public ReportingDialog(List<String> items) {
        super(DcResources.getText("lblCreateReport"), IconLibrary._icoReport);

        try {
            this.items = items;
            
            setHelpIndex("dc.reports");
    
            fileField = ComponentFactory.getFileField(false, false, null);
            buildDialog();

        } catch (Exception exp) {
            logger.error(DcResources.getText("msgFailedToOpen", exp.getMessage()), exp);
            GUI.getInstance().displayErrorMessage(DcResources.getText("msgFailedToOpen", exp.getMessage()));
        }
    }

    private void saveDefaults() {
        DcSettings.set(DcRepository.Settings.stReportingDialogSize, getSize());
        DcSettings.set(DcRepository.Settings.stReportFile, fileField.getFilename());
    }
    
    @Override
    public void notify(String message) {
        if (textLog != null) { 
            textLog.insert(message + '\n', 0);
            textLog.setCaretPosition(0);
        }
    }

    @Override
    public void notifyProcessed() {
        if (progressBar!= null) progressBar.setValue(progressBar.getValue() + 1);
    }

    public void allowActions(boolean b) {
        if (buttonRun != null) {
            buttonRun.setEnabled(b);
            buttonResults.setEnabled(b);
        }
    }

    private void showResults() {
    	File file = fileField.getFile();
        if (file != null && file.exists()) {
            try {
            	FileLauncher launcher = new FileLauncher(file);
            	launcher.launch();
            } catch (Exception e) {
                String msg = DcResources.getText("msgErrorWhileOpeningX", new String[] {file.toString(), e.getMessage()});
                GUI.getInstance().displayWarningMessage(msg);
                logger.error(msg, e);
            }
        } else {
            GUI.getInstance().displayWarningMessage("msgCouldNotOpenReport");
        }
    }

    private File getTarget(String extension) throws FileNotFoundException {
        File target = fileField.getFile();
        
        if (target == null)
            throw new FileNotFoundException();
        
        String filename = target.toString();
        if (!filename.toLowerCase().endsWith(extension.toLowerCase())) {
            
            if (filename.lastIndexOf(".") > 0) 
                filename = filename.substring(0, filename.lastIndexOf("."));
            
            target = new File(filename + "." + extension);
            fileField.setFile(target);
        }
        
        return target;
    }
    
    private void createReport() {
        try {
            ReportType reportType = (ReportType) cbReportType.getSelectedItem();
            
            Report report = (Report) cbReports.getSelectedItem();
            File target = getTarget(reportType.getExtension());
            
            if (target.exists()) {
                target.delete();
                if (target.exists()) {
                    GUI.getInstance().displayWarningMessage("msgFileIsInUse");
                    return;
                }
            }
            
            ReportGenerator rg = new ReportGenerator(this, items, target, report, reportType);
            rg.start();
            
            allowActions(false);
            
        } catch (FileNotFoundException fnfe) {
            GUI.getInstance().displayWarningMessage("msgSelectTargetFile");
        } catch (Throwable t) {
            GUI.getInstance().displayWarningMessage(DcResources.getText("msgCreateReportFailedUnexpect", t.toString()));
        } 
    }
    
    private void cancel() {
        canceled = true;
        allowActions(true);
    }

    @Override
    public void close() {

        saveDefaults();
        
        cancel();

        if (items != null) { 
            items.clear();
            items = null;
        }
        
        buttonRun = null;
        buttonStop = null;
        buttonClose = null;
        buttonResults = null;
        cbReports = null;
        cbReportType = null;
        textLog = null;
        progressBar = null;
        fileField = null;
        
        super.close();
    }
    
    private void buildDialog() {
        //**********************************************************
        //Input Panel
        //**********************************************************
        JPanel panelInput = new JPanel();
        panelInput.setLayout(Layout.getGBL());
        panelInput.add(fileField,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                      ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                       new Insets( 5, 5, 5, 5), 0, 0));
        
        fileField.setValue(DcSettings.getString(DcRepository.Settings.stReportFile));
        
        panelInput.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblTargetFile")));        
        
        //**********************************************************
        //Report Type
        //**********************************************************
        JPanel panelReport = new JPanel(false);
        panelReport.setLayout(Layout.getGBL());

        for (ReportType rt : ReportType.values())
            cbReportType.addItem(rt);

        Reports reports = new Reports();
        for (Report report : reports.getReports(DcModules.getCurrent().getIndex()))
            cbReports.addItem(report);
        
        JLabel lblReportFormat = ComponentFactory.getLabel(DcResources.getText("lblReportFormat"));
        JLabel lblReports = ComponentFactory.getLabel(DcResources.getText("lblReport"));
        
        panelReport.add(lblReports, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelReport.add(cbReports,  Layout.getGBC( 1, 0, 1, 1, 50.0, 50.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelReport.add(lblReportFormat, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelReport.add(cbReportType,    Layout.getGBC( 1, 1, 1, 1, 50.0, 50.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                 new Insets( 5, 5, 5, 5), 0, 0));

        
        panelReport.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblReportSelection")));

    	//**********************************************************
        //Actions Panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        panelActions.setLayout(Layout.getGBL());

        buttonRun.setActionCommand("createReport");
        buttonRun.addActionListener(this);
        buttonStop.setActionCommand("cancel");
        buttonStop.addActionListener(this);
        buttonResults.setActionCommand("showResults");
        buttonResults.addActionListener(this);
        buttonClose.setActionCommand("close");
        buttonClose.addActionListener(this);

        buttonResults.setMnemonic(KeyEvent.VK_O);

        panelActions.add(buttonRun,  Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelActions.add(buttonStop,    Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelActions.add(buttonResults,   Layout.getGBC( 2, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));
        panelActions.add(buttonClose,     Layout.getGBC( 4, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 5), 0, 0));

        //**********************************************************
        //Progress panel
        //**********************************************************
        JPanel panelProgress = new JPanel();
        panelProgress.setLayout(Layout.getGBL());
        panelProgress.add(progressBar, Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
                         ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                          new Insets(5, 5, 5, 5), 0, 0));

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
        //Main Panel
        //**********************************************************
        this.getContentPane().setLayout(Layout.getGBL());
        
        
        this.getContentPane().add(
                panelReport,    Layout.getGBC( 0, 0, 1, 1, 20.0, 20.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(
                panelInput,     Layout.getGBC( 0, 1, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets( 5, 5, 5, 5), 0, 0));
        this.getContentPane().add(
                panelActions,   Layout.getGBC( 0, 3, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets( 0, 5, 0, 5), 0, 0));
        this.getContentPane().add(
                panelProgress,  Layout.getGBC( 0, 4, 1, 1, 1.0, 1.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets( 0, 5, 0, 5), 0, 0));
        this.getContentPane().add(
                panelLog,       Layout.getGBC( 0, 5, 1, 1, 40.0, 40.0
               ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets( 0, 5, 0, 5), 0, 0));
        
        this.setResizable(true);
        this.pack();
        
        Dimension size = DcSettings.getDimension(DcRepository.Settings.stReportingDialogSize);
        setSize(size);

        setCenteredLocation();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("showResults"))
            showResults();
        else if (ae.getActionCommand().equals("close"))
            close();
        else if (ae.getActionCommand().equals("cancel"))
            cancel();
        else if (ae.getActionCommand().equals("createReport"))
            createReport();      
    }

    @Override
    public void notifyWarning(String msg) {
        notify(msg);
        logger.warn(msg);
    }

    @Override
    public void notifyError(Throwable e) {
        notify(e.getMessage());
        logger.error(e, e);
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {
        allowActions(true);
    }

    @Override
    public void notifyTaskStarted(int taskSize) {
        progressBar.setValue(0);
        progressBar.setMaximum(taskSize);
        allowActions(false);
    }
    

    @Override
    public boolean isCancelled() {
        return canceled;
    } 
}
