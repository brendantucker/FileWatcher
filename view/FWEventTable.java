package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.FileEvent;

import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 * This class represents a table that will display the events that have occurred to files. 
 * 
 *  To add: ability to sort table ascending/descending by column
 */
public class FWEventTable extends JPanel{
    /** JTable to display the events that have occurred. */
    private JTable myEventTable;
    /** DefaultTableModel to hold and act as manager for JTable. */
    private DefaultTableModel myTableModel;
    /** Array of column names for the JTable. */
    private String[] myColumnNames;
    /** ArrayList to hold the data for the JTable. */
    private ArrayList<FileEvent> myData;

    /**
     * Constructor for the FWEventTable. This will create the table and set up the panel.
     */
    public FWEventTable() {
        super(new BorderLayout()); // Ensure that the panel is using a BorderLayout.
        myColumnNames = new String[] { "File Name", "File Path", "Event Type", "File Extension", "Time", };
        myTableModel = new DefaultTableModel();
        myTableModel.setColumnIdentifiers(myColumnNames);
        myData = new ArrayList<FileEvent>();
        
        myEventTable = new JTable(myTableModel);
        
        //Allow column reordering
        myEventTable.getTableHeader().setReorderingAllowed(true);

        //Adds the JTable to a scroll pane, then adds the scroll pane to the FWEventTable panel.
        JScrollPane scrollPane = new JScrollPane(myEventTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, BorderLayout.SOUTH);
    }


    /**
     * Adds a FileEvent to the table.
     * 
     * @param theEvent The FileEvent to add to the table.
     */
    public void addEvent(FileEvent theEvent) {
        myData.add(theEvent);
        myTableModel.addRow(new Object[] { theEvent.getFileName(), theEvent.getFilePath(), theEvent.getEventType(), theEvent.getExtension(), theEvent.getEventTime() });
    }

    /**
     * Returns the data in the table.
     * 
     * @return The data in the table.
     */
    public ArrayList<FileEvent> getData() {
        return myData;
    }

    /**
     * Updates the table with the current data.
     */
    public void updateTable() {
        myTableModel.setRowCount(0);
        for (FileEvent event : myData) {
            myTableModel.addRow(new Object[] { event.getFileName(), event.getFilePath(), event.getEventType(), event.getExtension(), event.getEventTime() });
        }
    }

    /**
     * Clears the table of all data and empties the myData array of FileEvents.
     */
    public void clearTable() {
        myData.clear();
        myTableModel.setRowCount(0);
    }

}
