package ut.twente.notebridge.model;

import java.sql.Timestamp;

/**
 * This class represents a post in the application.
 * This class is used to store the information of a post
 */
public class Post extends BaseEntity {
    /**
     * The id of the person who has created the post.
     */
    private int personId;
    /**
     * The title of the post.
     */
    private String title;
    /**
     * The description of the post.
     */
    private String description;
    /**
     * The id of the person who has sponsored the post.
     */
    private Integer sponsoredBy;
    /**
     * The date and time the post is sponsored from.
     */
    private Timestamp sponsoredFrom;
    /**
     * The date and time the post is sponsored until.
     */
    private Timestamp sponsoredUntil;
    /**
     * The type of the event in the post.
     */
    private String eventType;
    /**
     * The location of the event in the post.
     */
    private String location;

    /**
     * Default constructor for Post.
     */
    public Post() {
        super();
        this.personId = 0;
        this.title = null;
        this.description = null;
        this.sponsoredBy = null;
        this.sponsoredFrom = null;
        this.sponsoredUntil = null;
        this.eventType = null;
        this.location = null;
    }

    /**
     * getPersonId method returns the personId of the Post.
     * this id refers to the author of the post
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * setPersonId method sets the personId of the Post.
     *
     * @param personId The personId of the Post
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    /**
     * getTitle method returns the title of the Post.
     */
    public String getTitle() {
        return title;
    }

    /**
     * setTitle method sets the title of the Post.
     *
     * @param title The title of the Post
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getDescription method returns the description of the Post.
     */
    public String getDescription() {
        return description;
    }

    /**
     * setDescription method sets the description of the Post.
     *
     * @param description The description of the Post
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getSponsoredBy method returns the id of the Sponsor of the Post.
     */
    public Integer getSponsoredBy() {
        return sponsoredBy;
    }

    /**
     * setSponsoredBy method sets the id of the Sponsor of the Post.
     *
     * @param sponsoredBy The id of the Sponsor of the Post
     */
    public void setSponsoredBy(Integer sponsoredBy) {
        this.sponsoredBy = sponsoredBy;
    }

    /**
     * getSponsoredFrom method returns the date and time the post is sponsored from.
     */
    public Timestamp getSponsoredFrom() {
        return sponsoredFrom;
    }

    /**
     * setSponsoredFrom method sets the date and time the post is sponsored from.
     *
     * @param sponsoredFrom The date and time the post is sponsored from
     */
    public void setSponsoredFrom(Timestamp sponsoredFrom) {
        this.sponsoredFrom = sponsoredFrom;
    }

    /**
     * getSponsoredUntil method returns the date and time the post is sponsored until.
     */
    public Timestamp getSponsoredUntil() {
        return sponsoredUntil;
    }

    /**
     * setSponsoredUntil method sets the date and time the post is sponsored until.
     *
     * @param sponsoredUntil The date and time the post is sponsored until
     */
    public void setSponsoredUntil(Timestamp sponsoredUntil) {
        this.sponsoredUntil = sponsoredUntil;
    }

    /**
     * getEventType method returns the type of the event in the post.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * setEventType method sets the type of the event in the post.
     *
     * @param eventType The type of the event in the post
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * getLocation method returns the location of the event in the post.
     */
    public String getLocation() {
        return location;
    }

    /**
     * setLocation method sets the location of the event in the post.
     *
     * @param location The location of the event in the post
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
