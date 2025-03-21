import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

/**
 * This class represents a table that will display the events that have occurred
 * to files.
 */
public final class FWEventTable extends JPanel {
    /** JTable to display the events that have occurred. */
    private final JTable myEventTable;
    /** DefaultTableModel to hold and act as manager for JTable. */
    private final DefaultTableModel myTableModel;
    /** ArrayList to hold the data for the JTable. */
    private final ArrayList<FileEvent> myData;
    /** The sorter for handling file event sorting */
    private final TableRowSorter<DefaultTableModel> mySorter;
    /** Array of default column widths for the JTable. */
    private static final int[] MY_DEFAULT_COLUMN_WIDTHS = { 110, 250, 60, 50, 90, 90 }; // Default column widths for the table

    /**
     * Constructor for the FWEventTable. This will create the table and set up the
     * panel.
     */
    public FWEventTable() {
        super(new BorderLayout()); // Ensure that the panel is using a BorderLayout.
        final String[] myColumnNames = { "File Name", "File Path", "Event Type", "Extension", "Date", "Time" };

        myTableModel = new DefaultTableModel(myColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create the data arraylist to hold the data for the table.
        myData = new ArrayList<>();

        myEventTable = new JTable(myTableModel);
        myEventTable.getTableHeader().setReorderingAllowed(true); // Allow column reordering

        // Create a sorter for the table
        mySorter = new TableRowSorter<>(myTableModel);
        myEventTable.setRowSorter(mySorter);

        // Create a scroll pane for the table
        final JScrollPane scrollPane = new JScrollPane(myEventTable);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, Integer.MAX_VALUE)); // Set vertical scrollbar width

        // Add preferred (default) widths to columns
        for (int i = 0; i < myColumnNames.length; i++) {
            myEventTable.getColumnModel().getColumn(i).setPreferredWidth(MY_DEFAULT_COLUMN_WIDTHS[i]);
        }

        myEventTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Adds a FileEvent to the table.
     * 
     * @param theEvent The FileEvent to add to the table.
     */
    public final void addEvent(final FileEvent theEvent) {
        myData.add(theEvent);

        myTableModel.addRow(new Object[] {
                theEvent.getFileName(),
                theEvent.getFilePath(),
                theEvent.getEventType(),
                theEvent.getExtension(),
                theEvent.getEventDate(),
                theEvent.getEventTime()
        });
    }

    /**
     * Returns the data in the table.
     * 
     * @return The data in the table.
     */
    public final ArrayList<FileEvent> getData() {
        return myData;
    }

    /**
     * Updates the table with information stored in the data arraylist.
     */
    public final void updateTable() {
        for (FileEvent event : myData) {
            myTableModel.addRow(new Object[] {
                    event.getFileName(),
                    event.getFilePath(),
                    event.getEventType(),
                    event.getExtension(),
                    event.getEventDate(),
                    event.getEventTime()
            });
        }
    }

    /**
     * Clears the table of all data and empties the myData array of FileEvents.
     */
    public final void clearTable() {
        myData.clear();
        myTableModel.setRowCount(0);
    }

    /**
     * Filters the table by the extension of the file.
     * @param theFilter The extension to filter by.
     */
    public final void filterByExtension(String theFilter) {
        myTableModel.setRowCount(0);
        if (!theFilter.equals("All Extensions")) {
            for (FileEvent event : myData) {
                if (event.getExtension().contains(theFilter)) {
                    myTableModel.addRow(new Object[] { event.getFileName(), event.getFilePath(), event.getEventType(),
                            event.getExtension(), event.getEventDate(), event.getEventTime() });
                }
            }
        }
    }
}
