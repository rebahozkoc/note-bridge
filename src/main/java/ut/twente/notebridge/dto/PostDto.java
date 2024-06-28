package ut.twente.notebridge.dto;

import ut.twente.notebridge.model.Post;

/**
 * This class is used to store the information of a post with additional information and convey it to the end user.
 */
public class PostDto extends Post {

    /**
     * The total number of people interested in the post.
     */
    private int totalInterested;
    /**
     * The total number of likes on the post.
     */
    private int totalLikes;
    /**
     * The boolean value to check if the post has an image.
     */
    private boolean hasImage;
    /**
     * The image URL of the post.
     */
    private String image;

    /**
     * Default constructor for PostDto.
     */
    public PostDto() {
        super();
        this.hasImage = false;
        this.image = null;
        this.totalInterested = 0;
        this.totalLikes = 0;
    }

    /**
     * get method returns the total number of people interested in the post.
     *
     * @return The total number of people interested in the post
     */
    public int getTotalInterested() {
        return totalInterested;
    }

    /**
     * set method sets the total number of people interested in the post.
     *
     * @param totalInterested The total number of people interested in the post
     */
    public void setTotalInterested(int totalInterested) {
        this.totalInterested = totalInterested;
    }

    /**
     * get method returns the total number of likes on the post.
     *
     * @return The total number of likes on the post
     */
    public int getTotalLikes() {
        return totalLikes;
    }

    /**
     * set method sets the total number of likes on the post.
     *
     * @param totalLikes The total number of likes on the post
     */
    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    /**
     * setHasImage method sets the boolean value to check if the post has an image.
     *
     * @param hasImage The boolean value to check if the post has an image
     */
    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    /**
     * getHasImage method returns the boolean value to check if the post has an image.
     *
     * @return The boolean value to check if the post has an image
     */
    public boolean getHasImage() {
        return hasImage;
    }

    /**
     * get method returns the image URL of the post.
     *
     * @return The image URL of the post
     */
    public String getImage() {
        return image;
    }

    /**
     * set method sets the image URL of the post.
     *
     * @param image The image URL of the post
     */
    public void setImage(String image) {
        this.image = image;
        if (image != null) {
            this.hasImage = true;
        }
    }

    /**
     * setPost method sets the post information.
     *
     * @param post The post information
     */
    public void setPost(Post post) {
        this.setId(post.getId());
        this.setPersonId(post.getPersonId());
        this.setTitle(post.getTitle());
        this.setDescription(post.getDescription());
        this.setSponsoredBy(post.getSponsoredBy());
        this.setSponsoredFrom(post.getSponsoredFrom());
        this.setSponsoredUntil(post.getSponsoredUntil());
        this.setEventType(post.getEventType());
        this.setLocation(post.getLocation());
    }
}
