package ut.twente.notebridge.model;

import java.sql.Timestamp;

public class Message extends BaseEntity {
    private int messagehistory_id;
    private Integer user_id;
    private Timestamp createddate;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message() {
        messagehistory_id =0;
        user_id =null;
        createddate =null;
        content =null;
    }

    public Message(Integer user_id, String content) {
        this.user_id = user_id;
        this.content = content;
    }

    public int getMessagehistory_id() {
        return messagehistory_id;
    }

    public void setMessagehistory_id(int id) {
        this.messagehistory_id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Timestamp getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Timestamp createddate) {
        this.createddate = createddate;
    }
}
