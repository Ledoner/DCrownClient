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

package net.datacrow.fileimporter.movie;

import java.io.RandomAccessFile;

import net.datacrow.core.utilities.CoreUtilities;

import org.apache.log4j.Logger;
import org.ebml.io.FileDataSource;
import org.ebml.matroska.MatroskaDocType;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileTrack;

class FilePropertiesMKV extends FileProperties {
    
    private static Logger logger = Logger.getLogger(FilePropertiesMKV.class.getName());

    @Override
	protected void process(RandomAccessFile raf, String filename) throws Exception {
        raf.seek(0);
        
        FileDataSource fds = new FileDataSource(filename);
        
        try {
            MatroskaFile mkf = new MatroskaFile(fds);
    		mkf.readFile();
    
    		double duration = mkf.getDuration();
    		duration = duration > 0 ? duration / 1000 : duration;
    		setDuration((int) duration);
    		
    		if (mkf.getTrackList() != null) {
    		    
        		for (MatroskaFileTrack track : mkf.getTrackList()) {
        		    if (track.TrackType ==  MatroskaDocType.track_video) {
        		        setVideoResolution(track.Video_PixelWidth + "x" + track.Video_PixelHeight);
        		        setVideoCodec(track.CodecID);
        		        setName(track.Name);
        		        
        		        if (track.Language != null) {
            		        String language = CoreUtilities.getLanguage(track.Language);
            		        language = language == null || language.length() == 0 ? track.Language : language;
            		        setLanguage(language);
        		        }
        		    } else if (track.TrackType ==  MatroskaDocType.track_subtitle) {
        		        String subtitles = getSubtitles();
        		        subtitles += subtitles.length() > 0 ? ", " : "";
        		        
                        String language = CoreUtilities.getLanguage(track.Name);
                        language = language == null || language.length() == 0 ? track.Name : track.Name;
        		        subtitles += language;
        		        setSubtitles(subtitles);
        		        
        		    } else if (track.TrackType ==  MatroskaDocType.track_audio) {
        		        setAudioChannels(track.Audio_Channels);
        		        setAudioCodec(track.CodecID);
        		    }
        		}
    		}

    		setContainer("MKV (Matroska)");

        } catch (Exception e) {
            logger.error("Failed to parse MKV file " + filename, e);
        } finally {
            fds.close();
        }
	}
}
