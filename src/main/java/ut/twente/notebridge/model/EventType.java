package ut.twente.notebridge.model;

/**
 * This class represents an event type in the application.
 */
public class EventType {
    /**
     * The name of the event type.
     */
    private String name;

    /**
     * Default constructor for EventType.
     */
    public EventType() {
        super();
        this.name = null;
    }

    /**
     * getName method returns the name of the EventType.
     */
    public String getName() {
        return name;
    }

    /**
     * setName method sets the name of the EventType.
     *
     * @param name The name of the EventType
     */
    public void setName(String name) {
        this.name = name;
    }
}
