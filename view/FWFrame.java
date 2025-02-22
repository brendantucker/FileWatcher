
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class FWFrame extends JFrame {

    /** Auto generated serial version UUID. */
    private static final long serialVersionUID = 5497889472015611168L;
    /** A ToolKit. */
    private static final Toolkit KIT = Toolkit.getDefaultToolkit();
    /** The Dimension of the screen. */
    private static final Dimension SCREEN_SIZE = KIT.getScreenSize();
    /** The width of the screen. */
    private static final int SCREEN_WIDTH = SCREEN_SIZE.width;
    /** The height of the screen. */
    private static final int SCREEN_HEIGHT = SCREEN_SIZE.height;
    /**
     * A Factor for scaling the size of the GUI relative to the current screen size.
     */
    private static final double VERTICAL_SCALE = 0.7;
    private static final double HORIZONTAL_SCALE = 0.3;

    public JFrame frameOutline() {
        // Setting up the frame and adjusting it to be 70% of the screens dimensions.
        final JFrame fileFrame = new JFrame("TCSS 360 - File Watcher");
        fileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // fileFrame.setSize((int) (SCREEN_WIDTH * SCALE), (int) (SCREEN_HEIGHT *
        // SCALE));
        fileFrame.setSize((int) (SCREEN_WIDTH * HORIZONTAL_SCALE), (int) (SCREEN_HEIGHT * VERTICAL_SCALE));

        // Centering the frame on program start.
        fileFrame.setLocationRelativeTo(null);

        return fileFrame;
    }

}
