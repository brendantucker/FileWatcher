
import java.awt.EventQueue;

/**
 * Main class to start the program.
 */
public class FWMain {

    /* Private constructor to inhibit instantiation. */
    private  FWMain() {
        throw new IllegalStateException();
    }

    /**
     * Main method to start the program.
     * 
     * @param args Command line arguments.
     */
    public static final void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FWGUI();
            }
        });
    }
}
