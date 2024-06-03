package ut.twente.notebridge.model;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Message extends BaseEntity{
    private User user1;
    private User user2;
    private List<String> messagesFromUser1;
    private List<String> messagesFromUser2;

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public List<String> getMessagesFromUser1() {
        return messagesFromUser1;
    }

    public void setMessagesFromUser1(List<String> messagesFromUser1) {
        this.messagesFromUser1 = messagesFromUser1;
    }

    public List<String> getMessagesFromUser2() {
        return messagesFromUser2;
    }

    public void setMessagesFromUser2(List<String> messagesFromUser2) {
        this.messagesFromUser2 = messagesFromUser2;
    }

    public List<String> getUserList(String user){
        if (Objects.equals(user, user1.getUsername())) {
            return messagesFromUser1;
        } else if (Objects.equals(user, user2.getUsername())) {
            return messagesFromUser2;
        }else {
            return null;
        }
    }

    public Message() {
        this.user1 = null;
        this.user2 = null;
        this.messagesFromUser1 = null;
        this.messagesFromUser2 = null;
    }
}
