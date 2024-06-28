package ut.twente.notebridge.model;

/**
 * This class represents a like in the application.
 * Users can like a post, this class is used to store the likes of users in posts
 */
public class Like {
    /**
     * The id of the post on which a like is shown.
     */
    private int postId;
    /**
     * The id of the person who has liked the post.
     */
    private int personId;

    /**
     * Default constructor for Like.
     */
    public Like() {
    }

    /**
     * getPostId method returns the postId of the Like.
     */
    public int getPostId() {
        return postId;
    }

    /**
     * setPostId method sets the postId of the Like.
     *
     * @param postId The postId of the Like
     */
    public void setPostId(int postId) {
        this.postId = postId;
    }

    /**
     * getPersonId method returns the personId of the Like.
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * setPersonId method sets the personId of the Like.
     *
     * @param personId The personId of the Like
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }
}
