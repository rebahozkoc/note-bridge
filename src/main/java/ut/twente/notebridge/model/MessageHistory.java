package ut.twente.notebridge.model;

import java.sql.Timestamp;
import java.util.*;

public class MessageHistory extends BaseEntity{
    int id;
    private BaseUser user1;
    private BaseUser user2;
    private List<Message> messagesFromUser1;
    private List<Message> messagesFromUser2;

    public BaseUser getUser1() {
        return user1;
    }

    public void setUser1(BaseUser user1) {
        this.user1 = user1;
    }

    public BaseUser getUser2() {
        return user2;
    }

    public void setUser2(BaseUser user2) {
        this.user2 = user2;
    }

    public List<Message> getMessagesFromUser1() {
        return messagesFromUser1;
    }

    public void setMessagesFromUser1(List<Message> messagesFromUser1) {
        this.messagesFromUser1 = messagesFromUser1;
    }

    public List<Message> getMessagesFromUser2() {
        return messagesFromUser2;
    }

    public void setMessagesFromUser2(List<Message> messagesFromUser2) {
        this.messagesFromUser2 = messagesFromUser2;
    }

    public int getIdOfMessageHistory() {
        return id;
    }

    public void setIdOfMessageHistory(int id) {
        this.id = id;
    }

    public List<Message> getUserList(String user){
        if (Objects.equals(user, user1.getUsername())) {
            return messagesFromUser1;
        } else if (Objects.equals(user, user2.getUsername())) {
            return messagesFromUser2;
        }else {
            return null;
        }
    }

    public MessageHistory() {
        this.id=0;
        this.user1 = null;
        this.user2 = null;
        this.messagesFromUser1 = null;
        this.messagesFromUser2 = null;
    }

    public List<Message> getMessagesSortedOnTime() {
        List<Message> sorted=new ArrayList<>();
        for (Message message1:messagesFromUser1){
            for(Message message2:messagesFromUser2){
                Timestamp date1=message1.getCreateddate();
                Timestamp date2=message2.getCreateddate();
                if (date1.after(date2)){
                    sorted.add(message1);
                }else {
                    sorted.add(message2);
                }
            }
        }
        return sorted;
    }
}
