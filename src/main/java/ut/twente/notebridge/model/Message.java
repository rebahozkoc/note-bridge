package ut.twente.notebridge.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Message extends BaseEntity {
    private int idOfMessageHistory;
    private String user;
    private Timestamp date;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {
        idOfMessageHistory=0;
        user=null;
        date=null;
        message=null;
    }

    public Message(String user, String message) {
        this.user=user;
        this.message=message;
    }

    public int getIdForMessage() {
        return idOfMessageHistory;
    }

    public void setIdForMessage(int id) {
        this.idOfMessageHistory = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
