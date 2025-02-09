package view;

import java.awt.*;
import javax.swing.*;

public class FWPanel extends JPanel {
    private JComboBox<String> extensionDropdown;
    private JTextField directoryField;
    private JButton startButton, stopButton;
    private JComboBox<String> queryExtensionDropdown;
    private JTextField databaseField;
    private JButton writeDbButton, queryButton, clearButton, browseButton;
    
    public FWPanel() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel monitorLabel = new JLabel("Monitor by extension");
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(monitorLabel, gbc);

        extensionDropdown = new JComboBox<>(new String[]{"DOCX", "PDF", "TXT", "PNG", "JPG", "JPEG", "GIF", "MP3", "MP4", "WAV", "log", "csv"});
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(extensionDropdown, gbc);

        JLabel directoryLabel = new JLabel("Directory to monitor");
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(directoryLabel, gbc);

        directoryField = new JTextField(0); // Increase the size of the text field
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0; // Allow the text field to expand
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available space
        mainPanel.add(directoryField, gbc);

        startButton = createModernButton("Start");
        stopButton = createModernButton("Stop");
        browseButton = createModernButton("Browse");

        stopButton.setEnabled(false);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 1/3; // Reset weightx
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        mainPanel.add(startButton, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.weightx = 1/3; // Reset weightx
        mainPanel.add(stopButton, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1/3; // Reset weightx
        mainPanel.add(browseButton, gbc);

        JLabel queryLabel = new JLabel("Query or Write by extension");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        mainPanel.add(queryLabel, gbc);

        queryExtensionDropdown = new JComboBox<>(new String[]{"txt", "log", "csv"});
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(queryExtensionDropdown, gbc);

        JLabel databaseLabel = new JLabel("Database");
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        mainPanel.add(databaseLabel, gbc);

        databaseField = new JTextField(30); // Increase the size of the text field
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0; // Allow the text field to expand
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the text field fill the available space
        mainPanel.add(databaseField, gbc);

        writeDbButton = createModernButton("Write to database");
        queryButton = createModernButton("Query");
        clearButton = createModernButton("Clear");
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = .5; 
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill
        mainPanel.add(writeDbButton, gbc);
        gbc.gridx = 2; gbc.gridy = 5;
        mainPanel.add(queryButton, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(clearButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
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
}
