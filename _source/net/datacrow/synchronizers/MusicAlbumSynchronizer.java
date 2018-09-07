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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.MusicAlbum;
import net.datacrow.core.objects.helpers.MusicTrack;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;
import net.datacrow.core.services.OnlineSearchHelper;
import net.datacrow.core.synchronizers.DefaultSynchronizer;
import net.datacrow.core.synchronizers.Synchronizer;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;
import net.datacrow.fileimporter.MusicFile;

import org.apache.log4j.Logger;

/**
 * Basically the same as the AudioCdSynchronizer class. 
 * However, for customization reasons (and the likes) it was decided to keep this class.
 * @author Robert Jan van der Waals
 */
public class MusicAlbumSynchronizer extends DefaultSynchronizer {

    private static final long serialVersionUID = 3616524098040009514L;

    private static Logger logger = Logger.getLogger(MusicAlbumSynchronizer.class.getName());
    
    public MusicAlbumSynchronizer() {
        super(DcResources.getText("lblMassItemUpdate", DcModules.get(DcModules._MUSIC_ALBUM).getObjectName()),
              DcModules._MUSIC_ALBUM);
    }
    
    @Override
	public Synchronizer getInstance() {
		return new MusicAlbumSynchronizer();
	}
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgMusicFileMassUpdateHelp");
    }
    
    @Override
    public boolean canParseFiles() {
        return true;
    }

    @Override
    public boolean canUseOnlineServices() {
        return true;
    }
    
    @Override
    protected boolean parseFiles(DcObject dco) {
        
        boolean updated = false;
        
        if (!client.isReparseFiles()) 
            return updated;
            
        for (DcObject child : dco.getChildren()) {
            
            if (client.isCancelled()) break;
            
            String filename = child.getFilename();
            
            if (filename == null || filename.trim().length() == 0)
                continue;
            
            File tst = new File(filename);
            if (!tst.exists())
                filename = filename.replaceAll("`", "'");
            
            client.notify(DcResources.getText("msgParsing", filename));
            
            MusicFile musicFile = new MusicFile(filename);

            dco.setValue(MusicAlbum._A_TITLE, musicFile.getAlbum());
            
            DcObject artist  = dco.createReference(MusicAlbum._F_ARTISTS, musicFile.getArtist());
            
            setValue(child, MusicTrack._K_QUALITY, Long.valueOf(musicFile.getBitrate()));
            setValue(child, MusicTrack._J_PLAYLENGTH, Long.valueOf(musicFile.getLength()));
            setValue(child, MusicTrack._L_ENCODING, musicFile.getEncodingType());
            setValue(child, MusicTrack._A_TITLE, musicFile.getTitle());

            child.createReference(MusicTrack._G_ARTIST, artist);
            
            setValue(child, MusicTrack._C_YEAR, musicFile.getYear());
            setValue(child, MusicTrack._F_TRACKNUMBER, Long.valueOf(musicFile.getTrack()));
            
            child.createReference(MusicTrack._H_GENRES, musicFile.getGenre());
            updated = true;
        }
        
        return updated;
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected boolean matches(DcObject result, String searchString, int fieldIdx) {
        boolean matches = super.matches(result, searchString, fieldIdx);
        if (matches && (client.getSearchMode() == null || client.getSearchMode().keywordSearch())) {
            // Additionally one of the artists has to match. Only used for keyword searches!
            Collection<DcMapping> artists1 = (Collection<DcMapping>) result.getValue(MusicAlbum._F_ARTISTS);
            Collection<DcMapping> artists2 = (Collection<DcMapping>) getDcObject().getValue(MusicAlbum._F_ARTISTS);
            artists1 = artists1 == null ? new ArrayList<DcMapping>() : artists1;
            artists2 = artists2 == null ? new ArrayList<DcMapping>() : artists2;
            for (DcObject person1 : artists1) {
                for (DcObject person2 : artists2) {
                    matches = StringUtils.equals(person1.toString(), person2.toString()); 
                    if (matches) break;
                }
            }
        }
        return matches;    
    }
    
    @Override
    protected void merge(DcObject target, DcObject source, OnlineSearchHelper osh) {
        super.merge(target, source, osh);

        Collection<DcObject> oldTracks = target.getChildren() == null ? new ArrayList<DcObject>() : target.getChildren();
        Collection<DcObject> newTracks = source.getChildren() == null ? new ArrayList<DcObject>() : source.getChildren();
        
        if (oldTracks.size() == 0) {
            Connector connector = DcConfig.getInstance().getConnector();
            for (DcObject track : newTracks) {
                track.setValue(track.getParentReferenceFieldIndex(), target.getID());
                target.addChild(track);
                try {
                    connector.saveItem(track);
                } catch (Exception e) {
                    logger.error("Unable to save new music track " + track, e);
                }
            }
        } else {
            for (DcObject currentTrack : oldTracks) {
                for (DcObject newTrack : newTracks) {
                    if (StringUtils.equals(currentTrack.getDisplayString(MusicTrack._A_TITLE), newTrack.getDisplayString(MusicTrack._A_TITLE))) {
                        currentTrack.copy(newTrack, true, false);
                        break;
                    } else if (newTracks.size() == oldTracks.size() && 
                              !CoreUtilities.isEmpty(currentTrack.getValue(MusicTrack._J_PLAYLENGTH)) && 
                               currentTrack.getDisplayString(MusicTrack._J_PLAYLENGTH).equals(newTrack.getDisplayString(MusicTrack._J_PLAYLENGTH))) {    
                        currentTrack.copy(newTrack, true, false);
                        break;
                    }
                }
            }
        }
    }
}
