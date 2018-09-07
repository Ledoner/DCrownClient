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
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

/**
 * Movie file representation.
 * 
 * @author Robert Jan van der Waals
 */
public class MovieFile {

    private static Logger logger = Logger.getLogger(MovieFile.class.getName());
    
    private RandomAccessFile raf;

    // tmp arrays to save information read form the avi file
    private byte[] baTemp = new byte[4];
    private int[] iaTemp = new int[4];

    // other temp vars
    private int HeaderSize;
    private int VideoHeaderSize;
    private int VideoHeaderStart;
    private int AudioHeaderSize;

    // vars holding the movie info
    private String movieName;
    private String movieFileName;
    private int movieSize;
    private int movieWidth;
    private int movieHeight;
    private double framesPerSec;
    private int totalFrames;
    private double playLength;
    private String videoCodec;
    private String audioCodec;
    private int audioBitRate;
    private int audioSamplingRate;
    private String audioChannel;
    private double videoBitRate;

    /**
     * Initializes the Movie File and parses the file for information
     * @param filename movie file to parse
     */
    public MovieFile(String file) {
        String filename = file;
        
        try {
            raf = new RandomAccessFile(filename, "r");
            // check whether its really an avi file
            read_str(2);
            File oFile = new File(filename);
            filename = oFile.getName();
            movieFileName = filename;
            movieName = filename.substring(0, filename.indexOf("."));
            movieSize = read_4int(1);
            HeaderSize = read_4int(7);
            VideoHeaderStart = (HeaderSize / 4) + 7;
            VideoHeaderSize = read_4int(VideoHeaderStart + 2);
            int ahstart = (HeaderSize / 4) + (VideoHeaderSize / 4) + 15;
            AudioHeaderSize = read_4int(ahstart - 1);
            movieWidth = read_4int(16);
            movieHeight = read_4int(17);
            framesPerSec = 1000000 / read_4int(8);
            totalFrames = read_4int(VideoHeaderStart + 14);
            playLength = totalFrames / framesPerSec;
            String VCODEC0 = read_str((HeaderSize / 4) + 14);
            if (VCODEC0.equalsIgnoreCase("div3")) {
                videoCodec = "divx v3 (low motion)";
            } else if (VCODEC0.equalsIgnoreCase("div4")) {
                videoCodec = "divx v3 (fast motion)";
            } else if (VCODEC0.equalsIgnoreCase("divx")) {
                videoCodec = "divx v4 (opendivx)";
            } else if (VCODEC0.equalsIgnoreCase("dx50")) {
                videoCodec = "divx v5";
            } else if (VCODEC0.equalsIgnoreCase("xvid")) {
                videoCodec = "xvid";
            } else if (VCODEC0.equalsIgnoreCase("rmp4")) {
                videoCodec = "realmagic mpeg4";
            } else if (VCODEC0.equalsIgnoreCase("3ivx")) {
                videoCodec = "divx-alike";
            } else if (VCODEC0.equalsIgnoreCase("3iv2")) {
                videoCodec = "divx-alike";
            } else if (VCODEC0.equalsIgnoreCase("div2")) {
                videoCodec = "ms mpeg4 v2";
            } else if (VCODEC0.equalsIgnoreCase("mp43")) {
                videoCodec = "ms mpeg4 v3";
            } else if (VCODEC0.equalsIgnoreCase("mp41")) {
                videoCodec = "ms mpeg4 v1";
            } else if (VCODEC0.equalsIgnoreCase("mp42")) {
                videoCodec = "ms mpeg4 v2";
            } else {
                videoCodec = VCODEC0;
            }

            int ACODEC0 = read_2int(ahstart + (AudioHeaderSize / 4) + 2, 0);
            if ((ACODEC0 == 0) || (ACODEC0 == 1)) {
                audioCodec = "pcm";
            } else if (ACODEC0 == 85) {
                audioCodec = "mp3";
            } else if (ACODEC0 == 353) {
                audioCodec = "divx-audio";
            } else if (ACODEC0 == 8192) {
                audioCodec = "ac3";
            } else {
                audioCodec = Integer.toString(ACODEC0);
            }

            int ABITRATE0 = read_2int(ahstart + (AudioHeaderSize / 4) + 4, 0);
            
            if (ABITRATE0 > 0) {
                audioBitRate = ABITRATE0 * 8;
                videoBitRate = ((movieSize / playLength) - ABITRATE0) * 8;
            }
                
            audioSamplingRate = read_2int(ahstart + (AudioHeaderSize / 4) + 3, 0);
            int ACHAN0 = read_2int(ahstart + (AudioHeaderSize / 4) + 2, 2);
            if (ACHAN0 == 1) {
                audioChannel = "1 mono";
            } else if (ACHAN0 == 2) {
                audioChannel = "2 stereo";
            } else if (ACHAN0 == 5) {
                audioChannel = "5 surround";
            } else {
                audioChannel = Integer.toString(ACHAN0);
            }
        } catch (IOException e) {
            logger.error("Error while parsing the movie file " + filename, e);
        } finally {
            try { raf.close(); } catch(Exception e) {
                logger.debug("Error while closing RandomAccessFile", e);
            }
        }
    }

    public String getName() {
        return movieName;
    }

    public String getFileName() {
        return movieFileName;
    }

    public int getSize() {
        return movieSize;
    }

    public int getHeight() {
        return movieHeight;
    }

    public int getWidth() {
        return movieWidth;
    }

    public double getFPS() {
        return framesPerSec;
    }

    public int getFrames() {
        return totalFrames;
    }

    public double getPlayLength() {
        return playLength;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public int getAudioBitRate() {
        return audioBitRate;
    }

    public int getAudioSamplingRate() {
        return audioSamplingRate;
    }

    public String getAudioChannel() {
        return audioChannel;
    }

    public double getVideoBitrate() {
        return videoBitRate;
    }

    /**
     * Reads a word and return it as string
     *
     * @param pos startposition for reading
     * @return the read string
     */
    public String read_str(int pos) {
        String retval = "";
        try {
            raf.seek(pos * 4);
            raf.read(baTemp);
            retval = String.valueOf(baTemp);
        } catch (IOException err_msg) {
        }
        return (retval);
    }

    /**
     * Reads a word and return it as an int
     *
     * @param pos startposition for reading
     * @return the read int
     */
    public int read_4int(int pos) {
        int retval = -1;
        try {
            raf.seek(pos * 4);
            iaTemp[3] = raf.readUnsignedByte();
            iaTemp[2] = raf.readUnsignedByte();
            iaTemp[1] = raf.readUnsignedByte();
            iaTemp[0] = raf.readUnsignedByte();
            retval =
                (iaTemp[0] * 16777216)
                    + (iaTemp[1] * 65536)
                    + (iaTemp[2] * 256)
                    + iaTemp[3];
        } catch (IOException err_msg) {
        }
        return (retval);
    }

    /**
     * Reads 2 bytes and return it as int
     *
     * @param pos startposition for reading
     * @return the read string
     */
    public int read_2int(int pos, int off) {

        int retval = -1;
        try {
            raf.seek((pos * 4) + off);
            iaTemp[1] = raf.readUnsignedByte();
            iaTemp[0] = raf.readUnsignedByte();
            retval = (iaTemp[0] * 256) + iaTemp[1];
        } catch (IOException err_msg) {
        }
        return (retval);
    }
}