/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                       (c) 2003 The Data Crow team                          *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                                                                            *
 *       This library is free software; you can redistribute it and/or        *
 *        modify it under the terms of the GNU Lesser General Public          *
 *       License as published by the Free Software Foundation; either         *
 *     version 2.1 of the License, or (at your option) any later version.     *
 *                                                                            *
 *      This library is distributed in the hope that it will be useful,       *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU       *
 *           Lesser General Public License for more details.                  *
 *                                                                            *
 *     You should have received a copy of the GNU Lesser General Public       *
 *    License along with this library; if not, write to the Free Software     *
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA   *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.MainFrame;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.utilities.Base64;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.StringUtils;
import net.datacrow.settings.DcSettings;

import org.apache.log4j.Logger;

public class Utilities {
    
    private static Logger logger = Logger.getLogger(Utilities.class.getName());

    private static final Toolkit tk = Toolkit.getDefaultToolkit();

    private static final Clipboard clipboard = tk.getSystemClipboard();
    
    private static final Pattern[] normalizer = {
        Pattern.compile("('|~|\\!|@|#|\\$|%|\\^|\\*|_|\\[|\\{|\\]|\\}|\\||\\\\|;|:|`|\"|<|,|>|\\.|\\?|/|&|_|-)"),
        Pattern.compile("[(,)]")};
    
    public static Toolkit getToolkit() {
        return tk;
    }
    
    public static DcImageIcon getImageFromClipboard() {
        Transferable clipData = clipboard.getContents(clipboard);
        if (clipData != null) {
            if (clipData.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                try {
                    Image image = (Image) clipData.getTransferData(DataFlavor.imageFlavor);
                    return new DcImageIcon(CoreUtilities.getBytes(new DcImageIcon(image)));
                } catch (Exception ignore) {}
            }
        }
        return null;
    }
    
    public static String getHtmlRating(int rating) {
        String result = "";
        
        String path = IconLibrary.picPath;
        for (int i = 0; i < rating; i++) 
            result += "<img src=\"file:///" + path + "rating_ok.png\" />";
        for (int i = rating; i < 10; i++)
            result += "<img src=\"file:///" + path + "rating_nok.png\" />";
        
        return result; 
    }
    
    /**
     * Returns a centered location for a window / form / dialog 
     * @param windowSize size of the window
     * @return centered location
     */
    public static Point getCenteredWindowLocation(Dimension windowSize, boolean main) {
        MainFrame mf = GUI.getInstance().getMainFrame();
        
        main = main || mf == null;
        
        Dimension dim;
        if (main) {
            dim = tk.getScreenSize();
            dim.height = (dim.height - windowSize.height) / 2;
            dim.width = (dim.width - windowSize.width) / 2;
        } else {
            // relative to the mainframe
            dim = tk.getScreenSize();
            Point p = mf.getLocation();
            dim.height = (p.y) + ((mf.getSize().height - windowSize.height )  / 2);
            dim.width = (p.x) + ((mf.getSize().width - windowSize.width ) / 2);
        }

        return new Point(dim.width, dim.height);
    }
    
    /**
     * Converts an ordinary string to something which is allowed to be used in a
     * filename or pathname.
     */
    public static String toFilename(String text) {
        String s = text == null ? "" : text.trim().toLowerCase();

        s = s.replaceAll("\n", "");
        s = s.replaceAll("\r", "");
        
        for (int i = 0; i < normalizer.length; i++) {
            Matcher ma = normalizer[i].matcher(s);
            s = ma.replaceAll("");
        }
        
        s = StringUtils.normalize2(s);
        s = s.replaceAll("[\\-]", "");
        s = s.replaceAll(" ", "");
        
        return s.trim();
    }
    
    public static String getFirstName(String name) {
    	if (name.indexOf(",") > -1) {
    		return name.substring(name.indexOf(",") + 1).trim();
    	} else if (name.indexOf(" ") > -1) {
    		String firstname = name.substring(0, name.indexOf(" ")).trim();
    		if (name.indexOf("(") > -1)
    			firstname += " " + name.substring(name.indexOf("("));
    		
    		return firstname;
    	} else {
    		return "";
    	}
    }
    
    public static String getLastName(String name) {
    	if (name.indexOf(",") > -1) {
    		return name.substring(0, name.indexOf(",")).trim();
    	} else if (name.indexOf(" ") > -1) {
    		String lastname = name.substring(name.indexOf(" ") + 1).trim();
    		if (lastname.indexOf("(") > -1)
    			lastname = lastname.substring(0, lastname.indexOf("(")).trim();
    		
    		return lastname;
    	} else {
    		return name;
    	}
    }
    
    public static String getName(String firstname, String lastname) {
        firstname = firstname == null ? "" : firstname.trim();
        lastname = lastname == null ? "" : lastname.trim();
        return (firstname + " " + lastname).trim();
    }
    
    public static String getHtmlStyle() {
        return getHtmlStyle(null, null, null, 0);
    }
    
    public static String getHtmlStyle(Font font) {
        return getHtmlStyle(null, null, font, font.getSize());
    }
    
    public static String getHtmlStyle(Color bg) {
        return getHtmlStyle(null, bg, null, 0);
    }
    
    public static String getHtmlStyle(String additionalStyleInfo, Font font) {
        return getHtmlStyle(additionalStyleInfo, null, font, font == null ? 0 : font.getSize());
    }
    
    public static String getHtmlStyle(String additionalStyleInfo, Color bg, Font f, int fSize) {
        Color color = ComponentFactory.getCurrentForegroundColor();
        String foreground = Utilities.getHexColor(color);
        Font font = f == null ? DcSettings.getFont(DcRepository.Settings.stSystemFontNormal) : f;
        int fontSize = fSize <= 0 ? font.getSize() : fSize;
        
        StringBuffer sb = new StringBuffer();
        sb.append("style=\"");
        
        sb.append("font-family:");
        sb.append(font.getFamily());
        sb.append(";font-size:");
        sb.append(fontSize);
        
        if (font.isItalic())
            sb.append(";font-style:italic");
        if (font.isBold())
            sb.append(";font-weigth:bolder");
        
        if (bg != null) {
            String background = Utilities.getHexColor(bg);
            sb.append(";background:");
            sb.append(background);
        }
        
        if (additionalStyleInfo != null) {
            if (!additionalStyleInfo.startsWith(";"))
                sb.append(";");
            
            sb.append(additionalStyleInfo);
        }
            
        sb.append(";color:");
        sb.append(foreground);
        sb.append(";\"");
        
        return sb.toString();
    }
    
    public static String getMacAddress() {
        InetAddress ip;
        try {
     
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
            }
            return sb.toString();
     
        } catch (UnknownHostException e) {
            logger.debug(e, e);
        } catch (SocketException e){
            logger.debug(e, e);
        }
        return null;
    }
    
    public static boolean sameImage(byte[] img1, byte[] img2) {
        boolean same = img1.length == img2.length;
        if (same) {
            for (int i = 0; i < img1.length; i++) {
                same = img1[i] == img2[i];
                if (!same)
                    break;
            }
        }
        return same;
    }    
    
    public static Collection<String> getCharacterSets() {
        Collection<String> characterSets = new ArrayList<String>(); 
        for (String name :  Charset.availableCharsets().keySet()) {
            characterSets.add(name);
        }
        return characterSets;
    }
    
    public static Long getSize(File file) {
        return Long.valueOf(file.length());  
    }
    
    /**
     * Creates a unique ID. Can be used for custom IDs in the database.
     * Based on date / time + random number
     * @return unique ID as String
     */
    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Retrieved the file extension of a file
     * @param f file to get the extension from
     * @return extension or empty string
     */
    public static String getExtension(File f) {
        String name = f.getName().toLowerCase();
        int i = name.lastIndexOf( "." );
        if (i == -1) {
            return "";
        }
        return name.substring( i + 1 );
    }    
    
    public static int getIntegerValue(String s) {
        char[] characters = s.toCharArray();
        String test = "";
        for (int i = 0; i < characters.length; i++) {
            if (Character.isDigit(characters[i])) test += "" + characters[i];
        }
        
        int number = 0;
        try {
            number = Integer.valueOf(test).intValue();
        } catch (Exception ignore) {}
        
        return number;
    }
   
    /**
     * Reads the content of a file (fully)
     * @param file file to retrieve the content from
     * @return content of the file as a byte array
     * @throws Exception
     */
    public static byte[] readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            bis.close();
            throw new IOException("File is too large to read " + file.getName());
        }
    
        byte[] bytes = new byte[(int)length];
    
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=bis.read(bytes, offset, bytes.length-offset)) >= 0)
            offset += numRead;

        bis.close();
        is.close();

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        
        return bytes;    
    }

    public static void writeToFile(byte[] b, String filename) throws Exception {
        writeToFile(b, new File(filename));
    } 
    
    public static void writeToFile(byte[] b, File file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        bos.write(b);
        bos.flush();
        bos.close();
    }   

    public static String getHexColor(Color color) {
        String hexColor = "#" + Integer.toHexString(color.getRed());
        hexColor += Integer.toHexString(color.getGreen());
        hexColor += Integer.toHexString(color.getBlue()); 
        return hexColor.toUpperCase();
    }
    
    public static String toHex(byte in[]) {
        byte ch = 0x00;
        int i = 0; 

        String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8",
                           "9", "A", "B", "C", "D", "E", "F"};

        StringBuffer out = new StringBuffer(in.length * 2);
        while (i < in.length) {

            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4);     // shift the bits down
            ch = (byte) (ch & 0x0F);    // must do this is high order bit is on!
            out.append(pseudo[ch]);

            ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
            out.append(pseudo[ch]);
            
            i++;
        }

        return out.toString();
    }       
    
    public static void copy(File currentFile, File newFile, boolean overwrite) throws IOException {
        
        if (currentFile.equals(newFile))
            return;
        
        if (!overwrite && newFile.exists())
            return;
        
        // native code failed to move the file; do it the custom way
        FileInputStream fis = new FileInputStream(currentFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
    
        FileOutputStream fos = new FileOutputStream(newFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        int count = 0;
        int b;
        while ((b = bis.read()) > -1) {
            bos.write(b);
            count++;
            if (count == 2000) {
                bos.flush();
                count = 0;
            }
        }
        
        bos.flush();
        
        bis.close();
        bos.close();
    }
    
    public static void rename(File currentFile, File newFile, boolean overwrite) throws IOException {
        
        if (currentFile.equals(newFile))
            return;

        if (newFile.exists() && !overwrite)
            return;
        
        if (newFile.getParentFile() != null)
            newFile.getParentFile().mkdirs();
        
        boolean success = currentFile.renameTo(newFile);
        
        if (!success) {
            copy(currentFile, newFile, overwrite);
            currentFile.delete();
        }
    }

    public static String getCurrentDirectory() throws Exception {
    	File fl = new File(".");
    	fl = fl.getCanonicalFile();
        return fl.toString();
    }
    
    /**
     * Gets the content of a file and converts it to a base64 string
     * @param url URL of file
     * @return base64 content of the file
     */
    public static String fileToBase64String(File file) {
        try {
            byte[] b = Utilities.readFile(file);
            file = null;
            return String.valueOf(Base64.encode(b));
        } catch (Exception e) {
            logger.error("Error while converting content from " + file + " to base64", e);
        }
        return "";
    }
}
