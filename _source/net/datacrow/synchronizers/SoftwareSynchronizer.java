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

package net.datacrow.synchronizers;

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.helpers.Software;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.services.plugin.IServer;
import net.datacrow.core.synchronizers.DefaultSynchronizer;
import net.datacrow.core.synchronizers.Synchronizer;

public class SoftwareSynchronizer extends DefaultSynchronizer {

    private static final long serialVersionUID = -5833866205911128015L;

    public SoftwareSynchronizer() {
        super(DcResources.getText("lblMassItemUpdate", DcModules.get(DcModules._SOFTWARE).getObjectName()),
              DcModules._SOFTWARE);
    }
    
    @Override
	public Synchronizer getInstance() {
		return new SoftwareSynchronizer();
	}
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgSoftwareMassUpdateHelp");
    }
    
    @Override
    protected String getSearchString(int field, IServer server) {
        if (field == Software._A_TITLE && server.getName().equals("MobyGames") && dco.getValue(Software._H_PLATFORM) != null) {
            String s = super.getSearchString(field, server);
            return !s.toLowerCase().contains(" for ") ? super.getSearchString(field, server) +  " for " + dco.getDisplayString(Software._H_PLATFORM) : s;
        } else {
            return super.getSearchString(field, server);
        }
    }
}
