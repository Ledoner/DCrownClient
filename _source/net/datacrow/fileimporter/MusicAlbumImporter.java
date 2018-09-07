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

import net.datacrow.core.fileimporter.FileImporter;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.MusicAlbum;
import net.datacrow.core.objects.helpers.MusicTrack;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Converter;
import net.datacrow.core.utilities.Hash;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;

/**
 * Music file importer.
 * Creates music album for music file collections.
 * 
 * @author Robert Jan van der Waals
 */
public class MusicAlbumImporter extends FileImporter {
    
    private static final int _DONOTUSE = 0;
    private static final int _DIRISALBUM = 1;
    private static final int _DIRISARTISTS = 2;
    private static final int _DIRISARTIST_SUBDIRISALBUM = 3;
    
    protected final Collection<DcObject> albums = new ArrayList<DcObject>();

    public MusicAlbumImporter() {
        super(DcModules._MUSIC_ALBUM);
    }
    
    @Override
	public FileImporter getInstance() {
		return new MusicAlbumImporter();
	}

    @Override
    public String[] getSupportedFileTypes() {
        return new String[] {"mp3", "ogg", "mp4", "mp4a", "m4p", "flac", "m4a",
                             "flc", "ape", "asf", "wav", "mpc", "ra", "wma", "rm"};
    }

    @Override
    public void beforeParse() {
        albums.clear();
    }
    
    @Override
    public boolean canImportArt() {
        return true;
    }
    
    @Override
    protected void afterParse(DcObject dco) {
        if (!albums.contains(dco))
            albums.add(dco);
    }

    @Override
    protected void afterImport() {
        for (DcObject dco : albums) {
        	Collection<DcObject> children = dco.getChildren();
        	if (children != null && children.size() > 0) 
        		super.afterParse(dco);
        }
    }

    @Override
    public DcObject parse(String filename, int directoryUsage) {
        DcObject ma = DcModules.get(DcModules._MUSIC_ALBUM).getItem();
        
        try {
            MusicFile musicFile = new MusicFile(filename);
            
            String artist = musicFile.getArtist();
            String album = musicFile.getAlbum();
            
            String s = null; 
            switch (directoryUsage) {
            case _DONOTUSE:
                break;
            case _DIRISALBUM:
                s = getDirectoryName(filename, 0);
                album = s != null ? s : album;
                break;
            case _DIRISARTISTS:
                s = getDirectoryName(filename, 0);
                artist = s != null ? s : artist;
                break;
            case _DIRISARTIST_SUBDIRISALBUM:
                s = getDirectoryName(filename, 0);
                album = s != null ? s : artist;

                s = getDirectoryName(filename, 1);
                artist = s != null ? s : album;

                break;
            }

            if (!CoreUtilities.isEmpty(album)) {
                album = Converter.databaseValueConverter(album);
                ma = getAlbum(album, albums);
            }
            
            if (ma == null) {
                ma = DcModules.get(DcModules._MUSIC_ALBUM).getItem();
                ma.setValue(MusicAlbum._A_TITLE, album);
                
                if (musicFile.getImage() != null)
                    ma.setValue(MusicAlbum._J_PICTUREFRONT, musicFile.getImage());
                
                setImages(filename, ma, MusicAlbum._J_PICTUREFRONT, 
                                        MusicAlbum._K_PICTUREBACK, 
                                        MusicAlbum._L_PICTURECD);
                
                ma.createReference(MusicAlbum._F_ARTISTS, artist);
            } 
            
            DcObject genre = null;
            if (musicFile.getGenre() != null)
                genre = ma.createReference(MusicAlbum._G_GENRES, musicFile.getGenre());

            DcObject mt = DcModules.get(DcModules._MUSIC_TRACK).getItem();
            mt.setValue(MusicTrack._SYS_FILENAME, filename);
            Hash.getInstance().calculateHash(mt);
            
            mt.setValue(MusicTrack._A_TITLE, getName(filename, directoryUsage));
            
            String year = musicFile.getYear();
            
            if (year != null)
                year = year.indexOf("-") > -1 ? year.substring(0, year.indexOf("-")) : year;
            
            int track = musicFile.getTrack();
            track = track == 0 ? ma.getChildren() != null ?  ma.getChildren().size() + 1 : 1  : track;
            
            mt.setValue(MusicTrack._K_QUALITY, musicFile.getBitrate() <= 0 ? null : Long.valueOf(musicFile.getBitrate()));
            mt.setValue(MusicTrack._J_PLAYLENGTH, musicFile.getLength() <= 0 ? null : Long.valueOf(musicFile.getLength()));
            mt.setValue(MusicTrack._L_ENCODING, musicFile.getEncodingType());
            mt.setValue(MusicTrack._A_TITLE, musicFile.getTitle());
            mt.setValue(MusicTrack._C_YEAR, year);
            mt.setValue(MusicTrack._F_TRACKNUMBER, track <= 0 ? null : Long.valueOf(track));
            
            if (genre != null)
                mt.createReference(MusicTrack._H_GENRES, genre);
            
            mt.createReference(MusicTrack._G_ARTIST, artist);                

            ma.addChild(mt);
            
        } catch (Exception exp) {
            getClient().notify(DcResources.getText("msgCouldNotReadInfoFrom", filename));
        }

        if (ma != null)
            ma.setIDs();
        
        return ma;
    }
    
    private DcObject getAlbum(String title, Collection<DcObject> albums) {
        if (title == null)
            return null;
        
        for (DcObject dco : albums) {
            String albumName = (String) dco.getValue(MusicAlbum._A_TITLE);
            if (StringUtils.equals(albumName, title))
                return dco;
        }
        
        return null;
    }    
    
    private String getDirectoryName(String filename, int pos) {
        if (pos == 0)
            return new File(filename).getParentFile().getName();
        else
            return new File(filename).getParentFile().getParentFile().getName();
    }    
}