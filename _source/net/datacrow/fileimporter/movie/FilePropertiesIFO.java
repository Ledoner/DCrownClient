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

class FilePropertiesIFO extends FileProperties {

    private static Logger logger = Logger.getLogger(FilePropertiesIFO.class.getName());
    
	private final int DVDVIDEO_VTS = 0x535456; /* 'VTS' */
	private final int SIZE = 100000;

	@Override
	protected void process(RandomAccessFile dataStream, String filename) throws Exception {
		byte[] ifoFile = new byte[SIZE + 10];

		// 4 bytes has already been read in FilePropertiesMovie, 
		// therefore the first byte read is stored in index 4
		dataStream.read(ifoFile, 4, SIZE);

		// gets the stream type... (4 bytes)
		int streamType = readUnsignedInt32(ifoFile, 9);

		if (streamType == DVDVIDEO_VTS) {
			processIfoFile(ifoFile);
			setContainer("VOB");
		}
	}

	void processIfoFile(byte[] ifoFile) throws Exception {
		getRuntime(ifoFile);
		getVideoAttributes(ifoFile);
		getAudioAttributes(ifoFile);
		getSubtitles(ifoFile);
	}

	void getRuntime(byte[] ifoFile) throws Exception {
		// sector pointer to VTS_PGCI (Title Program Chain table) Offset 204
		int sectorPointerVTS_PGCI = changeEndianness(readUnsignedInt32(ifoFile, 204));

		// offset value of the VTS_PGCITI (Video title set program chain information table)
		int offsetVTS_PGCI = sectorPointerVTS_PGCI * 2048;
		int numberOfTitles = readUnsignedInt16(ifoFile, offsetVTS_PGCI + 1);
		int[] runtime = new int[numberOfTitles];

		int pointer = offsetVTS_PGCI;
		int startcode = changeEndianness(readUnsignedInt32(ifoFile, offsetVTS_PGCI + 12));

		offsetVTS_PGCI += 12;

		for (int i = 0; i < numberOfTitles; i++) {
			runtime[i] = 0;
			runtime[i] += Integer.parseInt(Integer.toHexString(
                    ifoFile[pointer + startcode + 4]), 16) * 60 * 60; /*Hours*/
			runtime[i] += Integer.parseInt(Integer.toHexString(
                    ifoFile[pointer + startcode + 5]), 16) * 60; /*Minutes*/
			runtime[i] += Integer.parseInt(Integer.toHexString(
                    ifoFile[pointer + startcode + 6]), 16); /*Seconds*/

			int[] bits = getBits(ifoFile[pointer + startcode + 7], 1);
			switch (getDecimalValue(bits, 7, 6, false)) {
    			case 1: 
    				setVideoRate(25000); //25 FPS
    				break;
     			case 3: 
    				setVideoRate(30000); //30 FPS
    				break;
            }
			// Increase by 8 to get to the next start code
			offsetVTS_PGCI += 8;
			startcode = changeEndianness(readUnsignedInt32(ifoFile, offsetVTS_PGCI));
		}
		setDuration(runtime[0]);
	}

	protected void getVideoAttributes(byte[] ifoFile) throws Exception {

		/* offset 0200 (hex)*/
		int read = readUnsignedByte(ifoFile, 512);
		int[] bits = getBits(read, 1);
		String videoCodingMode = getDecimalValue(bits, 7, 6, false) == 0 ? "MPEG-1" : "MPEG-2";

		String standard = getDecimalValue(bits, 5, 4, false) == 0 ? "NTSC 525/60" : "PAL 625/50";

		String automaticDisplayMode = "";
		if (getDecimalValue(bits, 1, 1, false) == 0)
			automaticDisplayMode = "pan-scan";

		if (getDecimalValue(bits, 0, 0, false) == 0) {
			if (!automaticDisplayMode.equals(""))
				automaticDisplayMode += " & ";

			automaticDisplayMode += "letterboxed";
		}

		if (automaticDisplayMode.equals(""))
			automaticDisplayMode = "Not specified";

		setVideoCodec(videoCodingMode);

		/* offset 0201 (hex)*/
		read = readUnsignedByte(ifoFile, 513);
		bits = getBits(read, 1);

		getDecimalValue(bits, 5, 5, false);
		
		int res = getDecimalValue(bits, 4, 3, false);
		String resolution = "";

		switch (res) {
    		case 0: 
    			if (standard.equals("NTSC 525/60"))
    				resolution = "720x480";
    			else
    				resolution = "720x576";
    			break;
    		case 1: 
    			if (standard.equals("NTSC 525/60"))
    				resolution = "704x480";
    			else
    				resolution = "704x576";
    			break;
    		case 2: 
    			if (standard.equals("NTSC 525/60"))
    				resolution = "352x480";
    			else
    				resolution = "352x576";
    			break;
    		case 3: 
    			if (standard.equals("NTSC 525/60"))
    				resolution = "352x240";
    			else
    				resolution = "352x288";
    			break;
		}

		getDecimalValue(bits, 2, 2, false);
		getDecimalValue(bits, 0, 0, false);
		setVideoResolution(resolution);
	}

	protected void getAudioAttributes(byte[] ifoFile) throws Exception {

		int numberOfAudioStreams;
		int offset = 516;
		int read;
		int[] bits;

		// offset 0202 (hex)
		numberOfAudioStreams = getUnsignedInt16(readUnsignedByte(ifoFile, 514), readUnsignedByte(ifoFile, 515));

		// audio attributes
		for (int i = 0; i < numberOfAudioStreams; i++) {
			// offset 0204 (hex)
			read = readUnsignedByte(ifoFile, offset++);
			bits = getBits(read, 1);

			int audCodingMode = getDecimalValue(bits, 7, 5, false);
			String audioCodingMode = "";

			switch (audCodingMode) {
    			case 0:
    				audioCodingMode = "AC3";
    				break;
    			case 2:
    				audioCodingMode = "Mpeg-1";
    				break;
    			case 3:
    				audioCodingMode = "Mpeg-2ext";
    				break;
    			case 4:
    				audioCodingMode = "LPCM";
    				break;
    			case 6:
    				audioCodingMode = "DTS";
    				break;
			}

			setAudioCodec(audioCodingMode);

			int languageType = getDecimalValue(bits, 3, 2, false);

			int appMode = getDecimalValue(bits, 1, 0, false);

			switch (appMode) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			}

			read = readUnsignedByte(ifoFile, offset++);
			bits = getBits(read, 1);

			int q_DRC = getDecimalValue(bits, 7, 6, false);
			switch (q_DRC) {
    			case 0: 
    				break;
    			case 1: 
    				break;
    			case 2: 
    				break;
    			case 3: 
    				break;
			}

			int sampleRate = getDecimalValue(bits, 5, 4, false) == 0 ? 48000 : 96000;
			setAudioRate(sampleRate);

			int numberOfAudioChannels = getDecimalValue(bits, 2, 0, false);
			setAudioChannels(numberOfAudioChannels);

			if (languageType == 1) {
				read = readUnsignedInt16(ifoFile, offset);
				offset += 6;

				switch (read) {
    				case 0: 
    					break;
    				case 1:
    					break;
    				case 2:
    					break;
    				case 3:
    					break;
    				case 4:
    					break;
				}
			}
		}
	}

	protected void getSubtitles(byte[] ifoFile) {
		int read;
		int offset = 598;
		int numberOfSubtitleStreams;
		int[] bits;
		String languageCode;
		String subtitles = "";

		try {
			numberOfSubtitleStreams = getUnsignedInt16(readUnsignedByte(ifoFile, 596), 
                                                       readUnsignedByte(ifoFile, 597));
			for (int i = 0; i < numberOfSubtitleStreams; i++) {
				languageCode = "";
				read = readUnsignedByte(ifoFile, offset);
				bits = getBits(read, 1);
				if (getDecimalValue(bits, 1, 0, false) == 1) {
					offset += 2;

					read = readUnsignedInt16(ifoFile, offset);
					languageCode += fromByteToAscii(read, 2);

					if (!subtitles.equals("")) {
						subtitles += ", ";
                    }

					subtitles += CoreUtilities.getLanguage(languageCode);
					offset += 4;
				} else {
					offset += 6;
				}
			}
			setSubtitles(subtitles);

		} catch (Exception e) {
            logger.error(e, e);
		}
	}
}
