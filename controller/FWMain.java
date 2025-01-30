package controller;

import java.awt.EventQueue;
import view.FWGUI;

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
