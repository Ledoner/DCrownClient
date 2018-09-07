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

package net.datacrow.console.components.painter;

import java.awt.*;
import javax.swing.text.*;

/**
 *  Implements a simple highlight painter that renders a rectangle around the
 *  area to be highlighted.
 */
public class RectanglePainter extends
    DefaultHighlighter.DefaultHighlightPainter {
    public RectanglePainter(Color color) {
        super(color);
    }

    @Override
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
        Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

        if (r == null)
            return null;

        Color color = getColor();
        g.setColor(color == null ? c.getSelectionColor() : color);
        g.fillRect(r.x, r.y, r.width - 1, r.height -1); //, 4, 4);

        return r;
    }

    private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            Rectangle alloc;

            if (bounds instanceof Rectangle) {
                alloc = (Rectangle) bounds;
            } else {
                alloc = bounds.getBounds();
            }

            return alloc;
        } else {
            try {
                Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                Rectangle r = (shape instanceof Rectangle) ? (Rectangle) shape: shape.getBounds();
                return r;
            } catch (BadLocationException e) {
            }
        }

        return null;
    }
}
