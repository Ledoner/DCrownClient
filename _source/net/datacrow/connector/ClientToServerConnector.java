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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.datacrow.console.GUI;
import net.datacrow.console.windows.security.LoginDialog;
import net.datacrow.core.DcConfig;
import net.datacrow.core.console.IMasterView;
import net.datacrow.core.console.IView;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.data.DcIconCache;
import net.datacrow.core.data.DcResultSet;
import net.datacrow.core.data.Operator;
import net.datacrow.core.drivemanager.DriveManager;
import net.datacrow.core.enhancers.IValueEnhancer;
import net.datacrow.core.enhancers.ValueEnhancers;
import net.datacrow.core.filerenamer.FilePatterns;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcSimpleValue;
import net.datacrow.core.objects.Loan;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.objects.helpers.User;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.server.Connector;
import net.datacrow.core.server.DcServerConnection;
import net.datacrow.core.server.requests.ClientRequest;
import net.datacrow.core.server.requests.ClientRequestApplicationSettings;
import net.datacrow.core.server.requests.ClientRequestExecuteSQL;
import net.datacrow.core.server.requests.ClientRequestItem;
import net.datacrow.core.server.requests.ClientRequestItemAction;
import net.datacrow.core.server.requests.ClientRequestItemKeys;
import net.datacrow.core.server.requests.ClientRequestItems;
import net.datacrow.core.server.requests.ClientRequestLogin;
import net.datacrow.core.server.requests.ClientRequestModules;
import net.datacrow.core.server.requests.ClientRequestReferencingItems;
import net.datacrow.core.server.requests.ClientRequestSimpleValues;
import net.datacrow.core.server.requests.ClientRequestUser;
import net.datacrow.core.server.requests.ClientRequestValueEnhancers;
import net.datacrow.core.server.response.IServerResponse;
import net.datacrow.core.server.response.ServerActionResponse;
import net.datacrow.core.server.response.ServerApplicationSettingsRequestResponse;
import net.datacrow.core.server.response.ServerErrorResponse;
import net.datacrow.core.server.response.ServerItemKeysRequestResponse;
import net.datacrow.core.server.response.ServerItemRequestResponse;
import net.datacrow.core.server.response.ServerItemsRequestResponse;
import net.datacrow.core.server.response.ServerLoginResponse;
import net.datacrow.core.server.response.ServerModulesRequestResponse;
import net.datacrow.core.server.response.ServerSQLResponse;
import net.datacrow.core.server.response.ServerSimpleValuesResponse;
import net.datacrow.core.server.response.ServerValueEnhancersRequestResponse;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.utilities.SystemMonitor;
import net.datacrow.core.wf.tasks.DcTask;
import net.datacrow.settings.DcSettings;
import net.datacrow.settings.Settings;
import net.datacrow.tabs.Tabs;

public class ClientToServerConnector extends Connector {

	private static Logger logger = Logger.getLogger(ClientToServerConnector.class);
	
	private static final ClientToServerConnector si = new ClientToServerConnector();
	
	private Collection<DcServerConnection> connections = new ArrayList<DcServerConnection>();
	
	private SecuredUser su;
	
	public static ClientToServerConnector getInstance() {
		return si;
	}
	
	public ClientToServerConnector() {
		super();
	}
	
	@Override
	public DcServerConnection getServerConnection() throws Exception {
		
		cleanup();
	    
	    for (DcServerConnection connection : connections) {
	        if (connection.isActive() && connection.isAvailable())
	            return connection;
	    }
	    
	    DcServerConnection connection = new DcServerConnection(this);
	    connections.add(connection);
	    return connection;
	}
	
	@Override
	public void initialize() {
	    try {
            login(getUsername(), getPassword());
            
            DcModules.load();
                
            DataFilters.load();
            FilePatterns.load();
            ValueEnhancers.initialize();
            
            loadApplicationSettings();
            
            SystemMonitor monitor = new SystemMonitor();
            monitor.start();
            
        } catch (Exception e) {
            logger.error(e, e);
        }
	}
	
	/**
	 * Remove all disconnected connections
	 */
	private void cleanup() {
		Collection<DcServerConnection> remove = new ArrayList<DcServerConnection>();
	    for (DcServerConnection connection : connections) {
	        if (!connection.isActive()) {
	        	connection.disconnect();
	        	remove.add(connection);
	        }
	    }

	    connections.removeAll(remove);
	}
	
	/**
	 * Handles the actual client request to the server. Requests can vary between login, data and task execution requests.
	 * Each time a request is made to the server a thread is started on the server. 
	 * This connector will wait for the response to come through.
	 *  
	 * @param cr the client request, containing all the information to process the request on the server
	 * @return the response from the server
	 */
	private IServerResponse processClientRequest(ClientRequest cr) {
		IServerResponse sr = null;

		try {
			ClientRequestHandler handler = new ClientRequestHandler(cr);
			sr = handler.process();
			
			if (sr instanceof ServerErrorResponse) {
				ServerErrorResponse ser = (ServerErrorResponse) sr;
				logger.error("The server has encountered an error while processing the request: " + 
						ser.getErrorMessage(), ser.getError());
				
				GUI.getInstance().displayErrorMessage(ser.getErrorMessage());
				
				// to avoid ClassCastExecptions.
				sr = null;
			}

		} catch (IOException e) {
			logger.error("Error while sending the request " + cr + " to " + serverAddress + ":" + applicationServerPort +
					". Most likely the server is down. Please check with your server administrator.", e);
		} catch (ClassNotFoundException e) {
			logger.error("Error while sending the request " + cr + " to " + serverAddress + ":" + applicationServerPort + 
					". Most likely the server and client version are in conflict.", e);
		}
		
		return sr;
	}
	
	private void loadApplicationSettings() {
        try {
            ClientRequestApplicationSettings cras = new ClientRequestApplicationSettings(su);
            ServerApplicationSettingsRequestResponse response = (ServerApplicationSettingsRequestResponse) processClientRequest(cras);

            if (response == null) return;
            
            Settings settings = response.getSettings();
            for (Setting setting : settings.getSettings().getSettings()) {
                if (DcSettings.getSetting(setting.getKey()).isReadonly())
                    DcSettings.set(setting.getKey(), setting.getValue());
            }
         } catch (Exception e) {
             logger.error("Unable to retrieve the settings from the server", e);
         }
	}
	
	@Override
	public SecuredUser getUser() {
		return su;
	}
	
	@Override
    public void deleteModule(int moduleIdx) {
        logger.error("User requests to delete module while this is not allowed.");
    }

	@Override
	public SecuredUser login(String username, String password) {
        boolean success = false;
        int retry = 0;
        
        GUI.getInstance().showSplashScreen(false);
        while (!success && retry < 3) {
                               
            LoginDialog dlg = new LoginDialog();
            GUI.getInstance().openDialogNativeModal(dlg);
            if (dlg.isCanceled()) break;
            
            ClientRequest cr = new ClientRequestLogin(dlg.getLoginName(), dlg.getPassword());
            ServerLoginResponse response = (ServerLoginResponse) processClientRequest(cr);
            
            // register this user as the logged in user
            if (response != null)
                su = response.getUser();
            
            success = su != null;
        }
        
        if (!success) {
            System.exit(0);
        } else {
            GUI.getInstance().showSplashScreen(true);
		}
		return su;
	}
	
	@Override
	public List<DcSimpleValue> getSimpleValues(int module, boolean includeIcons) {
        ClientRequestSimpleValues cr = new ClientRequestSimpleValues(getUser(), module, includeIcons);
        ServerSimpleValuesResponse response = (ServerSimpleValuesResponse) processClientRequest(cr);
        return response != null ? response.getValues() : null;
	}
	
	@Override
	public Collection<Picture> getPictures(String parentID) {
		ClientRequestItems cr = new ClientRequestItems(getUser());
        DataFilter df = new DataFilter(DcModules._PICTURE);
        df.addEntry(new DataFilterEntry(DcModules._PICTURE, Picture._A_OBJECTID, Operator.EQUAL_TO, parentID));
		cr.setDataFilter(df);
	      
		ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cr);
		
		Collection<Picture> pictures = null;
		if (response != null && response.getItems() != null) {
		    pictures = new ArrayList<Picture>();
		    for (DcObject dco : response.getItems()) {
		        pictures.add((Picture) dco);
		    }
		}
		
		return pictures;
	}
	
	@Override
    public Map<DcField, Collection<IValueEnhancer>> getValueEnhancers() {
        ClientRequestValueEnhancers crve = new ClientRequestValueEnhancers(DcConfig.getInstance().getConnector().getUser());
        ServerValueEnhancersRequestResponse response = (ServerValueEnhancersRequestResponse) processClientRequest(crve);
        return response != null ? response.getEnhancers() : null;
    }

    @Override
	public Collection<DcObject> getReferences(int mappingModuleIdx, String parentKey, boolean full) {
		ClientRequestItems cr = new ClientRequestItems(getUser());
		
		DataFilter df = new DataFilter(mappingModuleIdx);
		df.addEntry(new DataFilterEntry(mappingModuleIdx, DcMapping._A_PARENT_ID, Operator.EQUAL_TO, parentKey));
		
		cr.setDataFilter(df);
		cr.setFields(full ? null : DcModules.get(mappingModuleIdx).getMinimalFields(null));
	      
		ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cr);
		return response != null ? response.getItems() : null;
	}
	
    @Override
	public Map<String, Integer> getChildrenKeys(String parentKey, int childModuleIdx) {
    	ClientRequestItemKeys cr = new ClientRequestItemKeys(getUser());
        DataFilter df = new DataFilter(childModuleIdx);
        DcModule module = DcModules.get(childModuleIdx);
        df.addEntry(new DataFilterEntry(DataFilterEntry._AND, childModuleIdx, module.getParentReferenceFieldIndex(), Operator.EQUAL_TO, parentKey));
        cr.setDataFilter(df);
    	
        ServerItemKeysRequestResponse response = (ServerItemKeysRequestResponse) processClientRequest(cr);
		return response != null ? response.getItems() : null;
    }
	
    @Override
	public Collection<DcObject> getChildren(String parentKey, int childModuleIdx, int[] fields) {
    	ClientRequestItems cr = new ClientRequestItems(getUser());
    	cr.setFields(fields);
    	
        DataFilter df = new DataFilter(childModuleIdx);
        DcModule module = DcModules.get(childModuleIdx);
        df.addEntry(new DataFilterEntry(DataFilterEntry._AND, childModuleIdx, module.getParentReferenceFieldIndex(), Operator.EQUAL_TO, parentKey));
        cr.setDataFilter(df);
    	
		ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cr);
		return response != null ? response.getItems() : null;
    }
    
    @Override
	public Loan getCurrentLoan(String parentKey) {
    	ClientRequestItems cr = new ClientRequestItems(getUser());
        DataFilter df = new DataFilter(DcModules._LOAN);
        df.addEntry(new DataFilterEntry(DcModules._LOAN, Loan._B_ENDDATE, Operator.IS_EMPTY, null));
        df.addEntry(new DataFilterEntry(DcModules._LOAN, Loan._D_OBJECTID, Operator.EQUAL_TO, parentKey));
        df.setResultLimit(1);
        cr.setDataFilter(df);
        
		ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cr);
		Loan loan = null;
		if (response != null) {
			List<DcObject> items = response.getItems();
			loan = items.size() > 0 ? (Loan) items.get(0) : null;
		}
        
		return loan == null ? new Loan() : loan;
    }
    
    @Override
	public List<DcObject> getLoans(String parentKey) {
    	ClientRequestItems cr = new ClientRequestItems(getUser());
        DataFilter df = new DataFilter(DcModules._LOAN);
        df.addEntry(new DataFilterEntry(DcModules._LOAN, Loan._D_OBJECTID, Operator.EQUAL_TO, parentKey));
        cr.setDataFilter(df);
		ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cr);
		return response != null ? response.getItems() : null;
    }
	
	@Override
	public DcObject getItem(int moduleIdx, String key) {
		return getItem(moduleIdx, key, null);
	}
	
	@Override
	public DcObject getItem(int moduleIdx, String key, int[] fields) {
		ClientRequestItem cr = new ClientRequestItem(getUser(), ClientRequestItem._SEARCHTYPE_BY_ID, moduleIdx, key);
		cr.setFields(fields);
		
		ServerItemRequestResponse response = (ServerItemRequestResponse) processClientRequest(cr);
		return response != null ? response.getItem() : null;
    }
	
    @Override
    public List<DcObject> getItems(int moduleIdx, int[] fields) {
        return getItems(new DataFilter(moduleIdx), fields);
    }
	
	@Override
	public DcObject getItemByKeyword(int module, String keyword) {
		ClientRequestItem cr = new ClientRequestItem(
				getUser(), 
				ClientRequestItem._SEARCHTYPE_BY_KEYWORD, 
				module, 
				keyword);
		ServerItemRequestResponse response = (ServerItemRequestResponse) processClientRequest(cr);
		return response != null ? response.getItem() : null;
	}

	@Override
	public DcObject getItemByExternalID(int module, String keyType, String keyword) {
		ClientRequestItem cr = new ClientRequestItem(
				getUser(), 
				ClientRequestItem._SEARCHTYPE_BY_EXTERNAL_ID, 
				module, 
				keyword);
		
		cr.setExternalKeyType(keyType);
		
		ServerItemRequestResponse response = (ServerItemRequestResponse) processClientRequest(cr);
		return response != null ? response.getItem() : null;
	}
	
	@Override
	public DcObject getItemByUniqueFields(DcObject dco) {
		ClientRequestItem cr = new ClientRequestItem(
				getUser(), 
				ClientRequestItem._SEARCHTYPE_BY_UNIQUE_FIELDS, 
				dco.getModule().getIndex(), 
				dco);
		ServerItemRequestResponse response = (ServerItemRequestResponse) processClientRequest(cr);
		return response != null ? response.getItem() : null;
	}
	
	@Override
	public DcObject getItemByDisplayValue(int moduleIdx, String displayValue) {
		ClientRequestItem cr = new ClientRequestItem(
				getUser(), 
				ClientRequestItem._SEARCHTYPE_BY_DISPLAY_VALUE, 
				moduleIdx, 
				displayValue);
		ServerItemRequestResponse response = (ServerItemRequestResponse) processClientRequest(cr);
		return response != null ? response.getItem() : null;
	}
	
	@Override
	public Map<String, Integer> getKeys(DataFilter df) {
    	ClientRequestItemKeys cr = new ClientRequestItemKeys(getUser());
        cr.setDataFilter(df);
        ServerItemKeysRequestResponse response = (ServerItemKeysRequestResponse) processClientRequest(cr);
		return response != null ? response.getItems() : null;
	}
	
	@Override
	public List<DcObject> getItems(DataFilter df) {
		return getItems(df, null);
	}

	@Override
	public List<DcObject> getItems(DataFilter df, int fields[]) {
    	ClientRequestItems cr = new ClientRequestItems(getUser());
    	cr.setFields(fields);
        cr.setDataFilter(df);
        ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cr);
		return response != null ? response.getItems() : null;
	}
	
	@Override
	public void createUser(User user, String password) {
    	ClientRequestUser cr = new ClientRequestUser(ClientRequestUser._ACTIONTYPE_CREATE, getUser(), user, password);
        processClientRequest(cr);
	}

	@Override
	public void changePassword(User user, String password) {
    	ClientRequestUser cr = new ClientRequestUser(ClientRequestUser._ACTIONTYPE_CHANGEPASSWORD, getUser(), user, password);
        processClientRequest(cr);
	}
	
	@Override
	public void updateUser(User user) {
    	ClientRequestUser cr = new ClientRequestUser(ClientRequestUser._ACTIONTYPE_UPDATE, getUser(), user, null);
        processClientRequest(cr);
	}
	
	@Override
	public void dropUser(User user) {
    	ClientRequestUser cr = new ClientRequestUser(ClientRequestUser._ACTIONTYPE_DROP, getUser(), user, null);
        processClientRequest(cr);
	}
	
    @Override
    public DcResultSet executeSQL(String sql) {
        ClientRequestExecuteSQL csr = new ClientRequestExecuteSQL(su, sql);
        IServerResponse response = processClientRequest(csr);
        
        DcResultSet result = null;
        if (response != null) {
            ServerSQLResponse ssr = (ServerSQLResponse) response;
            result = ssr.getResult();
        }
        
        return result == null ? new DcResultSet() : result;
    }
    
	@Override
	public boolean deleteItem(DcObject dco) throws ValidationException {
	    ClientRequestItemAction cr = new ClientRequestItemAction(
	            getUser(), ClientRequestItemAction._ACTION_DELETE, dco);
	    IServerResponse response = processClientRequest(cr);
	    
	    boolean success = false;
        if (response != null) {
            ServerActionResponse sar = (ServerActionResponse) response;
            success = sar.isSuccess();
        }
	    
		return success;
	}

	@Override
	public boolean saveItem(DcObject dco) throws ValidationException {
        ClientRequestItemAction cr = new ClientRequestItemAction(
                getUser(), ClientRequestItemAction._ACTION_SAVE, dco);
        
        // make sure to load the bytes as the image inside the ImageIcon will not be available on the server.
        dco.loadImageData();
        
        IServerResponse response = processClientRequest(cr);
        
        boolean success = false;
        if (response != null) {
            ServerActionResponse sar = (ServerActionResponse) response;
            success = sar.isSuccess();
        }
        
        return success;	
	}

	@Override
	public void close() {
	}

	@Override
	public int getCount(int module, int field, Object value) {
		return 0;
	}

	@Override
	public List<DcObject> getReferencingItems(int moduleIdx, String ID) {
		ClientRequestReferencingItems cri = new ClientRequestReferencingItems(su, moduleIdx, ID);
        ServerItemsRequestResponse response = (ServerItemsRequestResponse) processClientRequest(cri);
        return response != null ? response.getItems() : null;
	}

	@Override
	public boolean checkUniqueness(DcObject dco, boolean exitingItem) {
		return true;
	}

	@Override
	public void executeTask(DcTask task) {
	    // We start the thread instead of sending it to the server.
	    // The started task will use the connector again to save / delete the items (where applicable) 
	    // and will call the server within the thread. At completion the necessary requests are handled.
        Thread thread = new Thread(task);
        thread.start();
	}

    @Override
    public ServerModulesRequestResponse getModules() {
    	ClientRequestModules crm = new ClientRequestModules(su);
        ServerModulesRequestResponse response = (ServerModulesRequestResponse) processClientRequest(crm);
        return response;
    }

    @Override
    public void shutdown(boolean saveChanges) {
        DcConfig dcc = DcConfig.getInstance();
        dcc.getClientSettings().save();
        dcc.getConnector().close();

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
        
        GUI.getInstance().getMainFrame().setVisible(false);

        System.exit(0);
    }
}
