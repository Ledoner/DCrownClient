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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.StringTokenizer;

import net.datacrow.core.DcConfig;

import org.apache.log4j.Logger;

abstract class FileProperties {
    
    private static Logger logger = Logger.getLogger(FileProperties.class.getName());

    public static final int _TYPE_AUDIO_CODEC = 0;
    public static final int _TYPE_VIDEO_CODEC = 1;
    public static final int _TYPE_VIDEO_CODEC_EXT = 2;
    
    private String filename = "";
    private String language = "";
    private String name = "";
    private String subtitles = "";
    private String videoResolution = "";
    private String videoCodec = "";
    private double videoRate = 0;
    private int videoBitRate = 0;
    private int duration = -1;
    private String audioCodec = "";
    private int audioRate = 0;
    private int audioBitRate = 0;
    private int audioChannels = 0;
    private String container = "";
    private String mediaType = "";
    
    private List<String> metaData;
    
    private BufferedReader rdrVideo;
    private BufferedReader rdrVideoExt;
    private BufferedReader rdrAudio;

    public FileProperties() {
        try {
            InputStreamReader isrVideo = new InputStreamReader(new FileInputStream(
                    new File(DcConfig.getInstance().getResourcesDir(), "FOURCCvideo.txt")));
            rdrVideo = new BufferedReader(isrVideo);
        } catch (Exception e) {
            logger.error(e, e);
        }
        
        try {
            InputStreamReader isrAudio = new InputStreamReader(new FileInputStream(
                    new File(DcConfig.getInstance().getResourcesDir(), "FOURCCaudio.txt")));
            rdrAudio = new BufferedReader(isrAudio);
        } catch (Exception e) {
            logger.error(e, e);
        }
        
        try {
            InputStreamReader isrVideoExt = new InputStreamReader(new FileInputStream(
                    new File(DcConfig.getInstance().getResourcesDir(), "videoExtended.txt")));
            rdrVideoExt = new BufferedReader(isrVideoExt);
        } catch (Exception e) {
            logger.error(e, e);
        }            
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the subtitles.
     */
    public String getSubtitles() {
        return subtitles;
    }

    /**
     * Returns the resolution.
     */
    public String getVideoResolution() {
        return videoResolution;
    }

    /**
     * Returns the video codec.
     */
    public String getVideoCodec() {
        return videoCodec;
    }

    /**
     * Returns the video rate.
     */
    public double getVideoRate() {
        return videoRate;
    }

    /**
     * Returns the video bit rate.
     */
    public int getVideoBitRate() {
        return videoBitRate;
    }

    /**
     * Returns the duration.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the audio codec.
     */
    public String getAudioCodec() {
        return audioCodec;
    }

    /**
     * Returns the audio rate.
     */
    public int getAudioRate() {
        return audioRate;
    }

    /**
     * Returns the audio bit rate.
     */
    public int getAudioBitRate() {
        return audioBitRate;
    }

    /**
     * Returns the audio channels.
     */
    public int getAudioChannels() {
        return audioChannels;
    }

    /**
     * Sets the subtitles.
     */
    protected void setSubtitles(String subtitles) {
        this.subtitles = subtitles;
    }

    /**
     * Sets the resolution.
     */
    protected void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    /**
     * Sets the video codec (video handler).
     */
    protected void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    /**
     * Sets the video rate.
     */
    protected void setVideoRate(double videoRate) {
        this.videoRate = videoRate;
    }

    /**
     * Sets the video bit rate.
     */
    protected void setVideoBitRate(int videoBitRate) {
        this.videoBitRate = videoBitRate;
    }

    /**
     * Sets the duration.
     */
    protected void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Sets the audio Codec (auda handler).
     */
    protected void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    /**
     * Sets the audio rate.
     */
    protected void setAudioRate(int audioRate) {
        this.audioRate = audioRate;
    }

    /**
     * Sets the audio bit rate.
     */
    protected void setAudioBitRate(int audioBitRate) {
        this.audioBitRate = audioBitRate;
    }

    /**
     * Sets the audio channels.
     */
    protected void setAudioChannels(int audioChannels) {
        this.audioChannels = audioChannels;
    }

    /**
     * Sets the container.
     */
    protected void setContainer(String container) {
        this.container = container;
    }

    /**
     * Returns the container.
     */
    protected String getContainer() {
        return this.container;
    }

    /**
     * Sets the Media Type.
     */
    protected void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Returns the Media Type.
     */
    protected String getMediaType() {
        return this.mediaType;
    }

    /**
     * Sets the meta data ArrayList.
     */
    protected void setMetaData(List<String> metaData) {
        this.metaData = metaData;
    }

    /**
     * Returns the meta data ArrayList.
     */
    protected List<String> getMetaData() {
        return metaData;
    }

    /**
     * Processes a file from the given DataInputStream.
     */
    protected void process(RandomAccessFile dataStream, String filename) throws Exception {}

    /**
     * Reads an unsigned 8-bit integer.
     */
    protected int readUnsignedByte(byte[] b, int offset) throws Exception {
        return b[offset];
    }

    /**
     * Reads an unsigned 16-bit integer.
     */
    protected int readUnsignedInt16(byte[] b, int offset) throws Exception {
        return (b[offset] | (b[offset + 1] << 8));
    }

    /**
     * Reads an unsigned 32-bit integer.
     */
    protected int readUnsignedInt32(byte[] b, int offset) throws Exception {
        return (readUnsignedInt16(b, offset) | (readUnsignedInt16(b, offset + 2) << 16));
    }

    /**
     * Returns a 16-bit integer.
     */
    protected int getUnsignedInt16(int byte1, int byte2) throws Exception {
        return (byte2 | (byte1 << 8));
    }

    /**
     * Returns a 16-bit integer.
     */
    protected int getUnsignedInt16(byte byte1, byte byte2) throws Exception {
        return byte2 | byte1 << 8;
    }

    /**
     * Returns an unsigned 32-bit integer.
     */
    protected int getUnsignedInt32(byte byte1, byte byte2) throws Exception {
        return byte2 | byte1 << 16;
    }

    /**
     * Returns an unsigned 32-bit integer.
     */
    protected int getUnsignedInt32(int byte1, int byte2) throws Exception {
        return (byte1 | byte2 << 16);
    }

    /**
     * Reads an unsigned byte and returns its int representation.
     */
    protected int readUnsignedByte(RandomAccessFile dataStream) throws Exception {
        int data = dataStream.readUnsignedByte();
        if (data == -1) {
            throw new Exception("Unexpected end of stream.");
        }
        return data;
    }

    /**
     * Reads n unsigned bytes and returns it in an int[n].
     */
    protected int[] readUnsignedBytes(RandomAccessFile dataStream, int n) throws Exception {
        int[] data = new int[n];
        for (int i = 0; i < data.length; i++) {
            data[i] = readUnsignedByte(dataStream);
        }
        return data;
    }

    /**
     * Reads an unsigned 16-bit integer.
     */
    protected int readUnsignedInt16(RandomAccessFile dataStream) throws Exception {
        return (readUnsignedByte(dataStream) | (readUnsignedByte(dataStream) << 8));
    }

    /**
     * Reads an unsigned 32-bit integer.
     */
    protected int readUnsignedInt32(RandomAccessFile dataStream) throws Exception {
        return (readUnsignedInt16(dataStream) | (readUnsignedInt16(dataStream) << 16));
    }

    /**
     * Discards n bytes.
     */
    protected void skipBytes(RandomAccessFile dataStream, int n) throws Exception {
        readUnsignedBytes(dataStream, n);
    }

    /**
     * Reverses the byte order
     */
    protected int changeEndianness(int num) {
        return (num >>> 24) | (num << 24) | ((num << 8) & 0x00FF0000 | ((num >> 8) & 0x0000FF00));
    }

    /**
     * Returns the ASCII value of id
     */
    protected String fromByteToAscii(int j, int numberOfBytes) throws Exception {
        int id = j;
        StringBuffer buffer = new StringBuffer(4);

        for (int i = 0; i < numberOfBytes; i++) {
            int c = id & 0xff;
            buffer.append((char) c);
            id >>= 8;
        }
        return new String(buffer);
    }

    /**
     * Returns the decimal value of a specified number of bytes from a specific
     * part of a byte.
     */
    protected int getDecimalValue(int[] bits, int start, int stop, boolean printBits) {
        String dec = "";
        for (int i = start; i >= stop; i--) {
            dec += bits[i];
        }

        return Integer.parseInt(dec, 2);
    }

    /**
     * Returns an array containing the bits from the value.
     */
    protected int[] getBits(int value, int numberOfBytes) {

        int[] bits = new int[numberOfBytes * 8];

        for (int i = bits.length - 1; i >= 0; i--) {
            bits[i] = (value >>> i) & 1;
        }
        return bits;
    }
    
    public void close() {
        if (metaData != null) metaData.clear();
        if (rdrVideo != null) { 
            try { 
                rdrVideo.close();
            } catch (Exception e) {
                logger.debug("Failed to close file", e);
            }
        }
        
        if (rdrVideoExt != null) { 
            try { 
                rdrVideoExt.close();
            } catch (Exception e) {
                logger.debug("Failed to close file", e);
            }
        }
        
        if (rdrAudio != null) { 
            try { 
                rdrAudio.close();
            } catch (Exception e) {
                logger.debug("Failed to close file", e);
            }
        }
    }

    /**
     * Searches in the inputStream stream the name following the string id
     * (separated by a \t).
     */
           
    @SuppressWarnings("resource")
    protected String findName(String id, int type) throws Exception {
        String line = null;
        String result = "";
        
        // do not close rdr, reused throughout the session
        BufferedReader rdr = 
                type == _TYPE_AUDIO_CODEC ? rdrAudio :
                type == _TYPE_VIDEO_CODEC ? rdrVideo : rdrVideoExt;
        
        if (rdr == null) {
            logger.error("No valid resource file found for codec identification");
        } else {
            while ((line = rdr.readLine()) != null) {
                if (line.length() > 0) {
                    StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                    if (    tokenizer.countTokens() > 0 &&
                            id.compareToIgnoreCase(tokenizer.nextToken()) == 0) {
                        result = tokenizer.nextToken();
                        break;
                    }
                }
            }
        }
        
        return result;
    }
}
