import java.awt.*;
import javax.swing.*;

public class FWPanel extends JPanel {
    private JComboBox<String> extensionDropdown;
    private JTextField directoryField;
    private JComboBox<String> queryExtensionDropdown;
    private JTextField myDatabaseField;
    private JButton myWriteDbButton, myQueryButton, myClearButton, myBrowseButton, myStartButton, myStopButton;
    private JButton myImgStartButton, myImgStopButton, myImgDBButton, myImgClearButton;
    private GridBagConstraints myGBC;
    private JPanel myMainPanel;

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

    private void setUpExtensionBox() {
        JLabel monitorLabel = new JLabel("Monitor by extension");
        adjustGridBagConstraints(0, 1, 1);
        myMainPanel.add(monitorLabel, myGBC);

        extensionDropdown = new JComboBox<>(
                new String[] { "Enter an extension", "All extensions", "DOCX", "PDF", "TXT", "PNG", "JPG", "JPEG",
                        "GIF", "MP3", "MP4", "WAV",
                        "AVI", "MOV", "CSV" });
        adjustGridBagConstraints(1, 1, GridBagConstraints.REMAINDER);
        myMainPanel.add(extensionDropdown, myGBC);
    }

    private void setUpDirectoryBox() {
        JLabel directoryLabel = new JLabel("Directory to monitor");
        adjustGridBagConstraints(0, 2, 1);
        myMainPanel.add(directoryLabel, myGBC);

        directoryField = new JTextField(0); // Increase the size of the text field
        adjustGridBagConstraints(1, 2, GridBagConstraints.REMAINDER, 1.0);
        myGBC.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available space
        myMainPanel.add(directoryField, myGBC);
    }

    private void setUpDirectoryButtons() {
        myStartButton = createModernButton("Start");
        myStopButton = createModernButton("Stop");
        myBrowseButton = createModernButton("Browse");
        // Disabling both buttons for until the user has a directory and extension to
        // monitor.
        myStopButton.setEnabled(false);
        myStartButton.setEnabled(false);
        adjustGridBagConstraints(0, 3, 1, 1 / 3.0);
        myGBC.fill = GridBagConstraints.HORIZONTAL;
        myMainPanel.add(myBrowseButton, myGBC);
        adjustGridBagConstraints(1, 3, GridBagConstraints.RELATIVE, 1 / 3.0);
        myMainPanel.add(myStartButton, myGBC);
        adjustGridBagConstraints(3, 3, GridBagConstraints.REMAINDER, 1 / 3.0);
        myMainPanel.add(myStopButton, myGBC);
    }

    private void setUpDatabaseBox() {
        JLabel queryLabel = new JLabel("Query or Write by extension");
        adjustGridBagConstraints(0, 4, 1);
        myMainPanel.add(queryLabel, myGBC);

        queryExtensionDropdown = new JComboBox<>(new String[] { "txt", "log", "csv" });
        adjustGridBagConstraints(1, 4, GridBagConstraints.REMAINDER);
        myMainPanel.add(queryExtensionDropdown, myGBC);

        JLabel databaseLabel = new JLabel("Database");
        adjustGridBagConstraints(0, 5, 1);
        myMainPanel.add(databaseLabel, myGBC);

        myDatabaseField = new JTextField(30); // Increase the size of the text field
        adjustGridBagConstraints(1, 5, GridBagConstraints.REMAINDER, 1.0);
        myGBC.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available space
        myMainPanel.add(myDatabaseField, myGBC);

        myWriteDbButton = createModernButton("Write to database");
        myQueryButton = createModernButton("Query");
        myClearButton = createModernButton("Clear");
        adjustGridBagConstraints(0, 6, 2, .5);
        myGBC.fill = GridBagConstraints.HORIZONTAL; // Reset fill
        myMainPanel.add(myWriteDbButton, myGBC);
        adjustGridBagConstraints(2, 6);
        myMainPanel.add(myQueryButton, myGBC);
        adjustGridBagConstraints(0, 7);
        myGBC.fill = GridBagConstraints.HORIZONTAL;
        myGBC.gridwidth = GridBagConstraints.REMAINDER;
        myMainPanel.add(myClearButton, myGBC);
    }

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

    public JComboBox<String> getExtensionBox() {
        return extensionDropdown;
    }

    public JTextField getDirectoryField() {
        return directoryField;
    }

    public JButton getStartButton() {
        return myStartButton;
    }

    public JButton getStopButton() {
        return myStopButton;
    }

    public JButton getBrowseButton() {
        return myBrowseButton;
    }

    public JButton getClearButton() {
        return myClearButton;
    }

    public JButton getMyWriteDBButton() {
        return myWriteDbButton;
    }

    public JButton getMyImgStarButton() {
        return myImgStartButton;
    }

    public JButton getMyImgStopButton() {
        return myImgStopButton;
    }

    public JButton getMyImgDBButton() {
        return myImgDBButton;
    }

    public JButton getMyImgClearButton() {
        return myImgClearButton;
    }

    public JTextField getMyDatabaseField() {
        return myDatabaseField;
    }

    private void adjustGridBagConstraints(int theX, int theY) {
        myGBC.gridx = theX;
        myGBC.gridy = theY;
    }

    private void adjustGridBagConstraints(int theX, int theY, int theWidth) {
        myGBC.gridx = theX;
        myGBC.gridy = theY;
        myGBC.gridwidth = theWidth;
    }

    private void adjustGridBagConstraints(int theX, int theY, int theWidth, double theWeightx) {
        myGBC.gridx = theX;
        myGBC.gridy = theY;
        myGBC.gridwidth = theWidth;
        myGBC.weightx = theWeightx;
    }
}
