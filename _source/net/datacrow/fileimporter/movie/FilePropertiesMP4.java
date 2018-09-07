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

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.coremedia.iso.boxes.BitRateBox;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.log4j.Logger;

class FilePropertiesMP4 extends FileProperties {

    private static Logger logger = Logger.getLogger(FilePropertiesMP4.class.getName());

    private IsoFile isoFile;

    public static Boolean checkIfMp4File(RandomAccessFile dataStream) {
        try {
            dataStream.seek(0);
            IsoFile isoFile = new IsoFile(dataStream.getChannel());

            MovieBox mbox = isoFile.getMovieBox();
            // that seems to be an mp4 file container if the moviebox is not
            // null
            return mbox != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void process(RandomAccessFile dataStream, String filename) throws Exception {

        dataStream.seek(0);

        FileChannel fc = dataStream.getChannel();
        this.isoFile = new IsoFile(fc);

        FileTypeBox ftypBox = isoFile.getBoxes(FileTypeBox.class).get(0);

        String majorBrand = ftypBox.getMajorBrand();

        if (majorBrand.equals("mp42")) {
            setContainer("MP4");
        } else if (majorBrand.equals("3gp6")) {
            setContainer("MP4");
            logger.info("Major brand \"3gp6\" found in file " + filename + ". Parsing the file might fail. ");
        } else {
            logger.error("Unsupported major brand in ftyp-box of file " + filename);
        }

        MovieBox theMovieBox = this.isoFile.getMovieBox();
        MovieHeaderBox theMovieHeaderBox = theMovieBox.getMovieHeaderBox();

        if (theMovieHeaderBox != null) {
            double lengthInSeconds = (double) theMovieHeaderBox.getDuration() / theMovieHeaderBox.getTimescale();

            setDuration((int) lengthInSeconds);
        } else {
            logger.debug("MovieHeaderBox not found in file " + filename);
        }

        this.processHandlerBoxes(isoFile);
    }

    private void processHandlerBoxes(IsoFile isoFile) {

        try {
            List<HandlerBox> handlerBoxes = isoFile.getBoxes(HandlerBox.class,
                    true);

            for (HandlerBox handlerBox : handlerBoxes) {
                if (handlerBox.getHandlerType().equals("vide")) {
                    this.parseVideoDetails(handlerBox);

                } else if (handlerBox.getHandlerType().equals("soun")) {
                    this.parseAudioDetails(this.isoFile);
                } else {
                    logger.debug("Unhandled handler box ("
                            + handlerBox.getHandlerType() + ") found in file"
                            + this.getFilename());
                }
            }
        } catch (Exception e) {
            logger.error("Exception while processing handler boxes in file " + this.getFilename(), e);
        }
    }

    private void parseVideoDetails(HandlerBox videoHandlerBox) {
        try {
            logger.debug("Video track found!");

            // get track header box
            MediaBox mediaBox = (MediaBox) videoHandlerBox.getParent();
            TrackHeaderBox trackHeaderBox = mediaBox.getParent()
                    .getBoxes(TrackHeaderBox.class).get(0);

            // get width and height
            // we need integers in the further process
            int height = (int) trackHeaderBox.getHeight();
            int width = (int) trackHeaderBox.getWidth();
            this.setVideoResolution(width + "x" + height);

            MediaHeaderBox mdhd = mediaBox.getBoxes(MediaHeaderBox.class)
                    .get(0);

            if (mdhd != null) {
                this.setVideoRate(mdhd.getTimescale());
            }

            List<VisualSampleEntry> vseList = isoFile.getBoxes(
                    VisualSampleEntry.class, true);

            if (!vseList.isEmpty()) {
                VisualSampleEntry vse = vseList.get(0);

                String videoCodec;

                // mp4v"" || ""s263"" || ""avc1"" || ""avc3"" || ""drmi""
                switch (vse.getType()) {
                case "mp4v":
                    videoCodec = "MPEG-4 Video (mp4v)";
                    break;
                case "s263":
                    videoCodec = "3GPP H.263v1 (s263)";
                    break;
                case "avc1":
                    videoCodec = "AVC (avc1)";
                    break;
                case "avc3":
                    videoCodec = "AVC (avc3)";
                    break;
                case "hvc1":
                    videoCodec = "hvc1";
                    break;
                case "hev1":
                    videoCodec = "hev1";
                    break;
                case "encv":
                    videoCodec = "Encrypted (encv)";
                    break;
                case "drmi":
                    videoCodec = "drmi";
                    break;
                default:
                    videoCodec = "Unknown (" + vse.getType() + ")";
                }

                this.setVideoCodec(videoCodec);

                // check if a bitrate box is present

                List<com.coremedia.iso.boxes.BitRateBox> btrtBoxes = vse
                        .getBoxes(BitRateBox.class);

                if (!btrtBoxes.isEmpty()) {
                    BitRateBox btrt = btrtBoxes.get(0);
                    // convert the bitrate from bps to Kbps
                    int avgBitRate = (int) Math
                            .round(btrt.getAvgBitrate() / 1000.0);

                    this.setVideoBitRate(avgBitRate);
                }
            }
        } catch (Exception e) {
            logger.error("Could not parse video details of file " + this.getFilename(), e);
        }
    }

    private void parseAudioDetails(IsoFile isoFile) {
        try {
            List<AudioSampleEntry> aseList = isoFile.getBoxes(
                    AudioSampleEntry.class, true);

            if (!aseList.isEmpty()) {

                AudioSampleEntry ase = aseList.get(0);

                ESDescriptorBox esBox = ase.getBoxes(ESDescriptorBox.class)
                        .get(0);

                ESDescriptor esd = esBox.getEsDescriptor();

                DecoderConfigDescriptor dcd = esd.getDecoderConfigDescriptor();

                AudioSpecificConfig asc = dcd.getAudioSpecificInfo();

                String audioCodec;

                switch (asc.getAudioObjectType()) {
                // http://stackoverflow.com/questions/3987850/mp4-atom-how-to-discriminate-the-audio-codec-is-it-aac-or-mp3
                case 0:
                    audioCodec = "";
                    break;
                case 1:
                    audioCodec = "AAC Main";
                    break;
                case 2:
                    audioCodec = "AAC LC";
                    break;
                case 3:
                    audioCodec = "AAC SSR";
                    break;
                case 4:
                    audioCodec = "AAC LTP";
                    break;
                case 5:
                    audioCodec = "HE-AAC";
                    break;
                case 22:
                    audioCodec = "ER BSAC";
                    break;
                case 23:
                    audioCodec = "Low delay AAC";
                    break;
                case 29:
                    audioCodec = "HE-AACv2";
                    break;
                case 32:
                    audioCodec = "MP3on4 Layer 1";
                    break;
                case 33:
                    audioCodec = "MP3on4 Layer 2";
                    break;
                case 34:
                    audioCodec = "MP3on4 Layer 3";
                    break;
                default:
                    audioCodec = null;
                }

                this.setAudioCodec(audioCodec);

                // oh no, in this version of mp4parser, the bit rate is not yet
                // accessible
                // long avgBitRate = dcd.getAvgBitRate();
                // we have to do it the dirty way (?)
                String info = dcd.toString();
                String[] array = info.split("avgBitRate=");
                String avgBitRate = array[1].split(",")[0];

                int avgBitRateBps = Integer.parseInt(avgBitRate);

                // convert the bitrate from bps to Kbps
                int avgBitRateInt = (int) Math.round(avgBitRateBps / 1000.0);

                this.setAudioBitRate(avgBitRateInt);
                this.setAudioRate(asc.getSamplingFrequency());
                this.setAudioChannels(asc.getChannelConfiguration());
            }
        }

        catch (Exception e) {
            logger.error("Could not parse audio details of file " + this.getFilename(), e);
        }
    }
}