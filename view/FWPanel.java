package view;

import java.awt.*;
import javax.swing.*;

public class FWPanel extends JPanel {
    private JComboBox<String> extensionDropdown;
    private JTextField directoryField;
    private JButton startButton, stopButton;
    private JComboBox<String> queryExtensionDropdown;
    private JTextField databaseField;
    private JButton writeDbButton, queryButton, clearButton;
    
    public FWPanel() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel monitorLabel = new JLabel("Monitor by extension");
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(monitorLabel, gbc);

        extensionDropdown = new JComboBox<>(new String[]{"txt", "log", "csv"});
        gbc.gridx = 1;
        mainPanel.add(extensionDropdown, gbc);

        JLabel directoryLabel = new JLabel("Directory to monitor");
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(directoryLabel, gbc);

        directoryField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(directoryField, gbc);

        startButton = createModernButton("Start");
        stopButton = createModernButton("Stop");
        stopButton.setEnabled(false);
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(startButton, gbc);
        gbc.gridx = 1;
        mainPanel.add(stopButton, gbc);

        JLabel queryLabel = new JLabel("Query or Write by extension");
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(queryLabel, gbc);

        queryExtensionDropdown = new JComboBox<>(new String[]{"txt", "log", "csv"});
        gbc.gridx = 1;
        mainPanel.add(queryExtensionDropdown, gbc);

        JLabel databaseLabel = new JLabel("Database");
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(databaseLabel, gbc);

        databaseField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(databaseField, gbc);

        writeDbButton = createModernButton("Write to database");
        queryButton = createModernButton("Query");
        clearButton = createModernButton("Clear");
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(writeDbButton, gbc);
        gbc.gridx = 1;
        mainPanel.add(queryButton, gbc);
        gbc.gridx = 2;
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
