package ut.twente.notebridge.model;

/**
 * This class represents an instrument in the application.
 */
public class Instrument {
    /**
     * The name of the instrument.
     */
    private String name;

    /**
     * Default constructor for Instrument.
     */
    public Instrument() {
        super();
        this.name = null;
    }

    /**
     * getName method returns the name of the Instrument.
     */
    public String getName() {
        return name;
    }

    /**
     * setName method sets the name of the Instrument.
     *
     * @param name The name of the Instrument
     */
    public void setName(String name) {
        this.name = name;
    }
}
