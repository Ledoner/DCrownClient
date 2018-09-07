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

import org.jaudiotagger.audio.asf.data.AsfHeader;
import org.jaudiotagger.audio.asf.data.Chunk;
import org.jaudiotagger.audio.asf.data.StreamBitratePropertiesChunk;
import org.jaudiotagger.audio.asf.data.VideoStreamChunk;
import org.jaudiotagger.audio.asf.io.AsfHeaderReader;

public class FilePropertiesASF extends FileProperties {

	/**
	 * Reflects, whether the {@link #process(RandomAccessFile)} has found a video stream or not.
	 */
	private boolean videoStreamPresent = true;

	/**
	 * Returns <code>true</code> if the last call of 
	 * {@link #process(RandomAccessFile)} has found a video stream.<br>
	 * Only the this objects fields have been refilled.
	 */
	public boolean containsVideoStream() {
		return this.videoStreamPresent;
	}

	/**
	 * This method will read the file and interprets it as an ASF-media-file.<br>
	 */
    @Override
	public void process(RandomAccessFile file, String filename) throws Exception {
	    file.seek(0);
		// Let the audio library read the ASF file.
		AsfHeader header = AsfHeaderReader.readHeader(file);

		VideoStreamChunk video = null;
		for (Chunk chunk : header.getChunks()) {
            if (chunk instanceof VideoStreamChunk)
                video = (VideoStreamChunk) chunk;
		}
		
		this.videoStreamPresent = video != null;

		if (this.videoStreamPresent) {
			setVideoResolution(video.getPictureWidth() + "x" + video.getPictureHeight());
			setVideoCodec(video.getCodecIdAsString());
			setDuration(header.getFileHeader().getDurationInSeconds());
			
			// The average bit rate of the video is as hard to gather as the FPS.
			// However in this case there is a recommended chunk in ASF files
			// which contains this information.
			StreamBitratePropertiesChunk propertiesChunk = header.getStreamBitratePropertiesChunk();
			if (propertiesChunk != null)
				setVideoBitRate((int) propertiesChunk.getAvgBitrate(video.getStreamNumber()));

			// The audio part of the video. (optional)
			if (header.getAudioStreamChunk() != null) {
				setAudioCodec(header.getAudioStreamChunk().getCodecDescription());
				setAudioRate((int) header.getAudioStreamChunk().getSamplingRate());
				setAudioBitRate(header.getAudioStreamChunk().getKbps() * 1000);
				setAudioChannels((int) header.getAudioStreamChunk().getChannelCount());
			}
		}
	}
}
