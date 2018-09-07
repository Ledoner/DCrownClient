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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.basic.BasicListUI;

import net.datacrow.console.components.lists.elements.DcListElement;

import org.apache.log4j.Logger;

public class DcListUI extends BasicListUI {
    
    private static Logger logger = Logger.getLogger(DcListUI.class.getName());
    
    protected final static int columnsPerRowChanged = cellRendererChanged << 1;
    protected int columnsPerRow = 1;
    protected int[] cellWidths    = null;

    /**
     * Paint the rows that intersect the Graphics objects clipRect.
     * This method calls paintCell as necessary.  Subclasses
     * may want to override these methods. 
     *
     * @see #paintCell
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        
        try {
            try {
                maybeUpdateLayoutState();
            } catch (Exception e) {
                logger.error(e, e);
            }

            ListCellRenderer renderer = list.getCellRenderer();
            ListModel dataModel = list.getModel();
            ListSelectionModel selModel = list.getSelectionModel();
            if ((renderer == null) || (dataModel.getSize() == 0))
                return;

            // Compute the area we're going to paint in terms of the affected
            // columns/rows (firstPaintColumn, firstPaintRow, lastPaintColumn,
            // lastPaintRow), and the clip bounds.
            Rectangle paintBounds = g.getClipBounds();
   
            int firstPaintColumn = convertXToColumn(paintBounds.x);
            int firstPaintRow = convertYToRow(paintBounds.y);
            int lastPaintColumn = convertXToColumn((paintBounds.x+ paintBounds.width) - 1);
            int lastPaintRow = convertYToRow((paintBounds.y + paintBounds.height) - 1);
   
            if (firstPaintRow == -1)
                firstPaintRow = 0;

            if (lastPaintRow == -1)
                lastPaintRow = (dataModel.getSize()-1)/columnsPerRow;
            
            if (firstPaintColumn == -1)
                firstPaintColumn = 0;

            if (lastPaintColumn == -1)
                lastPaintColumn = columnsPerRow-1;
   
            Rectangle itemBounds = getCellBoundsPerColumnRow(list, firstPaintColumn, firstPaintRow);
            if (itemBounds == null)
                return;
   
            int leadIndex = list.getLeadSelectionIndex();
            int startItemBoundX = itemBounds.x;
   
            for(int row = firstPaintRow; row <= lastPaintRow; row++) {
                itemBounds.x = startItemBoundX;

                for(int column = firstPaintColumn; column <= lastPaintColumn; column++) {
                    int index = row * columnsPerRow + column;
                    if(index >= dataModel.getSize())
                        break;
                    
                    itemBounds.width  = getItemWidth(index);
                    itemBounds.height = getItemHeight(index);
   
                    // Set the clip rect to be the intersection of rowBounds
                    // and paintBounds and then paint the cell.
                    g.setClip(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
                    g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width, paintBounds.height);
                    
                    try {
                        paintCell(g, index, itemBounds, renderer, dataModel, selModel, leadIndex);
                    } catch (Exception exp) {
                        // An exception occured. Probably an incorrect element. Rebuild and retry
                    	
                        try {
	                        list.setSelectedIndex(index);
	                        ((DcListElement) list.getSelectedValue()).update();
	                        paintCell(g, index, itemBounds, renderer, dataModel, selModel, leadIndex);
                        } catch (Exception e) {
                            logger.debug("Error while painting cell in DcListUI", e);
                        	return;
                        }
                    }
                    
                    itemBounds.x += itemBounds.width;
                }
                itemBounds.y += itemBounds.height;
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }


    /**
     * The preferredSize of a list is total height of the rows
     * and the maximum width of the cells.  If JList.fixedCellHeight
     * is specified then the total height of the rows is just
     * (cellVerticalMargins + fixedCellHeight) * model.getSize() where
     * rowVerticalMargins is the space we allocate for drawing
     * the yellow focus outline.  Similarly if JListfixedCellWidth is
     * specified then we just use that plus the horizontal margins.
     *
     * @param c The JList component.
     * @return The total size of the list.
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        maybeUpdateLayoutState();

        int lastItem = list.getModel().getSize() - 1;
        if (lastItem < 0)
            return new Dimension(0, 0);

        Insets insets = list.getInsets();

        int width  = convertItemToX(columnsPerRow-1) + getItemWidth(lastItem) + insets.right;
        int height = convertItemToY(lastItem) + getItemHeight(lastItem) + insets.bottom;
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * Returns a new instance of Basic2DListUI.  Basic2DListUI delegates are
     * allocated one per JList.
     *
     * @return A new ListUI implementation for the Windows look and feel.
     */
    public static ComponentUI createUI(JComponent list) {
        return new DcListUI();
    }

    /**
     * @return The index of the cell at location, or -1.
     * @see ListUI#locationToIndex
     */
    @Override
    public int locationToIndex(JList list, Point location) {
        maybeUpdateLayoutState();
        return convertXYToIndex(location.x, location.y);
    }

    /**
     * @return The origin of the index'th cell, null if index is invalid.
     * @see ListUI#indexToLocation
     */
    @Override
    public Point indexToLocation(JList list, int index) {
        maybeUpdateLayoutState();
        int x = convertItemToX(index);
        int y = convertItemToY(index);
        return ((y == -1) || (x == -1)) ? null : new Point(x, y);
    }

    public int locationToNearestIndex(DcList list, Point p) {
        // Determine the nearest index == center of index that would
        // be the most logical position to insert an item
        int index = locationToIndex(list, p);
        if (index != -1) {
            Rectangle cell = getCellBounds(list, index, index);
            if(cell != null && (cell.getX()+cell.getWidth()/2<p.getX()))
                index++;
        } else {
            //It is possible, that we are on some white space on the
            //right of the List check this by calculating the row
            int row = convertYToRow(p.y);
            //The insertion index is the first Item on the next row
            if(row != -1)
                index = (row+1) * columnsPerRow;
        }

        if (index >= list.getModel().getSize())
            index = -1;

        return index;
    }


    public Rectangle getCellBoundsPerColumnRow(JList list, int column, int row) {
        int index = row * columnsPerRow + column;
        return getCellBounds(list, index, index);
    }

    /**
     * @return The bounds of the index'th cell.
     * @see ListUI#getCellBounds
     */
    @Override
    public Rectangle getCellBounds(JList list, int index1, int index2) {
        maybeUpdateLayoutState();


        int minIndex = Math.min(index1, index2);
        int maxIndex = Math.max(index1, index2);
        int minX = convertItemToX(minIndex);
        int maxX = convertItemToX(maxIndex);
        int minY = convertItemToY(minIndex);
        int maxY = convertItemToY(maxIndex);

        if ((minX == -1) || (maxX == -1) || (minY == -1) || (maxY == -1))
            return null;

        int x = minX;
        int y = minY;
        int w = (maxX + getItemWidth(maxIndex)) - minX;
        int h = (maxY + getItemHeight(maxIndex)) - minY;
        return new Rectangle(x, y, w, h);
    }

    @Override
    protected int getRowHeight(int row) {
        return getItemHeight(row);
    }

    protected int getItemHeight(int index) {
        if ((index < 0) || (index >= list.getModel().getSize()))
            return -1;
        
        int row = index/columnsPerRow;
        return (cellHeights == null) ? cellHeight : ((row < cellHeights.length) ? cellHeights[row] : -1);
    }

    protected int getItemWidth(int index) {
        if ((index < 0) || (index >= list.getModel().getSize()))
            return -1;
    
        int column = index%columnsPerRow;
        return (cellWidths == null) ? cellWidth : ((column < cellWidths.length) ? cellWidths[column] : -1);
    }


    protected int convertXYToIndex(int x, int y) {
        int column = convertXToColumn(x);
        int row    = convertYToRow(y);
        
        column = column == -1 ? 0 : column;
        row = row == -1 ? 0 : row;

        int index = row * columnsPerRow + column;
        //Due to independent calculation of column/row
        //the index can be higher as the listSize
        if(index >= list.getModel().getSize())
            return -1;

        return index;
    }

    /**
     * Convert the JList relative coordinate to the row that contains it,
     * based on the current layout.  If y0 doesn't fall within any row,
     * return -1.
     *
     * @return The row that contains y0, or -1.
     * @see #getRowHeight
     * @see #updateLayoutState
     */
    @Override
    protected int convertYToRow(int y0) {
        int nItems = list.getModel().getSize();
        Insets insets = list.getInsets();

        if (nItems <= 0)
            return -1;

        int rowCount = (nItems-1)/columnsPerRow+1;

        if (cellHeights == null) {
            int row = (cellHeight == 0) ? 0 : ((y0 - insets.top) / cellHeight);
            return ((row < 0) || (row >= rowCount)) ? -1 : row;
        } else if (rowCount > cellHeights.length) {
            return -1;
        } else {
            int y = insets.top;
            int row = 0;

            for(int i = 0; i < rowCount; i++) {
                if ((y0 >= y) && (y0 < y + cellHeights[i]))
                    return row;

                y += cellHeights[i];
                row += 1;
            }
            
            return -1;
        }
    }

    protected int convertXToColumn(int x0) {
        int nItems = list.getModel().getSize();
        Insets insets = list.getInsets();

        if (nItems <= 0)
            return -1;

        int columnCount = Math.min(nItems, columnsPerRow);
    
        if (cellWidths == null) {
            int column = (cellWidth == 0) ? 0 : ((x0 - insets.left) / cellWidth);
            return ((column < 0) || (column >= columnCount)) ? -1 : column;
        } else if (columnCount > cellWidths.length) {
            return -1;
        } else {
            int x = insets.left;
            int column = 0;
    
            for(int i = 0; i < columnCount; i++) {
                if ((x0 >= x) && (x0 < x + cellWidths[i]))
                    return column;
            
                x += cellWidths[i];
                column += 1;
            }
            return -1;
        }
    }

    @Override
    protected int convertRowToY(int row) {
        return convertItemToY(row);
    }

    protected int convertItemToY(int index) {
        int nItems = list.getModel().getSize();
        Insets insets = list.getInsets();

        if ((nItems < 0) || (index >= nItems))
            return -1;

        int row = index / columnsPerRow;
    
        if (cellHeights == null) {
            return insets.top + (cellHeight * row);
        } else if (row >= cellHeights.length) {
            return -1;
        } else {
            int y = insets.top;
            for(int i = 0; i < row; i++) {
                y += cellHeights[i];
            }
            return y;
        }
    }

    protected int convertItemToX(int index) {
        int nItems = list.getModel().getSize();
        Insets insets = list.getInsets();
    
        if ((nItems < 0) || (index >= nItems))
            return -1;
    
        int column = index % columnsPerRow;
    
        if (cellWidths == null) {
            return insets.top + (cellWidth * column);
        } else if (column >= cellWidths.length) {
            return -1;
        } else {
            int x = insets.left;
            for(int i = 0; i < column; i++) {
                x += cellWidths[i];
            }
            return x;
        }
    }


    /**
     * Recompute the value of cellHeight or cellHeights based
     * and cellWidth, based on the current font and the current
     * values of fixedCellWidth, fixedCellHeight, and prototypeCellValue.
     *
     * @see #maybeUpdateLayoutState
     */
    @Override
    protected void updateLayoutState() {
        try {
            // If both JList fixedCellWidth and fixedCellHeight have been
            // set, then initialize cellWidth and cellHeight, and set
            // cellHeights to null.
            columnsPerRow = ((DcList) list).getColumnsPerRow();
            int fixedCellHeight = list.getFixedCellHeight();
            int fixedCellWidth  = list.getFixedCellWidth();
            int nItems = list.getModel().getSize();
        
            if (fixedCellWidth != -1) {
                cellWidth = fixedCellWidth;
                cellWidths = null;
            } else {
                cellWidth = -1;
                cellWidths = new int[Math.min(nItems, columnsPerRow)];
            } 
        
            if (fixedCellHeight != -1) {
                cellHeight = fixedCellHeight;
                cellHeights = null;
            } else {
                cellHeight = -1;
                cellHeights = new int[(nItems-1)/columnsPerRow+1];
            }
        
            // If either of  JList fixedCellWidth and fixedCellHeight haven't
            // been set, then initialize cellWidth and cellHeights by
            // scanning through the entire model.  Note: if the renderer is
            // null, we just set cellWidth and cellHeights[*] to zero,
            // if they're not set already.
            if ((fixedCellWidth == -1) || (fixedCellHeight == -1)) {
        
                ListModel dataModel = list.getModel();
                int dataModelSize = dataModel.getSize();
                ListCellRenderer renderer = list.getCellRenderer();
        
                if (renderer != null) {
                    for(int index = 0; index < dataModelSize; index++) {
                        Object value = dataModel.getElementAt(index);
                        Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
                        rendererPane.add(c);
                        Dimension cellSize = c.getPreferredSize();
                        int column = index%columnsPerRow;
                        int row    = index/columnsPerRow;
                        if (fixedCellWidth == -1)
                            cellWidths[column] = Math.max(cellWidths[column], cellSize.width);
                        
                        if (fixedCellHeight == -1)
                            cellHeights[row] = Math.max(cellHeights[row], cellSize.height);
                    }
                } else {
                    if (fixedCellWidth == -1) {
                        for(int index = 0; index < cellWidths.length; index++) {
                            cellWidths[index] = 0;
                        }
                    }
                    
                    if (fixedCellHeight == -1) {
                        for(int index = 0; index < cellHeights.length; index++) {
                            cellHeights[index] = 0;
                        }
                    }
                }
            }
        
            list.invalidate();
        } catch (Exception e) {
            //logger.debug("Layout", e);
        }
    }

    protected int getMaxColumnsPerRow(int width) {
        int maxColumns = -1;
        
        try {
            int fixedCellWidth  = list.getFixedCellWidth();
            
            if (fixedCellWidth != -1) {
                maxColumns = width/fixedCellWidth;
            } else {
                ListModel dataModel = list.getModel();
                int nItems = dataModel.getSize();
                ListCellRenderer renderer = list.getCellRenderer();
        
                if (renderer != null) {
                    Dimension[] cellDims = new Dimension[nItems];
                    for(int index = 0; index < nItems; index++) {
                        Object value = dataModel.getElementAt(index);
                        Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
                        rendererPane.add(c);
                        cellDims[index] = c.getPreferredSize();
                    }
    
                    //Reduce until all widhts on all rows fit
                    maxColumns = cellDims.length > 0 ? getMaxItemsBasedOnFirstRow(cellDims, width) : 0;
                    int index = maxColumns;
                    while(index < nItems && maxColumns > 0) {
                        int newColMax = getMaxItemsBasedOnRow(cellDims, width);
                        if(newColMax != -1 && newColMax<maxColumns) {
                            //Increase only one, because width variable fields
                            //we cannot foresee the result if the items are
                            //reordered in the rows
                            maxColumns = maxColumns-1;
                            index = maxColumns;
                        } else {
                            index += maxColumns;
                        }
                    }
                }
            
                maxColumns = maxColumns == 0 ? 1 : maxColumns;
            }
        } catch (Exception exp) {}

        return maxColumns;
    }


    private int getMaxItemsBasedOnFirstRow(Dimension[] dims, int width) {
        return getMaxItemsBasedOnRow(dims, width);
    }


    private int getMaxItemsBasedOnRow(Dimension[] dims, int width) {
        int localWidth = dims[0].width;
        return width / localWidth; 
    } 

    /**
     * Mouse input, and focus handling for JList.  An instance of this
     * class is added to the appropriate java.awt.Component lists
     * at installUI() time.  Note keyboard input is handled with JComponent
     * KeyboardActions, see installKeyboardActions().
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see #createMouseInputListener
     * @see #installKeyboardActions
     * @see #installUI
     */
    public class MouseInputHandler implements MouseInputListener {
        private boolean ignoreMouseReleased = false;

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            ignoreMouseReleased = false;

            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            if (!list.isEnabled())
                return;

            // Request focus before updating the list selection.  This implies
            // that the current focus owner will see a focusLost() event
            // before the lists selection is updated IF requestFocus() is
            // synchronous (it is on Windows).  See bug 4122345
            if (!list.hasFocus())
                list.requestFocus();

            //ACTION TABLE TO SUPPORT DND FOR MULTIPLE INTERVAL SELECTIONS
            //
            //MousePressed:
            //------------------------------------------------------------
            //         |No Selection   |On Selection     |Out Selection
            //------------------------------------------------------------
            //No key   |set[Item]      |No action        |set[item]
            //------------------------------------------------------------
            //Shift    |set[0,item]*   |set[Anchor,item] |set[anchor,item]
            //------------------------------------------------------------
            //Control  |add/set[item]  |remove[item]     |add[item]
            //------------------------------------------------------------
            //* 0 if there hasn't been any selection, otherwise the anchor
            //No Selection/Out Selection appear to have the same behaviour
        
            
            //MouseReleased:
            //------------------------------------------------------------
            //         |No Selection   |On Selection     |Out Selection
            //------------------------------------------------------------
            //No key   |No action      |Set[item]*       |No action
            //------------------------------------------------------------
            //Shift    |No action      |No action        |No action
            //------------------------------------------------------------
            //Control  |No action      |No action        |No action
            //------------------------------------------------------------
            //* unless there has been a drop
            // No Selection/Out Selection appear to have the same behavior
        
            try {
	            int index = convertXYToIndex(e.getX(), e.getY());
	            if (index != -1) {
	                list.setValueIsAdjusting(true);
	                int anchorIndex = list.getAnchorSelectionIndex();
	                if(anchorIndex == -1) {
	                    anchorIndex = 0;
	                }
	
	                if (e.isShiftDown()) {
	                    //Regardless set from anchor till index
	                    list.setSelectionInterval(anchorIndex, index);
	                    //if(Debug.INFO) Debug.out.println("setSelectionInterval("+anchorIndex+", "+index+")");
	                } else if (e.isControlDown()) {
	                    //We need to toggle the index
	                    if (list.isSelectedIndex(index))
	                        list.removeSelectionInterval(index, index);
	                    else
	                        list.addSelectionInterval(index, index);
	                } else {
	                    //Only action if the index is not Selected!
	                    if (!list.isSelectedIndex(index))
	                        list.setSelectionInterval(index, index);
	                }
	            }
            } catch (Exception exp) {
            	logger.error(exp, exp);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            list.setValueIsAdjusting(false);
            int index = convertXYToIndex(e.getX(), e.getY());
            if(index != -1) {
                if (!ignoreMouseReleased && !e.isShiftDown() && !e.isControlDown() 
                    && list.isSelectedIndex(index)) {
                    //We gave the drag all time, but it didn't
                    //happen, wipe out everything that is selected
                    //and select the current index
                    list.setSelectionInterval(index, index);
                }
            }
        }
    }

    /**
     * Creates a delegate that implements MouseInputListener.
     * The delegate is added to the corresponding java.awt.Component listener 
     * lists at installUI() time. Subclasses can override this method to return 
     * a custom MouseInputListener, e.g.
     *
     * @see MouseInputHandler
     * @see #installUI
     */
    @Override
     protected MouseInputListener createMouseInputListener() {
         return new MouseInputHandler();
     }

    /**
     * The ListSelectionListener that's added to the JLists selection
     * model at installUI time, and whenever the JList.selectionModel property
     * changes.  When the selection changes we repaint the affected rows.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see #createListSelectionListener
     * @see #getCellBounds
     * @see #installUI
     */
    public class ListSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            maybeUpdateLayoutState();
    
            int first = e.getFirstIndex();
            int last  = e.getLastIndex();
    
            int minX = convertItemToX(first);
            int maxX = convertItemToX(last);
    
            int minY = convertItemToY(first);
            int maxY = convertItemToY(last);
    
            //Determine the clipping area
            if ((minX == -1) || (maxX == -1) || (minY == -1) || (maxY == -1)) {
                list.repaint(0, 0, list.getWidth(), list.getHeight());
            } else {
                maxX += getItemWidth(e.getLastIndex());
                maxY += getItemHeight(e.getLastIndex());
                boolean moreThanOneRow = (first/columnsPerRow) != (last/columnsPerRow);
                if( moreThanOneRow)
                    list.repaint(0, minY, list.getWidth(), maxY - minY);
                else
                    list.repaint(minX, minY, maxX - minX, maxY - minY);
            }
        }
    }


    /**
     * Creates an instance of ListSelectionHandler that's added to
     * the JLists by selectionModel as needed.  Subclasses can override
     * this method to return a custom ListSelectionListener, e.g.
     *
     * @see ListSelectionHandler
     * @see #installUI
     */
    @Override
    protected ListSelectionListener createListSelectionListener() {
        return new ListSelectionHandler();
    }

    private void redrawList() {
        list.repaint();
    }

    /**
     * Creates an instance of ListDataListener that's added to
     * the JLists by model as needed.  Subclasses can override
     * this method to return a custom ListDataListener, e.g.
     *
     * @see ListDataListener
     * @see JList#getModel
     * @see #installUI
     */
    @Override
    protected ListDataListener createListDataListener() {
        return new ListDataHandler();
    }

    /**
     * The PropertyChangeListener that's added to the JList at
     * installUI time.  When the value of a JList property that
     * affects layout changes, we set a bit in updateLayoutStateNeeded.
     * If the JLists model changes we additionally remove our listeners
     * from the old model.  Likewise for the JList selectionModel.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see #maybeUpdateLayoutState
     * @see #createPropertyChangeListener
     * @see #installUI
     */
    public class PropertyChangeHandler extends BasicListUI.PropertyChangeHandler {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange(e);
    
            String propertyName = e.getPropertyName();
    
            // If the JList.model property changes, remove our listener,
            // listDataListener from the old model and add it to the new one.
            if (propertyName.equals("columnsPerRow")) {
                updateLayoutStateNeeded |= columnsPerRowChanged;
                redrawList();
            }
        }
    }


    /**
     * Creates an instance of PropertyChangeHandler that's added to
     * the JList by installUI().  Subclasses can override this method
     * to return a custom PropertyChangeListener, e.g.
     *
     * @see PropertyChangeListener
     * @see #installUI
     */
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }
}