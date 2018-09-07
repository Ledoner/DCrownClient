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

package net.datacrow.console.views;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListSelectionListener;

import net.datacrow.core.modules.DcModule;
import net.datacrow.core.objects.DcObject;

public interface IViewComponent extends ISortableComponent {
    
    void clear();
    
    View getView();
    void setView(View view);
    
    DcModule getModule();
    
    int getItemCount();
    
    Dimension getSize();
    void setSize(Dimension d);
    
    boolean remove(String[] keys);
    void remove(int[] indices);
    
    void clear(int index);
    
    void setIgnorePaintRequests(boolean b);
    boolean isIgnoringPaintRequests();
    
    int getFirstVisibleIndex();
    int getLastVisibleIndex();
    int getViewportBufferSize();
    
    int add(String key);
    int add(DcObject item);
    void add(List<? extends DcObject> items);
    void add(Map<String, Integer> keys);
    
    List<String> getItemKeys();
    List<DcObject> getItems();
    DcObject getItemAt(int idx);
    DcObject getItem(String ID);
    String getItemKey(int idx);
    int getModule(int idx);
    
    void setSelected(int index);
    
    void ignoreEdit(boolean b);

    List<String> getSelectedItemKeys();
    int[] getSelectedIndices();
    int getSelectedIndex();
    DcObject getSelectedItem();

    int getIndex(String ID);
    
    void deselect();
    
    int update(String ID);
    int update(String ID, DcObject dco);
    void afterUpdate();

    int[] getChangedIndices();
    void undoChanges();
    boolean isChangesSaved();
    
    void setSelectionMode(int mode);
    
    void cancelEdit();
    int locationToIndex(Point point);    
    void setCursor(Cursor cursor);
    
    boolean allowsHorizontalTraversel();
    boolean allowsVerticalTraversel();
    
    void applySettings();
    void saveSettings();

    // LISTENERS
    void addSelectionListener(ListSelectionListener lsl);
    void removeSelectionListener(ListSelectionListener lsl);
    
    void addKeyListener(KeyListener kl);
    void addMouseListener(MouseListener ml);
    void removeMouseListener(MouseListener ml);
    MouseListener[] getMouseListeners();
    
    void activate();
    
    void paintRegionChanged();
    void repaint();
    void revalidate();
}
