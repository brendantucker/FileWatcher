package model;



/*
 * This class represents an event that occurs to a file and all necessary associated data. This will be used to track when
 *  and where an event occured to a file, as well as what type of event it was.
 */
public class FileEvent {
    /** A string representing the path to a file */
    private String myFilePath;
    /** A string representing the name of a file */
    private String myFileName;
    /** An enum representing the type of event that occured to the file */
    private String myEventType;
    /** A String representing the time that the event occured */
    private String myEventTime;
    /** A string representing the extension of the file */
    private String myExtension;

    /**
     * Constructor for a FileEvent. 
     * 
     * @param fileName The name of the file that the event occured to. 
     * @param filePath The path of the file that the event occured to.
     * @param eventType The type of event that occured to the file.
     * @param extension The extension of the file that the event occured to.
     * @param eventTime The time that the event occured.
     */
    public FileEvent(String theFileName, String theFilePath, String theEventType, String theExtension, String theEventTime) {
        myFileName = theFileName;
        myFilePath = theFilePath;
        myEventType = theEventType;
        myEventTime = theEventTime;
        myExtension = theExtension;
    }

    /**
     * Returns a string representation of event's file name.
     * 
     * @return The string representation of the file name.
     */
    public String getFileName() {
        return myFileName;
    }

    /**
     * Gets the string representation of the file path where the event occured.
     * 
     * @return The string representation of the file path.
     */
    public String getFilePath() {
        return myFilePath;
    }

    /**
     * Returns the type of event that occured to the file.
     * 
     * @return The EventType from EventType enum.
     */
    public String getEventType() {
        return myEventType;
    }

    /**
     * Returns the time that the event occured.
     * 
     * @return The LocalDateTime of the event.
     */
    public String getEventTime() {
        return myEventTime;
    }
    
    /**
     * Returns the extension of the file that the event occured to.
     * 
     * @return The extension of the affected file.
     */
    public String getExtension() {
        return myExtension;
    }

}
