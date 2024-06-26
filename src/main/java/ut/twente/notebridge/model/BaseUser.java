package ut.twente.notebridge.model;

/**
 * This class represents a base user in the application.
 * Since username, email, password, phoneNumber, picture, description are common fields in all users, they are defined here
 * Other user classes extend this class
 */

public class BaseUser extends BaseEntity {

    /**
     * The user's username.
     */
    private String username;
    /**
     * The user's email.
     */
    private String email;
    /**
     * The user's password.
     */
    private String password;
    /**
     * The user's phone number.
     */
    private String phoneNumber;
    /**
     * The user's picture.
     */
    private String picture;
    /**
     * The user's description.
     */
    private String description;

    /**
     * Default constructor for BaseUser.
     */
    public BaseUser() {
        super();
        this.username = null;
        this.email = null;
        this.password = null;
        this.phoneNumber = null;
        this.picture = null;
        this.description = null;
    }

    /**
     * getDescription method returns the description of the BaseUser.
     */
    public String getDescription() {
        return description;
    }

    /**
     * setDescription method sets the description of the BaseUser.
     *
     * @param description The description of the BaseUser
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getUsername method returns the username of the BaseUser.
     */
    public String getUsername() {
        return username;
    }

    /**
     * setUsername method sets the username of the BaseUser.
     *
     * @param username The username of the BaseUser
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getEmail method returns the email of the BaseUser.
     */
    public String getEmail() {
        return email;
    }

    /**
     * setEmail method sets the email of the BaseUser.
     *
     * @param email The email of the BaseUser
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getPassword method returns the password of the BaseUser.
     */
    public String getPassword() {
        return password;
    }

    /**
     * setPassword method sets the password of the BaseUser.
     *
     * @param password The password of the BaseUser
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * getPhoneNumber method returns the phoneNumber of the BaseUser.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * setPhoneNumber method sets the phoneNumber of the BaseUser.
     *
     * @param phoneNumber The phoneNumber of the BaseUser
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * getPicture method returns the picture of the BaseUser.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * setPicture method sets the picture of the BaseUser.
     *
     * @param picture The picture of the BaseUser
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * setBaseUser method sets the fields of the BaseUser.
     *
     * @param baseUser The BaseUser object
     */
    public void setBaseUser(BaseUser baseUser) {
        this.setId(baseUser.getId());
        this.setCreateDate(baseUser.getCreateDate());
        this.setLastUpdate(baseUser.getLastUpdate());
        this.setUsername(baseUser.getUsername());
        this.setPicture(baseUser.getPicture());
        this.setPhoneNumber(baseUser.getPhoneNumber());
        this.setPassword(baseUser.getPassword());
        this.setEmail(baseUser.getEmail());
        this.setDescription(baseUser.getDescription());
    }
}
