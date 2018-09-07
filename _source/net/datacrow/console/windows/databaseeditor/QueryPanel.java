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

package net.datacrow.console.windows.databaseeditor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.table.TableColumn;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.GUI;
import net.datacrow.console.Layout;
import net.datacrow.console.components.tables.DcTable;
import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.data.DcResultSet;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class QueryPanel extends JPanel implements ActionListener, ItemListener {

    private static Logger logger = Logger.getLogger(QueryPanel.class.getName());    
    
    private JEditorPane textInput;
	private JButton buttonRunSql;
	private JButton buttonClear;

	private JComboBox comboSQLCommands;
	
	private DcTable table;

    public QueryPanel() {
        table = ComponentFactory.getDCTable(false, false);
        build();
    }

    private void fillTable(DcResultSet rs) throws Exception {
        clearTable();

        int columns = rs.getColumnCount();
        
        table.setColumnCount(columns);
        int counter = 0;
        TableColumn column;
        for (Enumeration<TableColumn> enumerator = table.getColumnModel().getColumns(); enumerator.hasMoreElements(); counter++) {
            column = enumerator.nextElement();
            column.setHeaderValue(rs.getColumnName(counter).toLowerCase());
        }

        String[] values;
        for (int row = 0; row < rs.getRowCount(); row++) {
            values = new String[columns];
            for (int col = 0; col < columns; col++) {
                values[col] = rs.getString(row, col);
            }
            table.addRow(values);
        }

        // apply the correct headers (colors and such)
        table.applyHeaders();
    }

    protected void runQuery() {
        String sql   = textInput.getText().trim();

        if (sql.equals("")){
            GUI.getInstance().displayWarningMessage("msgNoInput");
            return;
        }

        Connector conn = DcConfig.getInstance().getConnector();
        try {
            DcResultSet result = conn.executeSQL(sql);

            boolean empty = result.getRowCount() == 0;
            if (empty) {
                GUI.getInstance().displayMessage("msgQueryWasSuccessFull");
            } else {
                // also closes the result set
                fillTable(result);
            }

            addQueryToComboBox(sql);

            saveDataToFile();
        } catch (Exception e) {
            logger.error("An error occurred while executing the query", e);
            GUI.getInstance().displayErrorMessage(e.toString());
        }
    }

    private void addQueryToComboBox(String qry) {
        String query = qry;
        query = query.replaceAll("\r", " ");
        query = query.replaceAll("\n", " ");

        boolean found = false;
        for (int i = 0; i < comboSQLCommands.getItemCount(); i++) {
            Object o = comboSQLCommands.getItemAt(i);
            if (o != null && o instanceof QueryObject) {
            	String s = ((QueryObject) o).getQryString();
            	if (s.toLowerCase().equals(query.toLowerCase())) {
            		found = true;
            		break;
                }
            }
        }
        if (!found) {
            QueryObject o = new QueryObject(query);
            comboSQLCommands.addItem(o);
        }
    }

    private void fillQueryComboBox() {
        File queryFile = new File(DcConfig.getInstance().getApplicationSettingsDir(), "data_crow_queries.txt");

        try {
            if (queryFile.exists()) {
                RandomAccessFile fileAccess  = new RandomAccessFile(queryFile, "rw");
                long filePointer = 0;
                long fileLength  = queryFile.length();
                comboSQLCommands.addItem("");
                while (filePointer < fileLength) {
                    String query = fileAccess.readLine();
                    if (query != null) {
                        addQueryToComboBox(query.trim());
                        filePointer = fileAccess.getFilePointer();
                    }
                }
                fileAccess.close();
            } else if (!queryFile.exists()) {
                logger.info(DcResources.getText("msgFileNotFound", queryFile.toString()));
                RandomAccessFile fileAccess  = new RandomAccessFile(queryFile, "rw");
                fileAccess.write(DcRepository.Database._PREDEFINEDQRY.getBytes());
                fileAccess.close();
                fillQueryComboBox();
            }
        } catch (Exception e) {
            comboSQLCommands.addItem("");
            logger.error("Could not read " + queryFile, e);
        }
        saveDataToFile();
    }

    protected void saveDataToFile() {
        File queryFile = new File(DcConfig.getInstance().getApplicationSettingsDir(), "data_crow_queries.txt");
        if (queryFile.exists()) {
            queryFile.delete();
            queryFile = new File(DcConfig.getInstance().getApplicationSettingsDir(), "data_crow_queries.txt");
        }

        try {
            queryFile.createNewFile();
            RandomAccessFile access  = new RandomAccessFile(queryFile, "rw");

            Object o;
            String query;
            for (int i = 0; i < comboSQLCommands.getItemCount(); i++) {
                o = comboSQLCommands.getItemAt(i);
                if (o != null && o instanceof QueryObject) {
                    query  = ((QueryObject) o).getQryString();
                    query = query.replaceAll("\r", " ");
                    query = query.replaceAll("\n", " ");
                    access.writeBytes(query + '\n');
                }
            }
            
            access.close();
            
        } catch (Exception e) {
            logger.error(DcResources.getText("msgFileSaveError", queryFile.toString()), e);
        }
        
        logger.info(DcResources.getText("msgFileSaved", queryFile.toString()));
    }

    protected void setQuery() {
        Object o = comboSQLCommands.getSelectedItem();
        if (o != null) {
            if (o instanceof QueryObject) {
            	QueryObject qry = (QueryObject) o ;
            	textInput.setText(qry.getQryString());
            }
        }
    }

    protected void clearPanel() {
        textInput.setText("");
        clearTable();
    }

    public void clearTable() {
        table.clear();
    }

    private void build() {
        //**********************************************************
        //SQL panel
        //**********************************************************
        JPanel panelSQL = new JPanel();
        panelSQL.setLayout(Layout.getGBL());

        textInput = ComponentFactory.getTextPane();

        JScrollPane scrollIn = new JScrollPane(textInput);
        scrollIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelSQL.add(scrollIn, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                     new Insets(0, 5, 5, 5), 0, 0));

        //**********************************************************
        //SQL panel
        //**********************************************************
        JPanel panelQueries = new JPanel();
        panelQueries.setLayout(Layout.getGBL());

        comboSQLCommands = ComponentFactory.getComboBox();
        comboSQLCommands.addItemListener(this);
        fillQueryComboBox();

        panelQueries.add(comboSQLCommands, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                         new Insets(5, 5, 5, 5), 0, 0));

        //**********************************************************
        //Result panel
        //**********************************************************
        JPanel panelResult = new JPanel();
        panelResult.setLayout(Layout.getGBL());
        table = ComponentFactory.getDCTable(true, false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollOut = new JScrollPane(table);
        scrollOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelResult.add(scrollOut, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                       ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));

        //**********************************************************
        //Action panel
        //**********************************************************
        JPanel panelActions = new JPanel();
        panelActions.setLayout(Layout.getGBL());

        buttonRunSql = ComponentFactory.getButton(DcResources.getText("lblRun"));
        buttonClear = ComponentFactory.getButton(DcResources.getText("lblClear"));

        buttonRunSql.addActionListener(this);
        buttonClear.addActionListener(this);
        
        buttonRunSql.setActionCommand("runQuery");
        buttonClear.setActionCommand("clear");
        
        buttonRunSql.setToolTipText(DcResources.getText("tpRunSQL"));
        buttonClear.setToolTipText(DcResources.getText("tpClear"));

        panelActions.add(buttonRunSql, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                         new Insets(0, 5, 0, 5), 0, 0));
        panelActions.add(buttonClear, Layout.getGBC( 1, 0, 1, 1, 1.0, 1.0
                        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                         new Insets(0, 0, 0, 5), 0, 0));

        //**********************************************************
        //Main panel
        //**********************************************************
        setLayout(Layout.getGBL());

        panelSQL.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblQueryIn")));
        panelResult.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblQueryOut")));
        panelQueries.setBorder(ComponentFactory.getTitleBorder(DcResources.getText("lblStoredSQL")));

        add(  panelSQL,     Layout.getGBC( 0, 0, 10, 1, 10.0, 10.0
                           ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                            new Insets(5, 5, 5, 5), 0, 0));
        add(  panelActions, Layout.getGBC( 0, 1, 1, 1,  1.0, 1.0
                           ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                            new Insets(5, 5, 5, 5), 0, 0));
        add(  panelQueries, Layout.getGBC( 0, 2, 10, 1, 1.0, 1.0
                           ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                            new Insets(5, 5, 5, 5), 0, 0));
        add(  panelResult,  Layout.getGBC( 0, 3, 10, 1, 20.0, 20.0
                           ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                            new Insets(5, 5, 5, 5), 0, 0));

        ToolTipManager.sharedInstance().registerComponent(table);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("runQuery"))
            runQuery();
        else if (e.getActionCommand().equals("clear"))
            clearPanel();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        setQuery();
    }

    private static class QueryObject {

        private final String query;

        public QueryObject(String query) {
            this.query = query;
        }

        public String getQryString() {
            return query;
        }

        @Override
        public String toString() {
            return query.length() > 50 ? query.substring(0, 50) + "..." : query;
        }
    } 
}