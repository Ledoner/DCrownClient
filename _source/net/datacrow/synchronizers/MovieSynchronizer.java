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

public class MovieSynchronizer extends DefaultSynchronizer {

    private static final long serialVersionUID = -3993167519552663866L;

    public MovieSynchronizer() {
        super(DcResources.getText("lblMassItemUpdate", DcModules.get(DcModules._MOVIE).getObjectName()),
              DcModules._MOVIE);
    }
    
    @Override
	public Synchronizer getInstance() {
		return new MovieSynchronizer();
	}
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgMovieMassUpdateHelp");
    }
}
