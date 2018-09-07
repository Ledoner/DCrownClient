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

package net.datacrow.console.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.ImageObserver;
import java.awt.image.Kernel;
import java.io.File;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JToolTip;

import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.menu.DcPictureFieldMenu;
import net.datacrow.console.windows.BrowserDialog;
import net.datacrow.console.windows.OpenFromUrlDialog;
import net.datacrow.console.windows.PictureDialog;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcImageIcon;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.utilities.Base64;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.core.utilities.filefilters.DcFileFilter;
import net.datacrow.core.utilities.svg.SVGtoBufferedImageConverter;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.Utilities;
import net.datacrow.util.filefilters.PictureFileFilter;

import org.apache.log4j.Logger;

public class DcPictureField extends JComponent implements IComponent, ActionListener, MouseListener {

    private static Logger logger = Logger.getLogger(DcPictureField.class.getName());
    
    private boolean changed = false;
    private boolean scaled = true;

    private Image img = null;
    private DcImageIcon picture;
    
    private int imageWidth = -1;
    private int imageHeight = -1;

    private Dimension size = null;
    private DcPictureFieldMenu menu;
    
    public DcPictureField() {
    	this(true, false);
    }
    
    public DcPictureField(boolean scaled, boolean allowActions) {
        this.setLayout(Layout.getGBL());
        if (allowActions) {
        	this.menu = new DcPictureFieldMenu(this);
            this.add(menu, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0,
                     GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 5, 5), 0, 0));
        }
        
        if (allowActions) addMouseListener(this);
        
        this.add(new PicturePane(), Layout.getGBC(0, 1, 1, 1, 80.0, 80.0,
                 GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets(1, 1, 1, 1), 0, 0));
        
        this.scaled = scaled;
    }
    
    public void setScaled(boolean scaled) {
        this.scaled = scaled;
    }

    @Override
    public void setValue(Object o) {
        if (o == picture)
            return;
        
        if (o instanceof Picture) {
            Picture pic = (Picture) o;
            DcImageIcon img = (DcImageIcon) pic.getValue(Picture._D_IMAGE);
            
            if (img == null && !CoreUtilities.isEmpty(pic.getUrl())) {
                o = new DcImageIcon(pic.getUrl());
            } else {
                o = img;
            }
        }
        
        flushImage();
        initialize();
        
        try {
            if (o == null) {
                picture = null;
            } else {
                if (o instanceof URL) {
                    URL url = (URL) o;
                    picture = new DcImageIcon(url.getFile());
                } else if (o instanceof String) {
                    String value = (String) o;
                    if (value.endsWith("jpg")) {
                        picture = new DcImageIcon(value);
                    } else {
                        String base64 = (String) o;
                        if (base64.length() > 0)
                            picture = new DcImageIcon(Base64.decode(base64.toCharArray()));
                    }
                } else if (o instanceof DcImageIcon) {
                    picture = (DcImageIcon) o;
                    picture.setImage(picture.getImage());
                }
            }
        } catch (Exception e) {
            logger.error(DcResources.getText("msgCouldNotLoadPicture"), e);
        }
        
        initialize();
        revalidate();
        repaint();
    }

    public boolean isChanged() {
        return changed;
    }
    
    @Override
    public void clear() {
        flushImage();
        imageWidth = -1;
        imageHeight = -1;
        picture = null;
        size = null;
        menu = null;
        img = null;
    } 
    
    public void flushImage() {
        if (picture != null)
            picture.getImage().flush();

        if (img != null)
            img.flush();
    }
    
    public boolean isEmpty() {
    	return img == null;
    }

    @Override
    public Object getValue() {
    	int width = picture != null ? picture.getIconWidth() : 0;
    	int height = picture != null ? picture.getIconHeight() : 0;
    	
    	if (width == 0 || height == 0)
    		return null;
    	else
    		return picture;
    }

    private boolean scalingAllowed(int width, int height) {
        return scaled && 
              ((height >= 50 && width >= 50) || 
               (imageWidth > size.width || imageHeight > size.height));
    }

    private class PicturePane extends JComponent implements ImageObserver {
        
        private PicturePane() {}
        
        @Override
        protected void paintComponent(Graphics g) {
            
            super.paintComponent(g);

            if (picture != null) {
                try {
                    img = picture.getImage();
                    
                    // less expensive way to prepare the image (using the default instance)
                    if (Utilities.getToolkit().prepareImage(img, imageWidth, imageHeight, this))
                        paintImage(g);
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }
        }
        
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
            paintImage(getGraphics());
            return true;
        }
        
        private void paintImage(Graphics g) {
            
            if (g == null || picture == null || img == null) return;
            
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            int width = imageWidth;
            int height = imageHeight;

            size = getSize(size);
            if (scalingAllowed(imageWidth, imageHeight)) {
                width =  Math.min(size.width, imageWidth);
                height = Math.min(size.height, imageHeight);
                double scaledRatio = (double) width / (double) height;
                double imageRatio = (double) imageWidth / (double) imageHeight;
            
                if (scaledRatio < imageRatio) {
                    height = (int) (width / imageRatio);
                } else {
                    width = (int) (height * imageRatio);
                }
            }

            g.translate((getWidth() - width) / 2, (getHeight() - height) / 2);
            g.drawImage(img, 0, 0, width, height, null);
            g.dispose();
        }     
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    
    private void openImageFromURL() {
        OpenFromUrlDialog dialog = new OpenFromUrlDialog();
        dialog.setVisible(true);
        
        byte[] image = dialog.getImage();
        if (image != null) {
            picture = new DcImageIcon(image);
            initialize();
            changed = true;
            dialog.setImage(null);
        }
    }
    
    private void openImage() {
        PictureDialog dlg = new PictureDialog(picture);
        
        if (dlg.isPictureChanged()) {
            picture = dlg.getImage();
            initialize();
            changed = true;
            repaint();
            revalidate();
        }
    }
    
    private void openImageFromFile() {
        try {
            BrowserDialog dialog = new BrowserDialog("Select a new Image", new PictureFileFilter());
            File file = dialog.showOpenFileDialog(this, null);
            if (file != null && file.isFile()) {
                
                if (file.toString().toLowerCase().endsWith(".svg")) {
                    SVGtoBufferedImageConverter converter = new SVGtoBufferedImageConverter();
                    BufferedImage bi = converter.renderSVG(file.toString());
                    picture = new DcImageIcon(CoreUtilities.getBytes(new DcImageIcon(bi)));
                } else {
                    String filename = file.toString().toLowerCase();
                    picture = new DcImageIcon(CoreUtilities.getBytes(
                            Toolkit.getDefaultToolkit().createImage(Utilities.readFile(file)), 
                            filename.endsWith(".png") ? DcImageIcon._TYPE_PNG : DcImageIcon._TYPE_JPEG));
                }
                
                initialize();
                changed = true;
                
                if (DcSettings.getBoolean(DcRepository.Settings.stDeleteImageFileAfterImport)) 
                    file.delete();
            }
        } catch (Exception e) {
            logger.error("An error occured while reading the image", e);
        }
    }
    
    private void saveToFile() {
        if (picture != null) {
            BrowserDialog dlg = new BrowserDialog(DcResources.getText("lblSelectFile"), 
                                                  new DcFileFilter("jpg"));
            
            File file = dlg.showCreateFileDialog(this, null);

            try {
                if (file != null) {
                    String filename = file.toString();
                    filename += filename.toLowerCase().endsWith("jpg") || filename.toLowerCase().endsWith("jpeg") ? "" : ".jpg";
                    CoreUtilities.writeToFile(picture, new File(filename));
                }
            } catch (Exception e) {
                GUI.getInstance().displayErrorMessage(CoreUtilities.isEmpty(e.getMessage()) ? e.toString() : e.getMessage());
                logger.error("An error occurred while saving the image", e);
            }
        }
    }
    
    private void grayscale() {
        img = picture.getImage();
        BufferedImage src = CoreUtilities.toBufferedImage(new DcImageIcon(img));
        BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
        update(op, src);
    }
    
    private void sharpen() {
        img = picture.getImage();
        BufferedImage src = CoreUtilities.toBufferedImage(new DcImageIcon(img));
        BufferedImageOp op = new ConvolveOp(
                new Kernel(3, 3, new float[] { 0.0f, -0.75f, 0.0f, -0.75f, 4.0f, 
                                              -0.75f, 0.0f, -0.75f, 0.0f }));
        update(op, src);
    }
    
    private void blur() {
        img = picture.getImage();
        BufferedImage src = CoreUtilities.toBufferedImage(new DcImageIcon(img));
        BufferedImageOp op = new ConvolveOp(
                new Kernel(3, 3, new float[] {.1111f, .1111f, .1111f, .1111f, .1111f, 
                                              .1111f, .1111f, .1111f, .1111f, }));
        update(op, src);
    }
    
    private void update(BufferedImageOp op, BufferedImage src) {
        picture = new DcImageIcon(CoreUtilities.getBytes(new DcImageIcon(op.filter(src, null))));
        initialize();
        changed = true;
        repaint();
        revalidate();
    }
    
    private void rotate(int degrees) {
        img = picture.getImage();
        
        BufferedImage src = CoreUtilities.toBufferedImage(new DcImageIcon(img));
        AffineTransform at = new AffineTransform();
        
        at.rotate(Math.toRadians(degrees), src.getWidth() / 2.0, src.getHeight() / 2.0);
        AffineTransform translationTransform = findTranslation (at, src);
        at.preConcatenate(translationTransform);
        BufferedImage destinationBI = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC).filter(src, null);

        picture = new DcImageIcon(CoreUtilities.getBytes(new DcImageIcon(destinationBI)));
        initialize();
        changed = true;
        repaint();
        revalidate();
    }
    
    /*
     * Find proper translations to keep rotated image correctly displayed
     */
    private AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
      Point2D p2din = new Point2D.Double (0.0, 0.0);
      Point2D p2dout = at.transform (p2din, null);
      double ytrans = p2dout.getY();

      p2din = new Point2D.Double(0, bi.getHeight());
      p2dout = at.transform(p2din, null);
      double xtrans = p2dout.getX () ;

      AffineTransform tat = new AffineTransform();
      tat.translate(-xtrans, -ytrans);
      
      return tat;
    }    
    
    private void initialize() {
        
        repaint();
        
        if (picture != null) {
            imageWidth = picture.getIconWidth();
            imageHeight = picture.getIconHeight();
        } else {
            imageWidth = -1;
            imageHeight = -1;
        }
    }    
    
    @Override
    public void setEditable(boolean b) {
    	setEnabled(b);
    	if (!b) {
    		remove(menu);
    	}
    }
    
    private void paste() {
        DcImageIcon icon = Utilities.getImageFromClipboard();
        if (icon != null) {
            picture = icon;
            initialize();
            changed = true;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	if (!isEnabled())
    		return;
    	
        String action = e.getActionCommand();
        if (action.equals("open_from_file")) {
            openImageFromFile();
        } else if (action.equals("open_from_url")) {
            openImageFromURL();
        } else if (action.equals("Save as")) {
            saveToFile();
        } else if (action.equals("delete")) {
            setValue(null);
            changed = true;
        } else if (action.equals("rotate_right")) {
            rotate(90);
        } else if (action.equals("rotate_left")) {
            rotate(90);
            rotate(90);
            rotate(90);
        } else if (action.equals("grayscale")) {
            grayscale();
        } else if (action.equals("sharpen")) {
            sharpen();
        } else if (action.equals("blur")) {
            blur();
        } else if (action.equals("open_from_clipboard")) {
            paste();
        }
    }

    @Override
    public JToolTip createToolTip() {
        return new DcMultiLineToolTip();
    }
    
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
    	if (!isEnabled())
    		return;
    	
        if (e.getClickCount() == 2) {
            if (picture == null)
                openImageFromFile();
            else 
                openImage();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void refresh() {}

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clear();
    }
}