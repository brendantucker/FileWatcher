import java.awt.*;
import javax.swing.*;

public class FWPanel extends JPanel {
    // Combo box for selecting the extension to monitor.
    private JComboBox<String> extensionDropdown;
    // Text field for the directory to monitor.
    private JTextField directoryField;
    // Combo box for the query window to select the queries.
    private JComboBox<String> querySelectionDropdown;
    // Buttons for starting, stopping, and browsing for a directory to monitor.
    private JButton myWriteDbButton, myQueryButton, myDatabaseResetButton, myResetButton, myBrowseButton, myStartButton,
            myStopButton, myExportCSVButton;
    // Buttons for the image icons.
    private JButton myImgStartButton, myImgStopButton, myImgDBButton, myImgClearButton;
    // GridBagConstraint for the layout.
    private GridBagConstraints myGBC;
    // Main panel for the layout.
    private JPanel myMainPanel;

    /**
     * Constructor for the FWPanel. This will create the panel and set up the
     * layout.
     */
    public FWPanel() {
        setLayout(new BorderLayout());

        myMainPanel = new JPanel(new GridBagLayout());
        myGBC = new GridBagConstraints();
        myGBC.insets = new Insets(5, 5, 5, 5);
        myGBC.fill = GridBagConstraints.HORIZONTAL;

        createButtonBar();
        setUpExtensionBox();
        setUpDirectoryBox();
        setUpDirectoryButtons();
        setUpDatabaseBox();

        add(myMainPanel, BorderLayout.CENTER);
    }

    public JPanel FWQueryPanel() {
        JPanel queryPanel = new JPanel(new GridBagLayout());
        GridBagConstraints queryGBC = new GridBagConstraints();
        queryGBC.insets = new Insets(5, 5, 5, 5);

        JLabel queryLabel = new JLabel("Query to Select: ");
        queryPanelGBC(queryGBC, 0, 0, 0.0);
        queryPanel.add(queryLabel, queryGBC);

        querySelectionDropdown = new JComboBox<>(
                new String[] { "Choose query", "Select specific extensions", "Query 1 - All events from today",
                        "Query 2 - Top 5 frequently modified file types", "Query 3 - Most Common Events for Each Extension", });
        queryPanelGBC(queryGBC, 1, 0, 1.0);
        queryPanel.add(querySelectionDropdown, queryGBC);

        myExportCSVButton = createModernButton("Export to CSV");
        queryPanelGBC(queryGBC, 2, 0, 0.0);
        queryPanel.add(myExportCSVButton, queryGBC);

        myDatabaseResetButton = createModernButton("Reset Database");
        queryPanelGBC(queryGBC, 3, 0, 0.0);
        queryPanel.add(myDatabaseResetButton, queryGBC);

        return queryPanel;
    }

    private void queryPanelGBC(GridBagConstraints theGBC, int theX, int theY, double theWeightx) {
        theGBC.fill = GridBagConstraints.HORIZONTAL;
        theGBC.gridx = theX;
        theGBC.gridy = theY;
        theGBC.weightx = theWeightx;
    }

    /**
     * Creates the buttons for the image icons.
     */
    private void createButtonBar() {
        ImageIcon startImageIcon = new ImageIcon("files/startWatching.png");
        ImageIcon stopImageIcon = new ImageIcon("files/stopWatching.png");
        ImageIcon dbImageIcon = new ImageIcon("files/startDB.png");
        ImageIcon clearImageIcon = new ImageIcon("files/clearData.png");

        // Force fixing the scaling as for some reason it was larger than its siblings.
        startImageIcon = new ImageIcon(startImageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        myImgStartButton = new JButton(startImageIcon);
        myImgStartButton.setEnabled(false);
        myImgStopButton = new JButton(stopImageIcon);
        myImgStopButton.setEnabled(false);
        myImgDBButton = new JButton(dbImageIcon);
        myImgClearButton = new JButton(clearImageIcon);
    }

    /**
     * Sets up the extension box for the panel.
     */
    private void setUpExtensionBox() {
        JLabel monitorLabel = new JLabel("Monitor by extension");
        adjustGridBagConstraints(0, 1, GridBagConstraints.RELATIVE, 0.0);
        myMainPanel.add(monitorLabel, myGBC);

        extensionDropdown = new JComboBox<>(
                new String[] { "All extensions", "Enter an extension", "test", "docx", "pdf", "txt", "png", "jpg",
                        "jpeg", "gif", "mp3", "mp4", "wav", "avi", "mov", "csv" });
        adjustGridBagConstraints(1, 1, GridBagConstraints.REMAINDER, 1.0);
        myMainPanel.add(extensionDropdown, myGBC);
    }

    /**
     * Sets up the directory box for the panel.
     */
    private void setUpDirectoryBox() {
        JLabel directoryLabel = new JLabel("Directory to monitor");
        adjustGridBagConstraints(0, 2, GridBagConstraints.RELATIVE, 0.0);
        myMainPanel.add(directoryLabel, myGBC);

        directoryField = new JTextField(0); // Increase the size of the text field
        adjustGridBagConstraints(1, 2, GridBagConstraints.REMAINDER, 1.0);
        myGBC.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available space
        myMainPanel.add(directoryField, myGBC);
    }

    /**
     * Sets up the directory buttons for the panel.
     */
    private void setUpDirectoryButtons() {
        myStartButton = createModernButton("Start");
        myStopButton = createModernButton("Stop");
        myBrowseButton = createModernButton("Browse");
        // Disabling both buttons for until the user has a directory and extension to
        // monitor.

        // Create a panel to hold the browse, start, and stop buttons so they are equal
        // size.
        JPanel buttonSet1 = new JPanel(new GridLayout(1, 3, 5, 0));
        buttonSet1.add(myBrowseButton);
        buttonSet1.add(myStartButton);
        buttonSet1.add(myStopButton);

        myStopButton.setEnabled(false);
        myStartButton.setEnabled(false);

        // Make JPanel take up remainder of row space and fill horizontally
        myGBC.fill = GridBagConstraints.HORIZONTAL;
        adjustGridBagConstraints(0, 3, GridBagConstraints.REMAINDER, 1);
        myMainPanel.add(buttonSet1, myGBC);
    }

    /**
     * Sets up the database box for the panel.
     */
    private void setUpDatabaseBox() {
        myWriteDbButton = createModernButton("Write to database");
        myQueryButton = createModernButton("Query");
        myQueryButton.setEnabled(false);
        myResetButton = createModernButton("Reset");
        adjustGridBagConstraints(0, 6, 1, 1);
        myGBC.fill = GridBagConstraints.HORIZONTAL; // Reset fill

        // Create a panel to hold the write to database and query buttons so they are
        // equal size.
        JPanel buttonSet2 = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonSet2.add(myWriteDbButton);
        buttonSet2.add(myQueryButton);
        myGBC.fill = GridBagConstraints.HORIZONTAL;
        myGBC.gridwidth = GridBagConstraints.REMAINDER; // Make the buttons take up entire row
        myMainPanel.add(buttonSet2, myGBC);

        adjustGridBagConstraints(0, 7);
        myMainPanel.add(myResetButton, myGBC);
    }

    /**
     * Creates a "modern design" button with the given text.
     * 
     * @param text The text being passed in that will appear on the button.
     * @return The button with the given text in the modern design.
     */
    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    /**
     * Gets the extension box.
     * 
     * @return The extension box.
     */
    public JComboBox<String> getExtensionBox() {
        return extensionDropdown;
    }

    /**
     * Gets the query extension box.
     * 
     * @return The query extension box.
     */
    public JComboBox<String> getQueryPopupSelection() {
        return querySelectionDropdown;
    }

    /**
     * Gets the directory box.
     * 
     * @return The directory box.
     */
    public JTextField getDirectoryField() {
        return directoryField;
    }

    /**
     * Gets the start button on the panel.
     * 
     * @return The start button on the panel.
     */
    public JButton getStartButton() {
        return myStartButton;
    }

    /**
     * Gets the stop button.
     * 
     * @return The stop button.
     */
    public JButton getStopButton() {
        return myStopButton;
    }

    /**
     * Gets the directory browse button.
     * 
     * @return The directory browsing button.
     */
    public JButton getBrowseButton() {
        return myBrowseButton;
    }

    /**
     * Gets the reset button
     * 
     * @return The reset button.
     */
    public JButton getResetButton() {
        return myResetButton;
    }

    /**
     * Gets the query button.
     * 
     * @return The query button.
     */
    public JButton getQueryButton() {
        return myQueryButton;
    }

    /**
     * Gets the button that will reset the database.
     * 
     * @return The button that will reset the database.
     */
    public JButton getDatabaseResetButton() {
        return myDatabaseResetButton;
    }

    /**
     * Gets the button that will export the database query list as a CSV.
     * @return The button that will export the database query list as a CSV.
     */
    public JButton getCSVButton(){
        return myExportCSVButton;
    }

    /**
     * Gets the button to start the database.
     * 
     * @return The button to start the database.
     */
    public JButton getMyWriteDBButton() {
        return myWriteDbButton;
    }

    /**
     * Gets the start button with an image
     * 
     * @return The start button with an image.
     */
    public JButton getMyImgStarButton() {
        return myImgStartButton;
    }

    /**
     * Gets the stop button with an image.
     * 
     * @return The stop button with an image.
     */
    public JButton getMyImgStopButton() {
        return myImgStopButton;
    }

    /**
     * Gets the button to query the database with an image.
     * 
     * @return The button to query the database with an image.
     */
    public JButton getMyImgDBButton() {
        return myImgDBButton;
    }

    /**
     * Gets the clear button with an image.
     * 
     * @return The clear button with an image.
     */
    public JButton getMyImgClearButton() {
        return myImgClearButton;
    }

    /**
     * Helper constructor to clean up code above, adjusting gridbag with X and Y
     * values.
     * 
     * @param theX The X value to be adjusted for the gridbag.
     * @param theY The Y value to be adjusted for the gridbag.
     */
    private void adjustGridBagConstraints(int theX, int theY) {
        myGBC.gridx = theX;
        myGBC.gridy = theY;
    }

    /**
     * Helper constructor to clean up code above, adjusting gridbag with X, Y,
     * width, and weightx values.
     * 
     * @param theX       The X value to be adjusted for the gridbag.
     * @param theY       The Y value to be adjusted for the gridbag.
     * @param theWidth   The width value to be adjusted for the gridbag. MUST USE
     *                   GridBagConstraints ENUM.
     * @param theWeightx The weightx value to be adjusted for the gridbag.
     */
    private void adjustGridBagConstraints(int theX, int theY, int theWidth, double theWeightx) {
        myGBC.gridx = theX;
        myGBC.gridy = theY;
        myGBC.gridwidth = theWidth;
        myGBC.weightx = theWeightx;
    }
}
