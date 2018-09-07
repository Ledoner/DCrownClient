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

package net.datacrow.util.freedb;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import net.datacrow.core.DcRepository;
import net.datacrow.core.http.HttpConnection;
import net.datacrow.core.http.HttpConnectionUtil;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.MusicAlbum;
import net.datacrow.core.objects.helpers.MusicTrack;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.Utilities;

public class Freedb {
    
    private String userLogin = formatProperty(System.getProperty("user.name"));
    private String userDomain = formatProperty(System.getProperty("os.name"));
    private String clientName = "DataCrow";
    private String clientVersion = "1.0";
    private String protocol = "6";
    
    private String server = "freedb.freedb.org";
    
    public Freedb() {}
    
    public DcObject[] queryDiscId(String discID) throws Exception {
        return query(discID);
    }   
    
    public String[] getCategories() throws Exception {
        String answer = askFreedb("cddb+lscat");
        StringTokenizer st = new StringTokenizer(answer, "\n");
        List<String> l = new LinkedList<String>();
        while(st.hasMoreTokens()) {
            String line = st.nextToken();
            if(!line.startsWith("2") && !line.startsWith("."))
                l.add(line);
        }
        
        return l.toArray(new String[0]);
    }
    
    
//    private String[] getAvailableServers() throws Exception {
//        String answer = askFreedb("sites");
//        StringTokenizer st = new StringTokenizer(answer, "\n");
//        
//        List<String> l = new LinkedList<String>();
//        while(st.hasMoreTokens()) {
//            String line = st.nextToken();
//            if(!line.startsWith("2") && !line.startsWith(".") ) {
//                String[] sline = line.split(" ", 7);
//                if(sline[1].equals("http"))
//                    l.add(sline[0]);
//            }
//        }
//        
//        return l.toArray(new String[0]);
//    }

    /**
     * Queries the freedb server for the full id:
     * Client command: 
     * -> cddb query discid ntrks off1 off2 ... nsecs
     * 
     * After querying, the cd is queried by its discid and genre
     * 
     * @param id full disc id
     * @throws Exception
     */
    public net.datacrow.core.objects.DcObject[] query(String id) throws Exception {
        //Create the command to be sent to freedb
        String command = getReadCommand(id);
        //Send the command, and read the answer
        String queryAnswer = askFreedb(command);
        
        StringTokenizer st = new StringTokenizer(queryAnswer, "\n" );
        String[] answers = new String[st.countTokens()];
        for (int i = 0; i < answers.length; i++) {
            answers[i] = st.nextToken();
        }
      
        int counter = 0;
        String[][] data = new String[answers.length][];
        for (int i = 0; i < answers.length; i++) {
        	String answer = answers[i];
            
            if (answer.startsWith("200")) {
            	answer = answer.substring(3, answer.length());
            }
            
            if (!answer.startsWith("211") && !answer.startsWith("210") && !answer.startsWith(".")) {
                st = new StringTokenizer(answer, " " );
                String[] row = new String[st.countTokens()];
                for (int j = 0; j < 2; j++) {
                    row[j] = st.nextToken();
                }
                data[i] = row;
                counter++;
            }
        }
        
        DcObject[] audioCDs = new MusicAlbum[counter];
        counter = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
            	String genre = data[i][0];
            	String discid = data[i][1];
                
                FreedbReadResult result = read(genre, discid);
                audioCDs[counter++] = convertToDcObject(result);
            }
        }
        return audioCDs;
    }
    
    public FreedbReadResult read(String genre, String id) throws Exception {
        //Create the command to be sent to freedb
        String command = getReadCommand(genre, id);
        //Send the command, and read the answer
        String queryAnswer = askFreedb(command);
        //Parse the result
        return new FreedbReadResult(queryAnswer, genre);
    }
    
    public FreedbReadResult read(FreedbQueryResult query) throws Exception {
        //Create the command to be sent to freedb
        String command = getReadCommand(query);

       // logger.info(DcResources.getText("msgSendCommandToServer", command));
        //Send the command, and read the answer
        String queryAnswer = askFreedb(command);
        
        //logger.info(DcResources.getText("msgGotResult"));
        //Parse the result
        return new FreedbReadResult(queryAnswer, query.isExactMatch());
    }    
    
    private String askFreedb(String command) throws Exception {
		URL url = null;
		try {
			url = new URL( "http://" + server + "/~cddb/cddb.cgi" );
		} catch (MalformedURLException e) {
			throw new Exception(DcResources.getText("msgInvalidURLChangeSetting", "http://" + server + "/~cddb/cddb.cgi"));
		}
        
        HttpConnection connection = HttpConnectionUtil.getConnection(url);
		try {
			PrintWriter out = new PrintWriter(connection.getOutputStream());
            String query = "cmd=" + command + "&hello=" + userLogin + "+" + userDomain + "+" + 
                           clientName + "+" + clientVersion + "&proto="+ protocol;
            out.println(query);
            out.flush();
            out.close();
		} catch ( IOException e ) {
			throw new Exception(DcResources.getText("msgFreeDBServerUnreachable", e.getMessage()));
		}
		
		String output = connection.getString();

		//Preliminary freedb error check, error codes 4xx and 5xx indicate an error
		if (output.startsWith("4") || output.startsWith("5") || output.startsWith("202")) {
			throw new Exception(DcResources.getText("msgFreeDBServerReturnedError", output));
        }
		return output;
	}
    
    private String getReadCommand(FreedbQueryResult query) {
		return "cddb+read+" + query.getCategory() + "+" + query.getDiscId();
	}
	
    private String getReadCommand(String id) {
        return "cddb+query+"+ id;
    }
    
	private String getReadCommand(String genre, String id) {
		return "cddb+read+" + genre + "+" + id;
	}
	
    private String formatProperty(String s) {
        if(s.length() == 0)
            return "default";
        int i = s.indexOf(" ");
        return (i == -1) ? s : s.substring(0, i);
    }
    
    /**
     * Converts a query result (not detailed) to a Data Crow Object
     * @param result query result
     */
    public DcObject convertToDcObject(FreedbQueryResult result) {
        DcObject ma = DcModules.get(DcModules._MUSIC_ALBUM).getItem();
        ma.setValue(MusicAlbum._A_TITLE, result.getAlbum());
        ma.setValue(MusicAlbum._F_ARTISTS, result.getArtist());
        ma.setValue(MusicAlbum._G_GENRES, result.getCategory());
        ma.addExternalReference(DcRepository.ExternalReferences._DISCID, result.getDiscId());
        return ma;
    }
    
    private void setGenres(DcObject dco, String genre) {
        dco.createReference(MusicAlbum._G_GENRES, genre);
    }
    
    /**
     * Converts a read result (detailed) to a Data Crow Object
     * @param result read result
     */
    public DcObject convertToDcObject(FreedbReadResult result) {
        DcObject audioCD = DcModules.get(DcModules._MUSIC_ALBUM).getItem();
        
        String title = result.getAlbum();
        if (title != null && title.length() > 0)
        	title = title.endsWith("\r") || title.endsWith("\n") ? title.substring(0, title.length() - 1) : title;
        
        audioCD.setValue(MusicAlbum._A_TITLE, title);
        
        audioCD.createReference(MusicAlbum._F_ARTISTS, result.getArtist());
        audioCD.addExternalReference(DcRepository.ExternalReferences._DISCID, result.getDiscId());
        setGenres(audioCD, result.getCategory());        
        
        int year = Utilities.getIntegerValue(result.getYear());
        audioCD.setValue(MusicAlbum._C_YEAR, new Integer(year));
        
        for (int i = 0; i < result.getTracksNumber(); i++) {
            DcObject track = DcModules.get(DcModules._MUSIC_TRACK).getItem();
            String lyric = result.getTrackComment(i);
            track.setValue(MusicTrack._F_TRACKNUMBER, new Integer(i + 1));
            track.setValue(MusicTrack._A_TITLE, result.getTrackTitle(i));
            track.setValue(MusicTrack._M_LYRIC, lyric);
            track.setValue(MusicTrack._J_PLAYLENGTH, result.getTrackSeconds(i));
            audioCD.addChild(track);
        }
        
        audioCD.setIDs();
        return audioCD;
    }    
}
