package ut.twente.notebridge.model;

/**
 * This class represents a message history in the application.
 * This class is used to store the contacts of users in messages
 */
public class MessageHistory extends BaseEntity {
    /**
     * The id of the first user in the contact.
     */
    private String user1;
    /**
     * The id of the second user in the contact.
     */
    private String user2;

    /**
     * Returns the id of the first user in the contact.
     */
    public String getUser1() {
        return user1;
    }

    /**
     * Sets the id of the first user in the contact.
     *
     * @param user1 The id of the first user in the contact
     */
    public void setUser1(String user1) {
        this.user1 = user1;
    }

    /**
     * Returns the id of the second user in the contact.
     */
    public String getUser2() {
        return user2;
    }

    /**
     * Sets the id of the second user in the contact.
     *
     * @param user2 The id of the second user in the contact
     */
    public void setUser2(String user2) {
        this.user2 = user2;
    }

    /**
     * Default constructor for MessageHistory.
     */
    public MessageHistory() {
        this.user1 = null;
        this.user2 = null;
    }
}
