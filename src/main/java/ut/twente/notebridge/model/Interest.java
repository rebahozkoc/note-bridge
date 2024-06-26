package ut.twente.notebridge.model;

/**
 * This class represents an interest in the application.
 * This class is used to store the interests of users in posts
 */
public class Interest {
    /**
     * The id of the post on which an interest is shown.
     */
    private int postId;
    /**
     * The id of the person who has shown interest in the post.
     */
    private int personId;

    /**
     * Default constructor for Interest.
     */
    public Interest() {
    }

    /**
     * getPostId method returns the postId of the Interest.
     */
    public int getPostId() {
        return postId;
    }

    /**
     * setPostId method sets the postId of the Interest.
     *
     * @param postId The postId of the Interest
     */
    public void setPostId(int postId) {
        this.postId = postId;
    }

    /**
     * getPersonId method returns the personId of the Interest.
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * setPersonId method sets the personId of the Interest.
     *
     * @param personId The personId of the Interest
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }
}
