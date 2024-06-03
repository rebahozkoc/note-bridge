package ut.twente.notebridge.model;

import java.util.List;
import java.util.Objects;

public class Message extends BaseEntity {
    private Person person1;
    private Person person2;
    private List<String> messagesFromUser1;
    private List<String> messagesFromUser2;

    public Person getUser1() {
        return person1;
    }

    public void setUser1(Person person1) {
        this.person1 = person1;
    }

    public Person getUser2() {
        return person2;
    }

    public void setUser2(Person person2) {
        this.person2 = person2;
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
        if (Objects.equals(user, person1.getUsername())) {
            return messagesFromUser1;
        } else if (Objects.equals(user, person2.getUsername())) {
            return messagesFromUser2;
        }else {
            return null;
        }
    }

    public Message() {
        super();
        this.person1 = null;
        this.person2 = null;
        this.messagesFromUser1 = null;
        this.messagesFromUser2 = null;
    }
}
