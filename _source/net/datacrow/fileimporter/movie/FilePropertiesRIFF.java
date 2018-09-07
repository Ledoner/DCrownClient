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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class FilePropertiesRIFF extends FileProperties {

    private final static int RIFF_AVI = 0x20495641;
    private final static int AVI_LIST = 0x5453494c;
    private final static int AVI_movi = 0x69766f6d;
    private final static int AVI_avih = 0x68697661;
    private final static int AVI_avih_SIZE = 0x00000038;
    private final static int AVI_strh = 0x68727473;
    private final static int AVI_vids = 0x73646976;
    private final static int AVI_auds = 0x73647561;//61756473
    private final static int AVI_strf = 0x66727473;
    private final static int AVI_INFO = 0x4f464e49;
    private int fccHandler1;

    private boolean checkForSpecifiedCodecInfo = false;
    private boolean extendedCodecInfoFound = false;
    private int extendedCodecInfoChunkCounter = 0;
    private boolean header = true;
    private int lastSubChunk = 0;
    private long videoAudioStreamSize = 0;

    private boolean quit = false;

    private String audioCodec = "";
    private int audioChannels = 0;
    private int audioRate = 0;
    private int audioBitRate = 0;

    /**
     * Processes a file from the given DataInputStream.
     */
    @Override
    protected void process(RandomAccessFile dataStream, String filename) throws Exception {
        dataStream.seek(4);

        //Gets the stream size... (4 bytes)
        int streamSize = readUnsignedInt32(dataStream);
        // Gets the stream type... (4 bytes)
        int streamType = readUnsignedInt32(dataStream);

        if (streamType == RIFF_AVI) {
            // Processes the AVI chunks...
            processAviChunks(dataStream, streamSize);

            setAudioCodec(audioCodec);
            setAudioChannels(audioChannels);
            setAudioRate(audioRate);
            setAudioBitRate(audioBitRate);
            calculateVideoBitRate();
            setContainer("AVI");
        }
    }

    /**
     * Processes n bytes of the AVI chunk.
     */
    private void processAviChunks(RandomAccessFile dataStream, int o) throws Exception {
        int n = o;
        int chunkType;
        int chunkSize;
        int safety = 0;

        while (n > 0 && !quit && safety++ < 1000) {
            chunkType = readUnsignedInt32(dataStream);
            n -= 4;
            chunkSize = readUnsignedInt32(dataStream);
            n -= 4;
            
            // processes this chunk...
            n -= chunkSize;

            if (header) {
                switch (chunkType) {
                case (AVI_LIST): {
                    chunkType = readUnsignedInt32(dataStream);
                    // If AVI_movi, the header is finished 
                    if (chunkType == AVI_movi) {
                        // a very aproximate test to check if the AVI_movi chunk
                        // size isn't wrong
                        if (    chunkSize < (dataStream.length() * 0.7) || 
                                chunkSize > dataStream.length()) {
                            videoAudioStreamSize = dataStream.length();
                        } else {
                            videoAudioStreamSize = chunkSize;
                        }
                        header = false;
                    } else if (chunkType == AVI_INFO) {
                        chunkSize = correctChunkSize(chunkSize);
                        processMetaTags(dataStream, chunkSize - 4);
                    } else {
                        processAviChunks(dataStream, chunkSize - 4);
                    }
                    break;
                    
                } case (AVI_avih): {
                    if (chunkSize != AVI_avih_SIZE) {
                        throw new Exception( "RIFF file corrupted (avih chunk size is "
                                             + chunkSize + " and not 0x38 as expected).");
                    }
                    processAviAvih(dataStream, chunkSize);
                    break;
                }

                case (AVI_strh): {
                    // Get the sub chunk type...
                    int subChunk = readUnsignedInt32(dataStream);
                    switch (subChunk) {
                    case (AVI_vids): {
                        processAviVids(dataStream, chunkSize - 4);
                        lastSubChunk = AVI_strf;
                        break;
                    }
                    case (AVI_auds): {
                        // Discards... 
                        skipBytes(dataStream, chunkSize - 4);
                        lastSubChunk = AVI_auds;
                        break;
                    }
                    default: {
                        // Discards... 
                        skipBytes(dataStream, chunkSize - 4);
                        break;
                    }
                    }
                    break;
                    
                } case (AVI_strf): {
                    switch (lastSubChunk) {
                        case (AVI_strf): 
                            processAviCodec(dataStream, chunkSize);
                            break;
                        case (AVI_auds): 
                            processAviSound(dataStream, chunkSize);
                            break;
                        default: 
                            // Discards...
                            skipBytes(dataStream, chunkSize);
                            break;
                    }
                    lastSubChunk = 0;
                    break;
                    
                } default: 
                    chunkSize = correctChunkSize(chunkSize);
                    skipBytes(dataStream, chunkSize);
                    break;
                }
                
            } else if (checkForSpecifiedCodecInfo) {
                extendedCodecInfoChunkCounter++;

                 // 65536 is an approximate value to prevent the whole file from being parsed
                if (chunkSize < 0 || dataStream.getFilePointer() > 100000) { //65536)
                    quit = true;
                }
                chunkSize = correctChunkSize(chunkSize);

                 // if codec is either xvid or DivX5 the extended codec info will
                 // be extracted from the video stream.
                if (!quit) {
                     // Sometimes the chunk size is so huge it uses many minutes
                     // to process, usually a sign of no useful info available
                    if (chunkSize > 50000) {
                        if (extendedCodecInfoChunkCounter > 60) {
                            quit = true;
                        }
                    } else {
                        getExtendedCodecInfo(dataStream, chunkSize);
                    }

                    if (extendedCodecInfoFound) {
                        quit = true;
                    }
                } else {
                    quit = true;
                }
            } else {
                quit = true;
            }
        }
    }

    /**
     * Processes the AVI avih chunk.
     */
    private void processAviAvih(RandomAccessFile dataStream, int chunkSize)
            throws Exception {
        int dwMicroSecPerFrame = readUnsignedInt32(dataStream);
        readUnsignedInt32(dataStream);
        // Skips unwanted info (discarded)...
        skipBytes(dataStream, 8);
        int dwTotalFrames = readUnsignedInt32(dataStream);
        skipBytes(dataStream, 12);
        int dwWidth = readUnsignedInt32(dataStream);
        int dwHeight = readUnsignedInt32(dataStream);
        skipBytes(dataStream, chunkSize - 40);

        if (dwMicroSecPerFrame > 0) {
            setDuration(Math.round((dwTotalFrames / 1000F)
                    * (dwMicroSecPerFrame / 1000F)));
        }
        
        setVideoResolution(dwWidth + "x" + dwHeight);
    }

    /**
     * Processes of avi_vids sub chunk.
     */
    private void processAviVids(RandomAccessFile dataStream, int chunkSize)
            throws Exception {

        fccHandler1 = readUnsignedInt32(dataStream);

        skipBytes(dataStream, 12);
        int dwScale = readUnsignedInt32(dataStream);
        int dwRate = readUnsignedInt32(dataStream);
        skipBytes(dataStream, chunkSize - 24);

        setVideoRate(dwRate / dwScale);
    }

    /**
     * Processes of avi_strf on the same sub chuck that avi_auds.
     */
    private void processAviSound(RandomAccessFile dataStream, int chunkSize)
            throws Exception {

        int wFormatTag = readUnsignedInt16(dataStream);
        audioChannels = readUnsignedInt16(dataStream);
        audioRate = readUnsignedInt32(dataStream);
        audioBitRate = readUnsignedInt32(dataStream);
        skipBytes(dataStream, chunkSize - 12);

        if (!audioCodec.equals("")) {
            audioCodec += ", ";
        }
        audioCodec += getAudioCodecName(wFormatTag);

        audioBitRate = Math.round((audioBitRate) * 8F / 1000F);
    }

    /**
     * Gets the audio codec name from file based on the id.
     */
    private String getAudioCodecName(int id) throws Exception {
        /* Transforms the id in a string... */
        StringBuffer buffer = new StringBuffer("0x");
        String value = Integer.toHexString(id);
        int i = 4 - value.length();
        while (i-- > 0) {
            buffer.append('0');
        }
        
        buffer.append(value);

        return findName(new String(buffer), FileProperties._TYPE_AUDIO_CODEC);
    }

    private void calculateVideoBitRate() {
        int audioSize = 0;
        int audioBitRate = 0;

        StringTokenizer string = new StringTokenizer("" + getAudioBitRate(), ", ");
        while (string.hasMoreTokens()) {
            audioBitRate = audioBitRate + Integer.parseInt(string.nextToken()); 
            audioSize += ((audioBitRate * getDuration()) / 8) * 1000;
        }

        int videoBitRate = ((int) ((videoAudioStreamSize - audioSize) / getDuration()) / 1000) * 8; 
        setVideoBitRate(videoBitRate);
    }

    private void processAviCodec(RandomAccessFile dataStream, int chunkSize) throws Exception {
        int fccHandler;
        int fccHandler2;

        skipBytes(dataStream, 16);

        fccHandler2 = readUnsignedInt32(dataStream);
        fccHandler = fccHandler2;

        skipBytes(dataStream, chunkSize - 20);
         // DivX 3 Low and High motion is identified by the first fourcc. Other
         // codecs is identified by the second fourcc code
        if (    (fromByteToAscii(fccHandler1, 4).toUpperCase().equals("DIV3")) ||
                (fromByteToAscii(fccHandler1, 4).toUpperCase() .equals("DIV4"))) {
            fccHandler = fccHandler1;
        }

        if (    (fromByteToAscii(fccHandler2, 4).toUpperCase().equals("DX50")) ||
                (fromByteToAscii(fccHandler2, 4).toUpperCase().equals("XVID"))) {
            checkForSpecifiedCodecInfo = true;
        }
        
        if (fccHandler == 0 && fccHandler1 != 0) {
            fccHandler = fccHandler1;
        }

        String codecName = fromByteToAscii(fccHandler, 4).toUpperCase();
        codecName = findName(codecName, FileProperties._TYPE_VIDEO_CODEC);

        setVideoCodec(codecName);
    }

    private void getExtendedCodecInfo(RandomAccessFile dataStream, int iChunkSize) throws Exception {
        
        int chunkSize = iChunkSize;
        int temp;
        String extendedInfo = "";

        while (chunkSize > 0) {
            temp = readUnsignedByte(dataStream);
            chunkSize--;

            // 44 == D, 58 == X 
            if (Integer.toHexString(temp).equals("44") || Integer.toHexString(temp).equals("58")) {
                extendedInfo = "";
                extendedInfo += fromByteToAscii(temp, 1);

                for (int u = 0; u < 3; u++) {
                    if (chunkSize == 0) {
                        return;
                    }
                    temp = readUnsignedByte(dataStream);
                    chunkSize--;
                    extendedInfo += fromByteToAscii(temp, 1);
                }

                if (    (extendedInfo.toLowerCase().equals("divx")) || 
                        (extendedInfo.toLowerCase().equals("xvid"))) {

                    for (int a = 0; a < 100; a++) {
                        temp = readUnsignedByte(dataStream);
                        chunkSize--;

                        if (temp == 0) {
                            break;
                        }

                        if (chunkSize == 0) {
                            return;
                        }
                        extendedInfo += fromByteToAscii(temp, 1);
                    }

                    // If last character is not a digit it is removed
                    for (int i = 0; i < extendedInfo.length(); i++) {
                        if (!Character.isDigit(extendedInfo.charAt(extendedInfo.length() - 1))) {
                            if (extendedInfo.charAt(extendedInfo.length() - 1) == 'p') {
                                extendedInfo = extendedInfo.substring(0, (extendedInfo.length() - 1));
                            }
                        } else
                            break;
                    }

                    // Replaces "Build" with "b" if it occurs
                    if (    (extendedInfo.toLowerCase().startsWith("divx")) &&
                             extendedInfo.length() > 12) {
                        if (extendedInfo.substring(7, 12).equals("Build")) {
                            extendedInfo = extendedInfo.replaceFirst("Build", "b");
                        }
                    }

                    String codecName = findName(extendedInfo, FileProperties._TYPE_VIDEO_CODEC_EXT);
                    if (!codecName.equals("")) {
                        setVideoCodec(codecName);
                        extendedCodecInfoFound = true;
                        skipBytes(dataStream, chunkSize);
                        return;
                    }
                }
            }
        }
    }

    public int correctChunkSize(int iChunkSize) {
        int chunkSize = iChunkSize;
        int m = chunkSize % 2;
        if (m != 0)
            chunkSize += (2 - m);

        return chunkSize;
    }

    public void processMetaTags(RandomAccessFile dataStream, int iChunkSize) throws Exception {
        int chunkSize = iChunkSize;
        final int JUNK = 0x4b4e554a; //Produced By
        String metaTagInfo = "";
        List<String> metaData = new ArrayList<String>();

        int metaChunkType;
        int metaChunkSize;

        while (chunkSize > 0) {
            metaTagInfo = "";
            metaChunkType = readUnsignedInt32(dataStream);
            chunkSize -= 4;
            metaChunkSize = readUnsignedInt32(dataStream);

            chunkSize -= 4;
            if (metaChunkType == JUNK) {
                break;
            }

            if (metaChunkSize > 0) {
                metaChunkSize = correctChunkSize(metaChunkSize);
                for (int i = 0; i < metaChunkSize; i++) {
                    metaTagInfo += fromByteToAscii(readUnsignedByte(dataStream), 1);
                    chunkSize--;
                }
                metaData.add(fromByteToAscii(metaChunkType, 4) + ":" + metaTagInfo.trim());
            }
        }
        setMetaData(metaData);
    }
}
