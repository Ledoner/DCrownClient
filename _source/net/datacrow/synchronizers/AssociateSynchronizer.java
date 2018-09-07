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
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.synchronizers.DefaultSynchronizer;
import net.datacrow.core.synchronizers.Synchronizer;

public class AssociateSynchronizer extends DefaultSynchronizer {

	private static final long serialVersionUID = 7292793329938977166L;

    public AssociateSynchronizer() {
        super(DcResources.getText("lblMassItemUpdate", DcModules.getCurrent().getObjectName()),
              DcModules.getCurrent().getIndex());    
    }

    @Override
	public Synchronizer getInstance() {
		return new AssociateSynchronizer();
	}
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgAssociateMassUpdateHelp",
                                    new String[] {DcModules.getCurrent().getObjectNamePlural().toLowerCase(),
                                                  DcModules.getCurrent().getObjectName().toLowerCase()});
    }
}
