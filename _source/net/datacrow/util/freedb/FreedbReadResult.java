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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.datacrow.util.Utilities;

@SuppressWarnings("unchecked")
public class FreedbReadResult implements Comparable {
    
    private Hashtable fields;
    private boolean exactMatch;

    public FreedbReadResult(String freedbReadResult, String genre) {
        this(freedbReadResult, true);
        this.fields.put("CAT", genre);
    }

    public FreedbReadResult(String freedbReadResult, boolean exactMatch) {
        this.fields = new Hashtable();
        this.exactMatch = exactMatch;
        
        this.fields.put("DISCID", "");
        this.fields.put("DTITLE", "");
        this.fields.put("DYEAR", "");
        this.fields.put("DGENRE", "");
        this.fields.put("EXTD", "");
        this.fields.put("PLAYORDER", "");
        this.fields.put("TITLE", new String[0]);
        this.fields.put("EXTT", new String[0]);

		StringTokenizer st = new StringTokenizer( freedbReadResult, "\n" );

		ExtensibleStringList TITLE = new ExtensibleStringList( st.countTokens() );
		ExtensibleStringList EXTT = new ExtensibleStringList( st.countTokens() );

		String[] answers = new String[st.countTokens()];

		for ( int i = 0; i < answers.length; i++ )
			answers[i] = st.nextToken();

		int j = 1;
		while (answers[j].substring(0, 1).equals( "#" )) {
			j++;
        }
        
        int offsetStartPos = 0;
        int offsetEndPos = 0;
        int playlength = 0;
		for (int i = j; i < answers.length - 1; i++) {
			if (answers[i].substring(0, 4).equals("EXTD")) {
				this.fields.put("EXTD", this.fields.get("EXTD") + answers[i].substring( 5 ));
            } else if (answers[i].substring(0, 4).equals("EXTT")) {
				st = new StringTokenizer(answers[i], "=");

				String field = st.nextToken();
				int index = Integer.parseInt( field.substring( 4 ) );

				try {
					EXTT.concat( st.nextToken(), index );
				} catch ( NoSuchElementException e ) {
					EXTT.concat( "", index );
				}
			} else if (answers[i].substring(0, 5).equals( "DYEAR" )) {
			    this.fields.put("DYEAR", this.fields.get("DYEAR") + answers[i].substring(6));
            } else if ( answers[i].substring( 0, 6 ).equals( "TTITLE" ) ) {
				st = new StringTokenizer( answers[i], "=" );
				String field = st.nextToken();
				int index = Integer.parseInt(field.substring(6));

				TITLE.concat(st.nextToken(), index);
			} else if (answers[i].substring(0, 6).equals("DISCID")) {
			    this.fields.put("DISCID", this.fields.get("DISCID") + answers[i].substring( 7 ));
            } else if (answers[i].substring(0, 6).equals("DTITLE")) {
			    this.fields.put("DTITLE", this.fields.get("DTITLE") + answers[i].substring(7));
            } else if (answers[i].substring(0, 6).equals("DGENRE")) {
			    this.fields.put("DGENRE", this.fields.get("DGENRE") + answers[i].substring( 7 ));
            } else if (answers[i].substring(0, 9).equals("PLAYORDER")) {
			    this.fields.put("PLAYORDER", this.fields.get("PLAYORDER") + answers[i].substring( 10 ));
            }
        }
        
        for (int i = 0; i < j; i++) {
            if (answers[i].indexOf("# Track frame offsets") > -1) {
                offsetStartPos = i + 1;
            } else if (answers[i].indexOf("# Disc length") > -1) {
                playlength = Utilities.getIntegerValue(answers[i]);
                offsetEndPos = i - 1;
            }
        }
        
        this.fields.put("TRACKLENGTH", getTrackLengths(answers, offsetStartPos, offsetEndPos, playlength));
		this.fields.put("TITLE", TITLE.trim());
		this.fields.put("EXTT", EXTT.trim());
		
		String[] info = ((String) this.fields.get("DTITLE")).split(" / ", 2);
	    this.fields.put("ARTIST", info[0]);
	    this.fields.put("ALBUM", (info.length > 1) ? info[1] : "");
	    this.fields.put("TRACKNUMBER", new Integer(((String[])this.fields.get("EXTT")).length));
    }
    
    public Integer[] getTrackLengths(String[] answers, int startPos, int endPos, int playlength) {
        Integer[] trackLengths = new Integer[50];
        if (startPos < endPos && startPos > 0) {
            LinkedList<Integer> offsets = new LinkedList<Integer>();
            for (int i = startPos; i < endPos; i++) {
                String answer = answers[i];
                int offset = Utilities.getIntegerValue(answer);
                if (offset!= 0) offsets.add(Integer.valueOf(offset));
            }
            
            Integer lastOffset = new Integer(playlength * 75);
            offsets.add(lastOffset);
    
            Integer[] offsetsArray = new Integer[offsets.size()];
            offsets.toArray(offsetsArray);
            trackLengths = getCalculatedTrackLengths(offsetsArray);
        }
        return trackLengths;
    }
    
    private Integer[] getCalculatedTrackLengths(Integer[] offsets) {
        int tracks = offsets.length - 1;
        Integer[] offsetlengths = new Integer[tracks];
        for (int i = 0; i < offsetlengths.length; i++) {
            offsetlengths[i] = 
               new Integer((offsets[i + 1].intValue() - offsets[i].intValue()) / 75); 
        }
        return offsetlengths;
    }
    
    public String getCategory() {
        return (String) this.fields.get("CAT");
    }
    
    public String getDiscId() {
        return (String) this.fields.get("DISCID");
    }
    
    public String getAlbum() {
        return (String) this.fields.get("ALBUM");
    }
    
    public String getArtist() {
        return (String) this.fields.get("ARTIST");
    }
    
    public String getTrackComment(int i) {
        return ((String[]) this.fields.get("EXTT"))[i];
    }

    public Integer getTrackSeconds(int i) {
        return ((Integer[]) this.fields.get("TRACKLENGTH"))[i];
    }
    
    public String getTrackTitle(int i) {
        return ((String[]) this.fields.get("TITLE"))[i];
    }
    
    public String getAlbumComment() {
        return (String) this.fields.get("EXTD");
    }
    
    public String getGenre() {
        return (String) this.fields.get("DGENRE");
    }
    
    public String getYear() {
        return (String) this.fields.get("DYEAR");
    }
    
    public boolean isExactMatch() {
        return this.exactMatch;
    }
    
    public int getTracksNumber() {
        return ((Integer) this.fields.get("TRACKNUMBER")).intValue();
    }
    
    public void swapTracks(int i1, int i2) {
        String[] extt = (String[]) this.fields.get("EXTT");
        String[] title  = (String[]) this.fields.get("TITLE");
        
        String temp = extt[i1];
        extt[i1] = extt[i2];
        extt[i2] = temp;
        
        temp = title[i1];
        title[i1] = title[i2];
        title[i2] = temp;
    }
    
    public int getQuality() {
        /*
         * Artist: mandatory
         * Album: mandatory
         * Tracks: mandatory
         * Comment: 25 
         * Year: 40
         * Genre: 20
         * TracksComment: 15 
         */
        return getQuality(25, 40, 20, 15);
	}
    
    private int getQuality(int comment, int year, int genre, int trackComment) {
        if (this.exactMatch) {
            return 100;
        }
        
        int q = 0;
		if (!getGenre().equals("")) {
			q += comment;
        }
        
		if (!getYear().equals("")) {
			q += year;
        }
        
		if (!getAlbumComment().equals("")) {
		    q += genre;
        }
		
        int nb = getTracksNumber();
		int nbTrackComment = 0;
		for (int i = 0; i < nb; i++) {
			if (!getTrackComment(i).equals("")) {
			    nbTrackComment++;
            }
        }
		
		q += (int) (trackComment * (((double)nbTrackComment) / nb));
		return q;
    }
	
	@Override
    public int compareTo(Object o) {
		if (o == null) {
			return 1;
        }
        
		FreedbReadResult rr = (FreedbReadResult) o;
		
		if (this.getQuality() == rr.getQuality()) {
		    // We have an ex-aequo, trying to determine the winner by 
            // genre/comment length and exttrackinfo presence
            
		    // match 1: genre length:
		    int win = 0;
		    if (this.getGenre().length() > rr.getGenre().length()) {
		        win++;
            } else if (this.getGenre().length() < rr.getGenre().length()) {
		        win--;
            }
		    
		    // match 2: comment length
		    if (this.getAlbumComment().length() > rr.getAlbumComment().length()) {
		        win++;
            } else if (this.getAlbumComment().length() < rr.getAlbumComment().length()) {
		        win--;
            }
                
		    // match 3: extinfopresence
		    int nb1 = getTracksNumber();
		    int count1 = 0;
		    for (int i = 0; i < nb1; i++) {
		        if (!getTrackComment(i).equals("")) {
		            count1++;
                }
            }
            
		    int nb2 = rr.getTracksNumber();
		    int count2 = 0;
		    for (int i = 0; i < nb2; i++) {
		        if (!rr.getTrackComment(i).equals("")) {
		            count2++;
                }
            }
            
		    if (count1 > count2) {
		        win++;
            } else if (count1 < count2) {
		        win--;
            }
		    return win;
		}
		return this.getQuality() - rr.getQuality();
	}
	
    /**
	 *  Creates a special data structure that allows to concatenate multiples strings at once. I created this because when the freedb Read query arrives, there are no means of knowing how many fields
	 *  there will be, how many extd track comments there will be, etc. Here is how it works :<br>
	 *  An ExtensibleStringList is created with a given number of "boxes" that contains an empty string. Each times the concat method is called, the box[index] receive a new bit of string that it
	 *  concatenates to the existing one and set the modified flag. After the "filling" process, the trim() method is called. It takes only the boxese that have been filled and reenter those in a new
	 *  array.
	 *
	 * @author     Rapha�l Slinckx (KiKiDonK)
	 * @version    v0.03
	 */
	private static class ExtensibleStringList {

		//  An array of of elements contained in this List
		private ExtensibleStringListElement[] l;

		//  The number of filled boxes
		private int validElements = 0;

		/**
		 * @param  dim  the dimension of the list (typically the number of lines the server returned)
		 */
		public ExtensibleStringList(int dim) {
			this.l = new ExtensibleStringListElement[dim];
			for (int i = 0; i < this.l.length; i++) {
				this.l[i] = new ExtensibleStringListElement( "" );
            }
		}

		/**
		 * Concatenates a bit of string in the given box and set the modified flag
		 * @param  s      the string bit to concatenate
		 * @param  index  the index of the box to be filled
		 */
		public void concat( String s, int index ) {
			this.l[index].setContent( this.l[index].getContent() + s );
			if ( !this.l[index].isModified() )
				this.validElements++;
			this.l[index].setModified();
		}

		/**
		 * Trim the current Elements array into a new array that fits exactly the modified elements
		 * @return    The final array containing only the interesting fields
		 */
		public String[] trim() {
			String[] s = new String[this.validElements];
			int j = 0;

			for ( int i = 0; i < this.l.length; i++ )
				if ( this.l[i].isModified() ) {
					s[j] = this.l[i].getContent();
					j++;
				}
			return s;
		}
	}


	/**
	 *  Creates an Element of the Extensible List that contains a String and a flag.
	 *
	 * @author     Rapha�l Slinckx (KiKiDonK)
	 * @version    v0.03
	 */
	private static class ExtensibleStringListElement {
		/**  The content of this box */
		private String content;

		/**  Flag indicating if this box has been filled */
		private boolean modified = false;

		/**
		 * Creates a new Box with the given String
		 * @param  s  the content of this Box
		 */
		public ExtensibleStringListElement( String s ) {
			this.content = s;
		}

		/**
		 * Set a new string in this box
		 * @param s the new string contained in this box
		 */
		public void setContent( String s ) {
			this.content = s;
		}

		/** 
         * Set the modified Flag to true for this Box meaning that a content 
         * has been added 
         */
		public void setModified() {
			this.modified = true;
		}

		/**
		 * Get the Content of this Box
		 * @return    the string contained in this box
		 */
		public String getContent() {
			return this.content;
		}

		/**
		 * Get the value of the Flag for this Box
		 * @return    the Flag value
		 */
		public boolean isModified() {
			return this.modified;
		}
	}
}
