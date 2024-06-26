package ut.twente.notebridge.model;

/**
 * This class represents a comment in the application.
 */
public class Comment extends BaseEntity {
    /**
     * The content of the comment.
     */
    private String content;
    /**
     * The user's unique ID.
     * Since only Person can comment, this is the person's unique ID.
     */
    private int personId;
    /**
     * The post's unique ID.
     */
    private int postId;

    /**
     * Default constructor for Comment.
     */
    public Comment() {
        super();
        this.content = null;
        this.personId = 0;
        this.postId = 0;
    }

    /**
     * getContent method returns the content of the Comment.
     */
    public String getContent() {
        return content;
    }

    /**
     * setContent method sets the content of the Comment.
     *
     * @param content The content of the Comment
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * getPersonId method returns the id of the person making the comment.
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * setPersonId method sets the personId of the Comment.
     *
     * @param personId the id of the person making the comment
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    /**
     * getPostId method returns the postId of the Comment.
     */
    public int getPostId() {
        return postId;
    }

    /**
     * setPostId method sets the postId of the Comment.
     *
     * @param postId The postId of the Comment
     */
    public void setPostId(int postId) {
        this.postId = postId;
    }
}
