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

package net.datacrow.console.windows.help;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.datacrow.console.Layout;
import net.datacrow.console.components.DcHtmlEditorPane;
import net.datacrow.console.windows.NativeDialog;
import net.datacrow.util.Utilities;

public class StartupHelpDialog extends NativeDialog implements ActionListener {

    public StartupHelpDialog() {
        super();
        setTitle("Data Crow - Startup information");
        build();
        setSize(400, 300);
        setModal(true);
        setLocation(Utilities.getCenteredWindowLocation(getSize(), true));
    }
    
    private void build() {
        DcHtmlEditorPane tp = new DcHtmlEditorPane();

        getContentPane().setLayout(Layout.getGBL());
        
        JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(this);
        buttonClose.setActionCommand("close");
        
        JScrollPane scroller = new JScrollPane(tp);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel panelActions = new JPanel();
        panelActions.add(buttonClose);
        buttonClose.setMnemonic('C');
        
        getContentPane().add(scroller,      Layout.getGBC( 0, 0, 4, 1, 10.0, 10.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
        getContentPane().add(panelActions,  Layout.getGBC( 3, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                 new Insets( 5, 5, 5, 0), 0, 0));
        
        String html = 
            "<html><body>" +
            "<h1>Java parameters</h1>" +
            "<p>You can allow Data Crow to use more memory by starting Data Crow in the following way: </p>" +
            "<p>java -Xmx512m -jar datacrow.jar </p>" +
            "<p>In the above example Data Crow is allowed to use 512MB of memory.</p>" +
            "<br><h1>Data Crow parameters</h1>" +
            "<p>Data Crow supports the following parameters:</p>" +
            "<p><table>" +
                "<tr><td>-db:&lt;database name&gt;</td></tr>" +
                "<tr><td>Forces Data Crow to use another database.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-dir:&lt;installation directory&gt;</td></tr>" +
                "<tr><td>Use this parameter when Data Crow starts incorrectly and complains about missing directories (non Windows platform only).</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-webserver</td></tr>" +
                "<tr><td>Starts the web server without starting the Data Crow GUI. Specify -credentials to avoid the login dialog.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-debug</td></tr>" +
                "<tr><td>Debug mode for additional logging information.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-nocache</td></tr>" +
                "<tr><td>Forces Data Crow to ignore the cached items and load everything fresh from the database.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-clearsettings</td></tr>" +
                "<tr><td>Loads the default Data Crow settings. Disgards all user settings.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-debug</td></tr>" +
                "<tr><td>Runs Data Crow in debug mode meaning that more information will be logged.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-datadir:&lt;path&gt;</td></tr>" +
                "<tr><td>Specifies an alternative location for the data folder. Spaces need to be substituted by %20.</td></tr>" +
                "<tr><td><br></td></tr>" +
                "<tr><td>-credentials:username/password</td></tr>" +
                "<tr><td>Specify the login credentials to start Data Crow without displaying the login dialog.</td></tr>" +
                "<tr><td>Example (username and password): java -jar datacrow.jar -credentials:sa/12345</td></tr>" +
                "<tr><td>Example (username without a password): java -jar datacrow.jar -credentials:sa</td></tr>" +
                "<tr><td><br></td></tr>" +
             "</table></p></body></html>";
        tp.setHtml(html);

        pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
    }    
}
