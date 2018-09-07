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

package net.datacrow.fileimporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.services.IOnlineSearchClient;
import net.datacrow.core.utilities.StringUtils;

import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

/**
 * Representation of a physical music file.
 * @author Robert Jan van der Waals
 */
public class MusicFile implements IOnlineSearchClient {
    
    private static Logger logger = Logger.getLogger(MusicFile.class.getName());
    
    private String album;
    private String artist;
    private String genre;
    private String year;
    private String title;
    private String encodingType;
    private DcImageIcon image;
    
    private int track;
    private int bitrate;
    private int length;
    
    private Collection<DcObject> albums = new ArrayList<DcObject>();
    
    public MusicFile() {}
    
    public MusicFile(String filename) {
        AudioFile audioFile;
        try {
        	audioFile = AudioFileIO.read(new File(filename));
        
            Tag tag = audioFile.getTag();
            if (tag != null) {
                album = tag.getFirst(FieldKey.ALBUM);
                artist = tag.getFirst(FieldKey.ARTIST);
                genre = getGenre(tag.getFirst(FieldKey.GENRE));
                year = tag.getFirst(FieldKey.YEAR);
                title = tag.getFirst(FieldKey.TITLE);
                
                for (Artwork aw : tag.getArtworkList())
                    image = new DcImageIcon(aw.getBinaryData());
                
                bitrate = (int) audioFile.getAudioHeader().getBitRateAsNumber();
                length = audioFile.getAudioHeader().getTrackLength();
                encodingType = audioFile.getAudioHeader().getEncodingType();
                
                try {
                    String s = tag.getFirst(FieldKey.TRACK);
                    if (s != null && s.length() > 0) {
                        if (s.indexOf("/") > 0)
                            s = s.substring(0, s.indexOf("/"));
                        
                        track = Integer.parseInt(s);
                    }
                        
                } catch (Exception e) {
                    logger.debug("Could not parse track [" + tag.getFirst(FieldKey.TRACK) + "]", e);
                }
            }
        } catch (Exception e) {
            logger.error("Could not parse music file " + filename, e);
        }
    }
    
    private String getGenre(String s) {
        String genre = s != null && s.length() > 0 ? s : null;
        
        if (genre != null) {
            try {
                String genreIdx = StringUtils.getValueBetween("(", ")", genre);
                int index = Integer.parseInt(genreIdx);
                genre = DcRepository.Collections.colMusicGenres[index];
            } catch (Exception exp) {}
        }
        
        return genre;
    }    
        
    public DcImageIcon getImage() {
        return image;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public int getTrack() {
        return track;
    }

    public String getYear() {
        return year;
    }

    public int getBitrate() {
        return bitrate;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public int getLength() {
        return length;
    }

    @Override
    public void addError(Throwable t) {
        logger.error(t.getMessage(), t);
    }

    @Override
    public void addError(String message) {}

    @Override
    public void addMessage(String message) {}

    @Override
    public void addObject(DcObject dco) {
        albums.add(dco);
    }

    @Override
    public void addWarning(String warning) {}

    @Override
    public DcModule getModule() {
        return DcModules.get(DcModules._MUSIC_ALBUM);
    }

    @Override
    public void processed(int i) {}

    @Override
    public void processing() {}

    @Override
    public void processingTotal(int i) {}

    @Override
    public int resultCount() {
        return albums.size();
    }

    @Override
    public void stopped() {}

    public DcObject getDcObject() {
        return null;
    } 
}


