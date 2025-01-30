package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class FileWatcherGUI implements ActionListener {

    private JFrame myFrame;

    public FileWatcherGUI() {
        super();
        myFrame = new FileWatcherFrame().frameOutline();
        myFrame.setVisible(true);
    }

    public void actionPerformed(final ActionEvent theEvent) {
        // TODO Auto-generated method stub
    }
}
