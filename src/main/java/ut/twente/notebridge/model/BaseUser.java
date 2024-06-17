package ut.twente.notebridge.model;


public class BaseUser extends BaseEntity {

    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String picture;
    private String description;


    public BaseUser() {
        super();
        this.username = null;
        this.email = null;
        this.password = null;
        this.phoneNumber = null;
        this.picture = null;
        this.description = null;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

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
