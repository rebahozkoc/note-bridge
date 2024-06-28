package ut.twente.notebridge.dto;

import java.sql.Timestamp;

/**
 * This class is used at retrieving all comments of a given posts with tailored information about the sender of the comments.
 */
public class CommentDto {

    /**
     * The id of the comment.
     */
    private int id;
    /**
     * The content of the comment.
     */
    private String content;
    /**
     * The id of the person who has created the comment.
     */
    private int personId;
    /**
     * The picture of the person who has created the comment.
     */
    private String picture;
    /**
     * The username of the person who has created the comment.
     */
    private String username;
    /**
     * The date and time the comment was created.
     */
    private Timestamp createDate;

    /**
     * Default constructor for CommentDto.
     */
    public CommentDto() {
    }

    /**
     * getContent method returns the content of the comment.
     */
    public String getContent() {
        return content;
    }

    /**
     * setContent method sets the content of the comment.
     *
     * @param content The content of the comment
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * getPersonId method returns the id of the sender of the comment.
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * setPersonId method sets the sender of the comment.
     *
     * @param personId The id of the sender of the comment
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    /**
     * getPicture method returns the picture of the sender of the comment.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * setPicture method sets the picture of the sender of the comment.
     *
     * @param picture The picture URL of the sender of the comment
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * getUsername method returns the username of the sender of the comment.
     */
    public String getUsername() {
        return username;
    }

    /**
     * setUsername method sets the username of the sender of the comment.
     *
     * @param username The username of the sender of the comment
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getCreateDate method returns the date and time the comment was created.
     */
    public Timestamp getCreateDate() {
        return createDate;
    }

    /**
     * setCreateDate method sets the date and time the comment was created.
     *
     * @param createDate The date and time the comment was created
     */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    /**
     * getId method returns the id of the comment.
     */
    public int getId() {
        return id;
    }

    /**
     * setId method sets the id of the comment.
     *
     * @param id The id of the comment
     */
    public void setId(int id) {
        this.id = id;
    }
}
