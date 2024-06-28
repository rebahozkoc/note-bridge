package ut.twente.notebridge.model;

/**
 * This class represents a person in the application.
 * This class is used to store the information of a person
 */
public class Person extends BaseUser {
    /**
     * The name of the person.
     */
    private String name;
    /**
     * The last name of the person.
     */
    private String lastname;

    /**
     * Default constructor for Person.
     */
    public Person() {
        super();
        this.name = null;
        this.lastname = null;
    }

    /**
     * getName method returns the name of the Person.
     */
    public String getName() {
        return name;
    }

    /**
     * setName method sets the name of the Person.
     *
     * @param name The name of the Person
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getLastname method returns the last name of the Person.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * setLastname method sets the last name of the Person.
     *
     * @param lastname The last name of the Person
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
