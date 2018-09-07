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

package net.datacrow.util.launcher;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public abstract class Launcher {

	private static Logger logger = Logger.getLogger(Launcher.class.getName());
	
	public abstract void launch();
	
    protected Desktop getDesktop() {
        if (Desktop.isDesktopSupported())
            return Desktop.getDesktop();
        
        return null;
    }
    
    protected void runCmd(String[] command) throws Exception {
    	try { 
    	    Process p = new ProcessBuilder(command).start();
    		
    		InputStream is = p.getErrorStream();
    	    InputStreamReader isr = new InputStreamReader(is);
    	    BufferedReader br = new BufferedReader(isr);
    	    String line;
    	    
    	    // log the error messages
    	    while ((line = br.readLine()) != null) {
    	        logger.error(line);
    	    }
    	    
    	    p.waitFor();
    	    br.close();
    		
    	} catch (IOException ie) {
    	    String s = "";
    	    for (String cmd : command)
    	        s+= cmd + " ";
    	    
        	logger.debug("Could not launch command using the runCmd method [" + s.trim() + "]", ie);
        	throw new Exception(ie);
    	}
    }
}
