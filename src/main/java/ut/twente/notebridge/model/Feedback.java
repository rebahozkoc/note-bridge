package ut.twente.notebridge.model;

/**
 * This class represents a feedback in the application.
 * Since email, message are common fields in all feedbacks, they are defined here
 * It is used in "Contact Us" section of the application
 */
public class Feedback extends BaseEntity {
    /**
     * The user's email.
     */
    private String email;
    /**
     * The user's message.
     */
    private String message;

    /**
     * Default constructor for Feedback.
     */
    public Feedback() {
        super();
        this.email = null;
        this.message = null;
    }

    /**
     * getEmail method returns the email of the sender.
     */
    public String getEmail() {
        return email;
    }

    /**
     * setEmail method sets the email of the sender.
     *
     * @param email The email of the sender
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getMessage method returns the message of the sender.
     */
    public String getMessage() {
        return message;
    }

    /**
     * setMessage method sets the message of the sender.
     *
     * @param message The message of the sender
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
