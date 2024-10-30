package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to convert a TSV table to a swagger object
 */
public class TSVTable {

    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;

    public TSVTable(String path) throws IOException {
        this.tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Return Object class to allow sorting of all column types
                return Object.class;
            }
        };
        this.table = new JTable(tableModel);
        this.scrollPane = new JScrollPane(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        this.parseTSVFile(path);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
    }

    private void parseTSVFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        if ((line = reader.readLine()) != null) {
            String[] headers = line.split("\t");
            String[] filteredHeaders = new String[]{headers[0], headers[1], headers[headers.length - 1]};
            for (String header : filteredHeaders) {
                tableModel.addColumn(header);
            }
        }

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("TIME")) {
                String[] rowData = line.split("\t");
                String[] filteredData = new String[]{rowData[0], rowData[1], rowData[rowData.length - 1]};
                tableModel.addRow(filteredData);
            }
        }
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
