package model;

import java.time.LocalDateTime;


/*
 * This class represents an event that occurs to a file. This will be used to track when and where an event occured
 * to a file, as well as what type of event it was.
 */
public class FileEvent {
    private String myFilePath;
    private String myFileName;
    private EventType myEventType;
    private LocalDateTime myEventTime;
    private String myExtension;

    /*
     * Constructor for the FileEvent class. 
     * @param fileName The name of the file that the event occured to. 
     * @param eventType The type of event that occured to the file.
     * @param eventTime The time that the event occured.
     */
    public FileEvent(String theFileName, String theFilePath, EventType theEventType, String theExtension, LocalDateTime theEventTime) {
        myFileName = theFileName;
        myFilePath = theFilePath;
        myEventType = theEventType;
        myEventTime = theEventTime;
        myExtension = theExtension;
    }

    /*
     * Returns a string representation of event's file name.
     */
    public String getFileName() {
        return myFileName;
    }

    /*
     * Returns a string representation of the file path where the event occured.
     */
    public String getFilePath() {
        return myFilePath;
    }

    /*
     * Returns the type of event that occured to the file. 
     *  This will be one of the values in the EventType enum.
     */
    public EventType getEventType() {
        return myEventType;
    }

    /*
     * Returns the time that the event occured.
     */
    public LocalDateTime getEventTime() {
        return myEventTime;
    }
    
    /*
     * Returns the extension of the file that the event occured to.
     */
    public String getExtension() {
        return myExtension;
    }

}
