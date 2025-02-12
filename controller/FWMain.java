
import java.awt.EventQueue;

public class FWMain {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FWGUI();
            }
        });
    }
}
