package ut.twente.notebridge.model;

public class MessageHistory extends BaseEntity{
    private String user1;
    private String user2;

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public MessageHistory() {
        this.user1 = null;
        this.user2 = null;
    }
}
