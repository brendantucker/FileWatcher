package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FileWatcherGUI implements ActionListener{

    private JFrame myFrame;

    public FileWatcherGUI()
    {
        super();
        myFrame = new FileWatcherFrame().frameOutline();
        myFrame.setVisible(true);
    }

    public void actionPerformed(final ActionEvent theEvent)
    {
        // TODO Auto-generated method stub
    }
}
