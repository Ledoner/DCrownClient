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

package net.datacrow.synchronizers;

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Book;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.synchronizers.DefaultSynchronizer;
import net.datacrow.core.synchronizers.Synchronizer;
import net.datacrow.core.utilities.isbn.ISBN;

import org.apache.log4j.Logger;

public class BookSynchronizer extends DefaultSynchronizer {

    private static final long serialVersionUID = 2238502740426525515L;

    private static Logger logger = Logger.getLogger(BookSynchronizer.class.getName());
    
    public BookSynchronizer() {
        super(DcResources.getText("lblMassItemUpdate", DcModules.get(DcModules._BOOK).getObjectName()),
              DcModules._BOOK);
    }
    
    @Override
	public Synchronizer getInstance() {
		return new BookSynchronizer();
	}
    
    @Override
    public String getHelpText() {
        return DcResources.getText("msgBookMassUpdateHelp");
    }

    @Override
    protected boolean matches(DcObject result, String searchString, int fieldIdx) {
        boolean matches = false;
        
        try {
            
            boolean isISBN10 = ISBN.isISBN10(searchString);
            boolean isISBN13 = ISBN.isISBN13(searchString);
            
            String check = null;
            if (isISBN10 || isISBN13) {
                check = isISBN10 ? ISBN.getISBN13(searchString) : searchString;
            } else {
                check = dco.isFilled(Book._N_ISBN13) ? (String) dco.getValue(Book._N_ISBN13) :
                        dco.isFilled(Book._J_ISBN10) ? ISBN.getISBN13((String) dco.getValue(Book._J_ISBN10)) :
                        null;
            }
            
            if (check != null) {
                String isbn = (String) result.getValue(Book._N_ISBN13);
                matches = check.equals(isbn);
            }

        } catch (Exception e) {
            logger.error(e, e);
        }
        
        return matches ? true : super.matches(result, searchString, fieldIdx); 
    }
}
