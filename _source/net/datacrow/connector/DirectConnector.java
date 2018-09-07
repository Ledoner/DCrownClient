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

package net.datacrow.connector;

import net.datacrow.console.GUI;
import net.datacrow.console.windows.security.LoginDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.IView;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.data.DcIconCache;
import net.datacrow.core.drivemanager.DriveManager;
import net.datacrow.core.enhancers.ValueEnhancers;
import net.datacrow.core.filerenamer.FilePatterns;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.upgrade.ModuleUpgrade;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.SystemMonitor;
import net.datacrow.server.LocalServerConnector;
import net.datacrow.server.db.DatabaseInvalidException;
import net.datacrow.server.db.DatabaseManager;
import net.datacrow.server.security.SecurityCenter;
import net.datacrow.settings.DcSettings;
import net.datacrow.tabs.Tabs;
import net.datacrow.util.PollerTask;

import org.apache.log4j.Logger;

public class DirectConnector extends LocalServerConnector {
	
	private static Logger logger = Logger.getLogger(DirectConnector.class.getName());

	public DirectConnector() {
		super();
	}
	
    @Override
	public String getUsername() {
		return super.getUsername() == null ? "SA" : super.getUsername();
	}
    
    @Override
    public boolean displayQuestion(String msg) {
        return GUI.getInstance().displayQuestion(msg);
    }
    
    @Override
    public void displayError(String msg) {
    	GUI.getInstance().displayErrorMessage(msg);
    }
    
    @Override
    public void notifyDatabaseFailure(String msg) {
        GUI.getInstance().displayErrorMessage(
                "There is a problem with the database and/or upgrade: " + msg + 
                "Data Crow will now exit. Start Data Crow again and retry. If the problem persist, " +
                "select a new user folder on startup and restore the latest backup after " +
                "startup.");

        DcSettings.set(DcRepository.Settings.stDoNotAskAgainChangeUserDir, Boolean.FALSE);
        DcSettings.save();
        
        System.exit(0);
    }
    
    @Override
    public void displayMessage(String msg) {
    	GUI.getInstance().displayMessage(msg);
    }
    
    @Override
    public void applySettings() {
        GUI.getInstance().getMainFrame().applySettings(false);
    }
	
	@Override
	public void initialize() {
	    try {	        
	        new ModuleUpgrade().upgrade();
			DcModules.load();

			DcSettings.initialize();
			
            DcSettings.set(DcRepository.Settings.stConnectionString, "dc");
            String db = DcConfig.getInstance().getDatabaseName();
            if (!CoreUtilities.isEmpty(db))
                DcSettings.set(DcRepository.Settings.stConnectionString, db);

			try {
			    DatabaseManager.getInstance().doDatabaseHealthCheck();
			} catch (DatabaseInvalidException die) {
			    notifyDatabaseFailure(die.getMessage());
			}
			
			SecurityCenter.getInstance().initialize();
	        login(getUsername(), getPassword());

	        
	        
	        DatabaseManager.getInstance().initialize();
	        
	        try {
	            DcModules.loadDefaultModuleData();
	        } catch (Throwable t) {
	            logger.error("Default data could not be loaded", t);
	        }
	        
	        DataFilters.load();
	        FilePatterns.load();
	        ValueEnhancers.initialize();
	        
	        SystemMonitor monitor = new SystemMonitor();
	        monitor.start();
	        
	    } catch (Exception e) {
	        logger.error(e, e);
	    }
	}
	
	@Override
	public PollerTask getPollerTask(Thread thread, String title) {
	    return new PollerTask(thread, title);
	}
	
    @Override
    public SecuredUser login(String user, String pw) {
        String username = getUsername();
        String password = pw;
        
        SecuredUser su = null;
        
        // SA is the default, so ignore this for this routine (user left the user name empty)
        if ("SA".equals(username) && password == null)
            username = null;
        
        if (username == null) {
            su = super.login("SA", "");
            if (su == null) {
                boolean success = false;
                int retry = 0;
                
                GUI.getInstance().showSplashScreen(false);
                while (!success && retry < 3) {
                    LoginDialog dlg = new LoginDialog();
                    GUI.getInstance().openDialogNativeModal(dlg);
                    if (dlg.isCanceled()) break;
                    
                    su = super.login(dlg.getLoginName(), dlg.getPassword());
                    
                    success = su != null;
                }
                
                if (!success) 
                    System.exit(0);
                else
                    GUI.getInstance().showSplashScreen(true);
            }
        } else {
            // use the blunt message
            su = super.login(username, password);
        }
        
        return su;
    }

    @Override
    public void shutdown(boolean saveChanges) {
        DcConfig dcc = DcConfig.getInstance();
        dcc.getClientSettings().save();

        DriveManager.getInstance().stopScanners();
        DriveManager.getInstance().stopDrivePoller();
        DriveManager.getInstance().stopFileSynchronizer();
        
        logger.info(DcResources.getText("msgApplicationStops"));
        
        if (saveChanges) {
            boolean unsavedChanges = false;
            for (IMasterView mv : GUI.getInstance().getViews()) {
                mv.saveSettings();
                for (IView view : mv.getViews()) {
                    if (!view.isChangesSaved())
                        unsavedChanges = true;
                }
            }
            
            if (unsavedChanges && !GUI.getInstance().displayQuestion("msgCancelExitAndSave"))
                return;
        }
        
        Tabs.getInstance().save();
        
        DcIconCache.getInstance().deleteIcons();
        
        if (saveChanges) {
            DataFilters.save();
            FilePatterns.save();
            DcSettings.save();
            DcModules.save();
        }
        
        DatabaseManager.getInstance().closeDatabases(false);
        
        if (GUI.getInstance().getMainFrame() != null)
            GUI.getInstance().getMainFrame().setVisible(false);

        dcc.getConnector().close();
        
        System.exit(0);
    }
}
