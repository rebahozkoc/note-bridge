package ut.twente.notebridge.model;

/**
 * This class represents a person and their interested instruments in the application.
 * This class is used to store the information of a person instrument relationship
 */
public class PersonInstrument {
    /**
     * The id of the person who has an instrument experience.
     */
    private int personId;
    /**
     * The name of the instrument.
     */
    private String instrumentName;
    /**
     * The years of experience of the person with the instrument.
     */
    private double yearsOfExperience;

    /**
     * Default constructor for PersonInstrument.
     */
    public PersonInstrument() {
    }

    /**
     * getPersonId method returns the personId of Person.
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * setPersonId method sets the personId of Person.
     *
     * @param personId The personId of Person
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    /**
     * getInstrumentName method returns the instrument name.
     */
    public String getInstrumentName() {
        return instrumentName;
    }

    /**
     * setInstrumentName method sets the instrument name.
     *
     * @param instrumentName The instrument name
     */
    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    /**
     * getYearsOfExperience method returns the years of experience.
     */
    public double getYearsOfExperience() {
        return yearsOfExperience;
    }

    /**
     * setYearsOfExperience method sets the years of experience.
     *
     * @param yearsOfExperience The years of experience
     */
    public void setYearsOfExperience(double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
