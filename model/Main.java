package model;

import java.awt.EventQueue;

import view.FileWatcherGUI;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() 
        {
           @Override
           public void run()
           {
            new FileWatcherGUI();
           } 
        });
    }
}