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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class FilePropertiesMovie  {
    
    private static Logger logger = Logger.getLogger(FilePropertiesMovie.class.getName());
    
    private static final int[][] MAGIC_BYTES = { 
        { 0x00, 0x00, 0x01, 0xb3 }, // MPEG (video)
        { 0x00, 0x00, 0x01, 0xba }, // MPEG (video)
        { 0x52, 0x49, 0x46, 0x46 }, // RIFF (WAV / audio, AVI / video)
        { 0x4f, 0x67, 0x67, 0x53 }, // OGM 
        { 0x44, 0x56, 0x44, 0x56 }, // IFO (DVDV)
        { 0x1a, 0x45, 0xdf, 0xa3 }, // MKV
        { 0x30, 0x26, 0xb2, 0x75 }  // ASF (audio / video)
    };
    
    private FileProperties format;
    
    private FileProperties[] formats = { new FilePropertiesMPEG(),
            new FilePropertiesMPEG(), new FilePropertiesRIFF(),
            new FilePropertiesOGM(), new FilePropertiesIFO(),
            new FilePropertiesMKV(), new FilePropertiesASF()};
    
    /**
     * Initializes the movie file properties. 
     * @param filename
     * @throws Exception
     */
	public FilePropertiesMovie(String filename) throws Exception {
		boolean supported = false;
        RandomAccessFile ds = null;

		try {
		    
			ds = new RandomAccessFile(filename, "r");

			// gets the header for file type identification 
			int[] header = new int[4];
			for (int i = 0; i < header.length; i++)
				header[i] = ds.readUnsignedByte();

			// finds the right object
			int type = 0;
			for (type = 0; type < formats.length; type++) {
				if (Arrays.equals(header, MAGIC_BYTES[type])) {
					format = formats[type];
					format.process(ds, filename);
					format.setFilename(filename);
					supported = true;
				}
				
			}
				
			if (format == null) {
			    // no supported format found yet, check for mp4
		         if (FilePropertiesMP4.checkIfMp4File(ds)) {
		             format = new FilePropertiesMP4();
		             format.setFilename(filename);
		             format.process(ds, filename);
		             supported = true;
		         }
			}
			
		} catch (Exception e) {
			throw new Exception("File is corrupted. Some info may have been saved.");
		} finally {
		    if (ds != null) ds.close();
			if (!supported) throw new Exception("File format not supported.");
		}
	}
	
	public void close() {
	    for (FileProperties format : formats) {
	        format.close();
	    }
	}

	public int getVideoWidth() {
		try {
			String width = format.getVideoResolution().substring(0, format.getVideoResolution().indexOf("x"));
			return Integer.valueOf(width);
		} catch (Exception e) {
		    logger.debug("Error while determining the video width", e);
			return 0;
		}
	}

	public int getVideoHeight() {
		try {
			String height = 
			        format.getVideoResolution().substring(
			        format.getVideoResolution().indexOf("x") + 1,
			        format.getVideoResolution().length());
			return Integer.valueOf(height);
		} catch (Exception e) {
		    logger.debug("Error while determining the video height", e);
			return 0;
		}
	}

	public String getMetaDataTagInfo(String tag) {
	    List<String> metaData = format.getMetaData();
		if (metaData != null) {
			for (int i = 0; i < metaData.size(); i++) {
				String temp = metaData.get(i);
				if (temp.startsWith(tag))
					return temp.substring(tag.length() + 1, temp.length());
			}
		}
		return "";
	}
	
    public String getFilename() {
        return format.getFilename();
    }

    public String getLanguage() {
        return format.getLanguage();
    }

    public String getName() {
        return format.getName();
    }

    public String getSubtitles() {
        return format.getSubtitles();
    }

    /**
     * Returns the resolution.
     */
    public String getVideoResolution() {
        return format.getVideoResolution();
    }

    /**
     * Returns the video codec.
     */
    public String getVideoCodec() {
        return format.getVideoCodec();
    }

    /**
     * Returns the video rate.
     */
    public double getVideoRate() {
        return format.getVideoRate();
    }

    /**
     * Returns the video bit rate.
     */
    public int getVideoBitRate() {
        return format.getVideoBitRate();
    }

    /**
     * Returns the duration.
     */
    public int getDuration() {
        return format.getDuration();
    }

    /**
     * Returns the audio codec.
     */
    public String getAudioCodec() {
        return format.getAudioCodec();
    }

    /**
     * Returns the audio rate.
     */
    public int getAudioRate() {
        return format.getAudioRate();
    }

    /**
     * Returns the audio bit rate.
     */
    public int getAudioBitRate() {
        return format.getAudioBitRate();
    }

    /**
     * Returns the audio channels.
     */
    public int getAudioChannels() {
        return format.getAudioChannels();
    }
}
