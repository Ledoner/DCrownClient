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

package net.datacrow.util.freedb;

public class FreedbQueryResult {
    
    private String album,artist,id,category;
    private boolean exactMatch;
    
    public FreedbQueryResult(String queryResult) {
        this(queryResult, false);
    }
    
    public FreedbQueryResult(String freedbQueryResult, boolean exactMatch) {
        this.exactMatch = exactMatch;
        
		String[] fields = freedbQueryResult.split( " ", 3 );

		this.category = fields[0];
		this.id = fields[1];

		String[] infos = fields[2].split(" / ", 2);
		this.artist = infos[0];
		this.album = (infos.length > 1) ? infos[1] : "";
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public String getDiscId() {
        return this.id;
    }
    
    public String getAlbum() {
        return this.album;
    }
    
    public String getArtist() {
        return this.artist;
    }
    
    public boolean isExactMatch() {
        return this.exactMatch;
    }
}
