package controller;

import java.awt.EventQueue;

import view.FWGUI;

public class FWMain {

    /* Private constructor to inhibit instantiation. */
    private FWMain() {
        throw new IllegalStateException();
    }

    /**
     * Main method to start the program.
     * 
     * @param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FWGUI();
            }
        });
    }
}
