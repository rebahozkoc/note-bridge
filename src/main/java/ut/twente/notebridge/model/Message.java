package ut.twente.notebridge.model;

import java.sql.Timestamp;

/**
 * This class represents a message in the application.
 * This class is used to store the messages sent by users in the messenger section
 */
public class Message extends BaseEntity {
    /**
     * The id of the message.
     */
    private int messagehistory_id;
    /**
     * The id of the user who has sent the message.
     */
    private Integer user_id;
    /**
     * The date and time the message was created.
     */
    private Timestamp createddate;
    /**
     * The content of the message.
     */
    private String content;
    /**
     * The status of the message.
     */
    private boolean isread;

    /**
     * Returns the content of the Message.
     */
    public String getContent() {
        return content;
    }

    /**
     * setContent method sets the content of the Message.
     *
     * @param content The content of the Message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Default constructor for Message.
     */
    public Message() {
        messagehistory_id = 0;
        user_id = null;
        createddate = null;
        content = null;
        isread = false;
    }

    /**
     * Constructor for Message.
     *
     * @param user_id The id of the user who has sent the message
     * @param content The content of the message
     */
    public Message(Integer user_id, String content) {
        this.user_id = user_id;
        this.content = content;
    }

    public boolean isIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    /**
     * getMessagehistory_id method returns the id of the Message.
     */
    public int getMessagehistory_id() {
        return messagehistory_id;
    }

    /**
     * setMessagehistory_id method sets the id of the Message.
     *
     * @param id The id of the Message
     */
    public void setMessagehistory_id(int id) {
        this.messagehistory_id = id;
    }

    /**
     * getUser_id method returns the id of the user who has sent the message.
     */
    public Integer getUser_id() {
        return user_id;
    }

    /**
     * setUser_id method sets the user_id of the Message.
     *
     * @param user_id The id of the user who has sent the message
     */
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    /**
     * getCreateddate method returns the date and time the message was created.
     */
    public Timestamp getCreateddate() {
        return createddate;
    }

    /**
     * setCreateddate method sets the date and time the message was created.
     *
     * @param createddate The date and time the message was created
     */
    public void setCreateddate(Timestamp createddate) {
        this.createddate = createddate;
    }
}
