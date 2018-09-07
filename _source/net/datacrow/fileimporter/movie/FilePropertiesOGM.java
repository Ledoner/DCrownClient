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
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

class FilePropertiesOGM extends FileProperties {

    private static Logger logger = Logger.getLogger(FilePropertiesOGM.class.getName());
    
    private final int End = 0x0373;
    private final int vide = 0x65646976;
    private final int audi = 0x69647561;
    private final int vorb = 0x62726f76;
    private final int text = 0x74786574;
    private final int OggS = 0x5367674f;
    private final int ffff = 0xffffffff;

    private float frameRate;
    
    /**
     * Processes a file from the given RandomAccessFile.
     */
    @Override
    protected void process(RandomAccessFile dataStream, String filename) throws Exception {
        dataStream.seek(0);

        getInfo(dataStream);
        setDuration(getDuration(dataStream));

        calculateVideoBitRate(dataStream.length());
        setContainer("OGM");
    }

    private void getInfo(RandomAccessFile dataStream) throws Exception {

        int videoFccHandler;
        int audioFccHandler;
        int type;

        int twoByteCheck;

        String audioCodecs = "";
        int audioSampleRate = 0;
        int audioBitRate = 0;
        int audioChannels = 0;
        int safety = 0;
        boolean quit = false;
        int streamCounter = 0; // Counts audio and subtitle streams 

        while (!quit && safety++ < 15000) {

            if (       readUnsignedByte(dataStream) == 0x4f
                    && readUnsignedByte(dataStream) == 0x67
                    && readUnsignedByte(dataStream) == 0x67
                    && readUnsignedByte(dataStream) == 0x53) {

                skipBytes(dataStream, 1);

                if (readUnsignedByte(dataStream) == 2) {

                    readUnsignedInt32(dataStream);
                    skipBytes(dataStream, 4);
                    readUnsignedByte(dataStream);

                    skipBytes(dataStream, 12);

                    twoByteCheck = readUnsignedInt16(dataStream);
                    type = readUnsignedInt32(dataStream);

                    if (twoByteCheck == End) {
                        break;
                    }

                    // Video info 
                    if (type == vide) {
                        skipBytes(dataStream, 4);
                        videoFccHandler = readUnsignedInt32(dataStream);                        
                        String videoCodec = findName(fromByteToAscii(videoFccHandler, 4), FileProperties._TYPE_VIDEO_CODEC);
                        
                        setVideoCodec(videoCodec);

                        skipBytes(dataStream, 4);

                        frameRate = ((float) 10000000 / (float) readUnsignedInt32(dataStream)) * 1000;
                        frameRate = (float) ((Math.ceil(frameRate)) / 1000);

                        setVideoRate((int) frameRate);
                        skipBytes(dataStream, 24);

                        int dwWidth = readUnsignedInt32(dataStream);
                        int dwHeight = readUnsignedInt32(dataStream);

                        setVideoResolution(dwWidth + "x" + dwHeight);
                    }

                    // Audio info
                    if (type == audi) {
                        streamCounter++;

                        skipBytes(dataStream, 4);
                        audioFccHandler = readUnsignedInt32(dataStream);

                        if (!audioCodecs.equals(""))
                            audioCodecs += ", ";
                        
                        audioCodecs += findName("0x" + fromByteToAscii(audioFccHandler, 4), FileProperties._TYPE_AUDIO_CODEC);
                        
                        skipBytes(dataStream, 12);
                        float sampleRate = readUnsignedInt32(dataStream);

                        audioSampleRate += (int) sampleRate;
                        skipBytes(dataStream, 16);

                        int channels = readUnsignedByte(dataStream);
                        audioChannels = channels;

                        skipBytes(dataStream, 3);

                        int bitRate = readUnsignedInt16(dataStream) / 1000;

                        audioBitRate = bitRate * 8;
                    }

                    if (type == vorb) {
                        streamCounter++;
                        if (!audioCodecs.equals("")) {
                            audioCodecs += ", ";
                        }
                        
                        audioCodecs += "Vorbis";
                        skipBytes(dataStream, 6);
                        audioChannels = readUnsignedByte(dataStream);
                        float sampleRate = readUnsignedInt16(dataStream);
                        audioSampleRate = (int) sampleRate;

                        skipBytes(dataStream, 6);

                        audioBitRate = readUnsignedInt32(dataStream) / 1000;
                    }

                    // Subtitles
                    if (type == text) {
                        streamCounter++;
                    }
                } else {
                    skipBytes(dataStream, 8);
                    type = readUnsignedByte(dataStream);
                    skipBytes(dataStream, 14);
                    int streamType = readUnsignedInt32(dataStream);

                    if (type != 0) {
                        // Subtitles 
                        if (streamType == vorb) {
                            String subtitles = getSubtitles();

                            if (!subtitles.equals("")) {
                                subtitles += ", ";
                            }

                            subtitles += getSubtitle(dataStream);
                            setSubtitles(subtitles);
                            streamCounter--;
                        } else if (streamType == ffff) { // Audio
                            streamCounter--;
                        }
                    } else {
                        if (streamCounter == 0) {
                            setExtendedCodecInfo(dataStream, 100);
                            quit = true;
                        }
                    }
                }
            }
        }

        setAudioCodec(audioCodecs);
        setAudioRate(audioSampleRate);
        setAudioBitRate(audioBitRate);
        setAudioChannels(audioChannels);
    }

    protected String getSubtitle(RandomAccessFile dataStream) {
        try {
            int counter = 0;
            while (counter++ < 100) {
                int temp = readUnsignedByte(dataStream);
                if (temp == 0x4c && readUnsignedByte(dataStream) == 0x41
                                 && readUnsignedByte(dataStream) == 0x4e
                                 && readUnsignedByte(dataStream) == 0x47) {
                    skipBytes(dataStream, 5);

                    temp = readUnsignedByte(dataStream);

                    String subtitle = "";
                    while (temp != 0x01 && temp != 0x5b) {
                        subtitle += fromByteToAscii(temp, 1);
                        temp = readUnsignedByte(dataStream);
                    }
                    return subtitle;
                } else if (readUnsignedByte(dataStream) == 0x4f
                        && readUnsignedByte(dataStream) == 0x67
                        && readUnsignedByte(dataStream) == 0x67
                        && readUnsignedByte(dataStream) == 0x53) {

                    dataStream.seek(dataStream.getFilePointer() - 4);
                    return "";
                }
            }
        } catch (Exception e) {
            logger.error("Unable to determine subtitle", e);
        }
        return "";
    }

    /*
     * Calculates the size of the video only and then calculates the
     * videoBitRate/kbps.
     */
    private void calculateVideoBitRate(long fileSize) {
        int audioSize = 0;
        getDuration();
        int audioBitRate = 0;

        StringTokenizer string = new StringTokenizer("" + getAudioBitRate(), ", ");

        while (string.hasMoreTokens()) {
            // audio bit rate in kbit/s
            audioBitRate = Integer.parseInt(string.nextToken()); 
            audioSize += ((audioBitRate * getDuration()) / 8) * 1000;
        }
        // video rate kbit/s
        int videoBitRate = ((int) ((fileSize - audioSize) / getDuration()) / 1000) * 8; 
        setVideoBitRate(videoBitRate);
    }

    /**
     * Starts at the end of the file and finds the first OggS (Video) and
     * returns
     */
    private int getDuration(RandomAccessFile dataStream) {

        boolean found = false;
        int read;
        long length;
        int duration = 0;

        try {
            // starts at end
            length = dataStream.length() - 4;

            while (!found) {
                dataStream.seek(length--);
                read = readUnsignedInt32(dataStream);

                if (read == OggS) {
                    skipBytes(dataStream, 2);

                    duration = readUnsignedInt32(dataStream);
                    skipBytes(dataStream, 4);

                    if (readUnsignedByte(dataStream) == 0) /* 0 for video */
                        found = true;
                }
            }
        }

        catch (Exception e) {
            logger.error("Unable to determine duration", e);
            return 0;
        }

        return (int) (duration / frameRate);
    }

    private void setExtendedCodecInfo(RandomAccessFile dataStream, int iLimit) throws Exception {

        int limit = iLimit;
        int temp;
        String extendedInfo = "";

        while (limit-- > 0) {
            temp = readUnsignedByte(dataStream);

            if (Integer.toHexString(temp).equals("44") || Integer.toHexString(temp).equals("58")) {
                extendedInfo = "";
                extendedInfo += fromByteToAscii(temp, 1);

                for (int u = 0; u < 3; u++) {
                    temp = readUnsignedByte(dataStream);
                    extendedInfo += fromByteToAscii(temp, 1);
                }

                if ((extendedInfo.toLowerCase().equals("divx")) || (extendedInfo.toLowerCase().equals("xvid"))) {

                    for (int a = 0; a < 100; a++) {
                        temp = readUnsignedByte(dataStream);

                        if (temp == 0)
                            break;

                        extendedInfo += fromByteToAscii(temp, 1);
                    }

                    // If last character is not a digit it is removed
                    for (int i = 0; i < extendedInfo.length(); i++) {
                        if (!Character.isDigit(extendedInfo.charAt(extendedInfo.length() - 1))) {
                            if (extendedInfo.charAt(extendedInfo.length() - 1) == 'p')
                                extendedInfo = extendedInfo.substring(0, (extendedInfo.length() - 1));
                        } else {
                            break;
                        }
                    }

                    // Replaces "Build" with "b" if it occurs
                    if ((extendedInfo.toLowerCase().startsWith("divx")) && extendedInfo.length() > 12) {
                        if (extendedInfo.substring(7, 12).equals("Build"))
                            extendedInfo = extendedInfo.replaceFirst("Build", "b");
                    }

                    String codecName = findName(extendedInfo, FileProperties._TYPE_VIDEO_CODEC_EXT);
                    if (!codecName.equals("")) {
                        setVideoCodec(codecName);
                        return;
                    }
                }
            }
        }
    }
}
