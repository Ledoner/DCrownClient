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

package net.datacrow.console.components.lists;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ListUI;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.lists.elements.DcListElement;

import org.apache.log4j.Logger;

public class DcList extends JList implements ComponentListener {
    
    private static Logger logger = Logger.getLogger(DcList.class.getName());

    private static final String  uiClassID = "DcListUI";
    private int columnsPerRow = 1;
    private int visibleColumnCount = 1;
    
    static {
        UIDefaults ui = UIManager.getDefaults();
        if(!ui.contains(uiClassID))
            ui.put(uiClassID, "net.datacrow.console.components.lists.DcListUI");
    }
    
    public DcList(DcListModel model) {
        super(model);
        addComponentListener(this);
        ComponentFactory.setBorder(this);
        setBackground(ComponentFactory.getDisabledColor());
        setMinimumSize(new Dimension(0,0));
    }    
    
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    public int getVisibleColumnCount() {
        return visibleColumnCount;
    }
    
    public void setVisibleColumnCount(int visibleColumnCount) {
        int oldValue = this.visibleColumnCount;
        this.visibleColumnCount = Math.max(0, visibleColumnCount);
        firePropertyChange("visibleColumnCount", oldValue, visibleColumnCount);
    }
    
    public int getColumnsPerRow() {
        return columnsPerRow;
    }
    
    public void moveRowUp() {
        if (getSelectedIndex() > 0)
            moveSelectedRow(getSelectedIndex() -1);
    }
    
    public void moveRowDown() {
        if (getSelectedIndex() > -1)
            moveSelectedRow(getSelectedIndex() < getDcModel().getSize() -1 ? getSelectedIndex() + 1 : 0);
    }

    public void moveRowToTop() {
        if (getSelectedIndex() > -1)
            moveSelectedRow(0);
    }

    public void moveRowToBottom() {
        if (getSelectedIndex() > -1)
            moveSelectedRow(getDcModel().getSize() -1);
    }

    private void moveSelectedRow(int toIdx) {
        Vector<Object> v = new Vector<Object>();
        for (int i = 0; i < getDcModel().getSize(); i++) {
            v.add(getDcModel().get(i));
        }

        Object o = getSelectedValue();
        v.remove(o);
        v.add(toIdx, o);
        setListData(v);
        setSelectedIndex(toIdx);
    }
    
    public void setColumnsPerRow(int columnsPerRow) {
        int oldValue = this.columnsPerRow;
        this.columnsPerRow = Math.max(1, columnsPerRow);
        if(this.columnsPerRow != oldValue)
            firePropertyChange("columnsPerRow", oldValue, columnsPerRow);
    }    

    @Override
    public int locationToIndex(Point location) {
        return ((DcListUI) ui).locationToIndex(this, location);
    }

    public int locationToNearestIndex(Point location) {
        ListUI ui = getUI();
        if(ui == null || !(ui instanceof DcListUI))
            return -1;

        return ((DcListUI) ui).locationToNearestIndex(this, location);
    }
    
    public void setMaxVisibleColumnsPerRow() {
        Rectangle r = getVisibleRect();
        DcListUI ui = (DcListUI)getUI();
        int newMaxColPerRow = ui.getMaxColumnsPerRow(r.width);
        if(newMaxColPerRow != columnsPerRow)
            setColumnsPerRow(newMaxColPerRow);
    }    
    
    @Override
    public int getLastVisibleIndex() {
        Rectangle r = getVisibleRect();
        Point visibleLR = new Point(0, (r.y + r.height) - 1);
        int index = locationToNearestIndex(visibleLR);
        return index;
    }
    
    public int getFirstIndexOnLastVisibleRow() {
        Rectangle r = getVisibleRect();
        Point visibleLR = new Point(0, (r.y + r.height) - 1);
        return locationToNearestIndex(visibleLR);
    }    
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        
        if (getLayoutOrientation() != VERTICAL)
            return getPreferredSize();
        
        Insets insets = getInsets();
        int dx = insets.left + insets.right;
        int dy = insets.top  + insets.bottom;

        int visibleRowCount    = getVisibleRowCount();
        int visibleColumnCount = getVisibleColumnCount();
        int fixedCellWidth     = getFixedCellWidth();
        int fixedCellHeight    = getFixedCellHeight();

        try {
            if ((fixedCellWidth > 0) && (fixedCellHeight > 0)) {
                int width  = (visibleColumnCount * fixedCellWidth)+ dx;
                int height = (visibleRowCount * fixedCellHeight) + dy;
                
                return new Dimension(width, height);
            } else if (getModel().getSize() > 0) {
                Rectangle r = getCellBounds(0, 0);
                int width  = (visibleColumnCount * r.width) + dx;
                int height = (visibleRowCount * r.height) + dy;
                
                return new Dimension(width, height);
            } else {
                fixedCellWidth = (fixedCellWidth > 0) ? fixedCellWidth : 256;
                fixedCellHeight = (fixedCellHeight > 0) ? fixedCellHeight : 16;
                return new Dimension(fixedCellWidth, fixedCellHeight * visibleRowCount);
            }
        } catch (Exception exp) {
            return new Dimension(fixedCellWidth, fixedCellHeight * visibleRowCount);
        }
    } 

    private void applyScrollableUnitIncrement(int increment) {
        try {
            // Fix for the continuous scrolling issue (not very elegant..)
            
            JViewport vp = (JViewport) getParent();
            JScrollPane sp = (JScrollPane) vp.getParent();
            JScrollBar sb = sp.getVerticalScrollBar();
            
            if (increment != 0)
                sb.setUnitIncrement(increment);
        } catch (Exception e) {
            logger.warn("Could not set the incrememt for the scrollbar", e);
        }
    }
    
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        
        int firstIndex = getFirstVisibleIndex();
        if (getLayoutOrientation() == JList.VERTICAL_WRAP) {
            applyScrollableUnitIncrement(40);
            return 40;
        }
        
        int increment;
        int row    = firstIndex / columnsPerRow;
        int column = firstIndex % columnsPerRow;

        if (orientation == SwingConstants.HORIZONTAL) {

            if (direction > 0) {
                Rectangle r = getCellBounds(firstIndex, firstIndex);
                increment = (r == null) ? 0 : r.width - (visibleRect.x - r.x);
            } else {
                Rectangle r = getCellBounds(firstIndex, firstIndex);

                if ((r.x == visibleRect.x) && (column == 0)) {
                    increment = 0;
                } else if (r.x == visibleRect.x) {
                   Rectangle prevR = getCellBounds(firstIndex - 1, firstIndex - 1);
                   increment = (prevR == null) ? 0 : prevR.width;
                } else {
                    increment = visibleRect.x - r.x;
                }
            }
        } else {
            if (direction > 0) {
                Rectangle r = getCellBounds(firstIndex, firstIndex);
                increment = (r == null) ? 0 : r.height - (visibleRect.y - r.y);
            } else {
                Rectangle r = getCellBounds(firstIndex, firstIndex);
                
                if (r == null) {
                    increment = 0;
                } else if ((r.y == visibleRect.y) && (row == 0))  {
                    increment = 0;
                } else if (r.y == visibleRect.y) {
                    Rectangle prevR = getCellBounds(firstIndex - 1, firstIndex - 1);
                    increment = (prevR== null) ? 0 : prevR.height;
                } else {
                    increment = visibleRect.y - r.y;
                }
            }
        }
        
        increment = increment > 0 && increment < 10 ? 40 : increment;
        
        applyScrollableUnitIncrement(increment);
        
        return increment;
    }
    
    @Override
    public void setSelectionMode(int selectionMode) {
        getSelectionModel().setSelectionMode(selectionMode);        
    }

    @Override
    public void setListData(Vector v) {
        addElements(v);
    }
    
    public void addElements(List elements) {
        getDcModel().setSize(0);
        for (Object element : elements)
            getDcModel().addElement(element);
    }
    
    public DcListModel getDcModel() {
        if (!(getModel() instanceof DcListModel))
            setModel(new DcListModel());
        
        return (DcListModel) getModel();
    }
    
    public List<DcListElement> getElements() {
        List<DcListElement> elements = new ArrayList<DcListElement>();
        for (int i = 0 ; i < getDcModel().getSize(); i++)
            elements.add((DcListElement) getDcModel().getElementAt(i));

        return elements;
    }    
    
    public void update() {
        DcListElement element;
        for (int i = 0 ; i < getDcModel().getSize(); i++) {
            element = (DcListElement) getDcModel().getElementAt(i);
            element.update();
        }
        repaint();
    }
    
    public void remove() {
        if (getDcModel().size() == 1 && getSelectedIndex() != -1)
            getDcModel().setSize(0);
        else 
            getDcModel().remove(getSelectedIndex());
    }

    public void remove(int[] indices) {
        for (int i = indices.length; i > 0; i--) {
            try {
                getDcModel().removeElementAt(indices[i - 1]);
            } catch (Exception e) {}
        }
    }    
    
    public void clear() {
        getDcModel().clear();
        getSelectionModel().clearSelection();
    }
    
    @Override
    public void ensureIndexIsVisible(int index) {
        try {
            super.ensureIndexIsVisible(index);
        } catch (Exception e) {
            logger.warn(e, e);            
        }
    }
    
    public void setListenersEnabled(boolean b) {
        getDcModel().setListenersEnabled(b);        
    }
    
    @Override
    public void componentHidden(ComponentEvent ce) {}
    @Override
    public void componentShown(ComponentEvent ce) {}
    @Override
    public void componentMoved(ComponentEvent ce) {}

    @Override
    public void componentResized(ComponentEvent ce) {
        setMaxVisibleColumnsPerRow();
    }
}
