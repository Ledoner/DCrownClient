/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                       (c) 2003 The Data Crow team                          *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                                                                            *
 *       This library is free software; you can redistribute it and/or        *
 *        modify it under the terms of the GNU Lesser General Public          *
 *       License as published by the Free Software Foundation; either         *
 *     version 2.1 of the License, or (at your option) any later version.     *
 *                                                                            *
 *      This library is distributed in the hope that it will be useful,       *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU       *
 *           Lesser General Public License for more details.                  *
 *                                                                            *
 *     You should have received a copy of the GNU Lesser General Public       *
 *    License along with this library; if not, write to the Free Software     *
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA   *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.util.freedb;

import java.io.IOException;
import java.io.InputStream;

import net.datacrow.core.DcConfig;

import org.apache.log4j.Logger;


public class DiscIDUtil {
	
	private static Logger logger = Logger.getLogger(DiscIDUtil.class.getName());
    
    private boolean success = false;
    
    /**
     * Indicates whether the disc ID was read successfully
     * @return success indicator
     */
    public boolean isSuccess() {
    	return success;
    }
    
    /**
     * Read the disc ID. The disc ID is read in a separate process as this is
     * done platform dependent. 
     * @return valid disc id (with track offsets)
     * @throws IOException
     */
    public String readDiscID() throws IOException {
        String exec = getScript();
        setExecutionAllowed(exec);
        
        ProcessBuilder pb = new ProcessBuilder(new String[] {exec});
        Process p =  pb.start();
        
        try { 
            p.waitFor();
        } catch (Exception exp) {}
        
        int exitvalue = p.exitValue();
        InputStream is = null;
        if (exitvalue == 1) {
            is = p.getErrorStream();
        } else {
            is = p.getInputStream();
            success = true;
        }

        byte[] b = new byte[is.available()];
        is.read(b);
        String s = new String(b);
        if (s.equals("")) {
            success = false;
        }
        
        is.close();
        return s;
    }    
    
    private String getScript() {
        String exec;
     
        DcConfig dcc = DcConfig.getInstance(); 
        if (dcc.getPlatform().isMac()) {
            exec = dcc.getPluginsDir() + "discid/mac/discid";
        } else if (dcc.getPlatform().isWin()) {
            exec = dcc.getPluginsDir() + "discid/win32/discid.exe";
            if (exec.startsWith("/")) exec = exec.substring(1, exec.length());
        } else {
            exec = dcc.getPluginsDir() + "discid/linux/discid";
        }           
        
        return exec;
    }
    
    /**
     * Change the permissions on the script file (non Windows users)
     * for the current user (allow execution).
     */
    private void setExecutionAllowed(String scriptFile) {
        try {
            if (!DcConfig.getInstance().getPlatform().isWin()) {
                Process p = new ProcessBuilder(new String[] {"chmod", "u=rwx", scriptFile}).start();
                p.waitFor();
            }
        } catch (Exception e) {
            logger.error(e, e);
        }        
    }    
}
