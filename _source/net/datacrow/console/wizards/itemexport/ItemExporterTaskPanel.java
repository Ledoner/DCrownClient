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

package net.datacrow.console.wizards.itemexport;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import net.datacrow.console.Layout;
import net.datacrow.console.components.panels.TaskPanel;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.clients.IItemExporterClient;
import net.datacrow.core.migration.itemexport.ItemExporter;
import net.datacrow.core.resources.DcResources;

import org.apache.log4j.Logger;

public class ItemExporterTaskPanel extends ItemExporterWizardPanel implements IItemExporterClient  {

	private static Logger logger = Logger.getLogger(ItemExporterTaskPanel.class.getName());
	
	private TaskPanel tp = new TaskPanel(TaskPanel._SINGLE_PROGRESSBAR);
	
    private ItemExporter exporter;
    
    public ItemExporterTaskPanel(ItemExporterWizard wizard) {
        super(wizard);
        build();
    }
    
    @Override
    public Object apply() throws WizardException {
        return wizard.getDefinition();
    }

	@Override
    public void destroy() {
    	if (exporter != null) exporter.cancel();
    	exporter = null;
    	if (tp != null) tp.destroy();
    	tp = null;
    	wizard = null;
    }

    @Override
    public String getHelpText() {
        return DcResources.getText("msgExportProcess");
    }
    
    @Override
    public void onActivation() {
    	if (definition != null && definition.getExporter() != null) {
    		this.exporter = wizard.getDefinition().getExporter();
    		start();
    	}
	}

    @Override
	public void onDeactivation() {
		cancel();
	}

    private void start() {
        exporter.setClient(this);
    	try { 
	        exporter.setFile(wizard.getDefinition().getFile());
    	    exporter.setSettings(definition.getSettings());
    	    exporter.setItems(wizard.getItems());
    	    exporter.setFields(definition.getFields());
    	    exporter.start();
    	} catch (Exception e ) {
    	    notify(e.getMessage());
    	    logger.error(e, e);
    	}
    }
    
    private void build() {
        setLayout(Layout.getGBL());
		add(tp,  Layout.getGBC( 0, 01, 1, 1, 1.0, 1.0
				,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				 new Insets( 5, 5, 5, 5), 0, 0));
    }
    
    private void cancel() {
        if (exporter != null) exporter.cancel();
        notifyTaskCompleted(false, "");
    }    
    
    @Override
    public void notify(String message) {
        if (tp != null) tp.addMessage(message);
    }

    @Override
    public void notifyTaskStarted(int count) {
        if (tp == null) return; 
        tp.clear();
        tp.initializeTask(count);
    }

    @Override
    public void notifyProcessed() {
        if (tp != null) tp.updateProgressTask();       
    }

    @Override
    public void notifyWarning(String msg) {
        if (tp != null) tp.addMessage(msg);
    }

    @Override
    public void notifyError(Throwable e) {
        if (tp != null) tp.addMessage(e.getMessage());
    }

    @Override
    public void notifyTaskCompleted(boolean success, String taskID) {}

    @Override
    public boolean isCancelled() {
        return false;
    }
}
