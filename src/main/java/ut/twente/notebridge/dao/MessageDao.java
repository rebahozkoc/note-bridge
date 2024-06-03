package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import ut.twente.notebridge.utils.Utils;
import ut.twente.notebridge.model.Message;

public enum MessageDao {
    INSTANCE;

    private static final String ORIGINAL_MESSAGES = Utils.getAbsolutePathToResources() + "/mock-messages-dataset.json";

    private static final String UPDATED_MESSAGES = Utils.getAbsolutePathToResources() + "/updated-mock-user-dataset.json";


    private final HashMap<Integer,Message> messenger=new HashMap<>();

    public List<Message> getMessages(int pageSize, int pageNumber, String sortBy) {
        List<Message> list = new ArrayList<>(messenger.values());

        if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
            list.sort((pt1, pt2) -> Utils.compare(pt1.getId(), pt2.getId()));
        else if ("lastUpDate".equals(sortBy))
            list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpdate(), pt2.getLastUpdate()));
        else
            throw new NotSupportedException("Sort field not supported");

        return (List<Message>) Utils.pageSlice(list,pageSize,pageNumber);
    }
    public void delete(String id) {
        if(messenger.containsKey(id)) {
            messenger.remove(id);
        } else {
            throw new NotFoundException("Message History '" + id + "' not found.");
        }
    }

    public void deleteMessage(String user, String message){
        if(messenger.containsKey(user)) {
            List<String> messageHistory=messenger.get(user).getUserList(user);
            if (messageHistory.contains(message)){
                messageHistory.remove(message);
            }else {
                throw new NotFoundException("Message '" + message + "' not found.");
            }
        } else {
            throw new NotFoundException("Message History '" + user + "' not found.");
        }
    }
    public Message getMessages(String id) {
        var pt = messenger.get(id);

        if (pt == null) {
            throw new NotFoundException("Message History '" + id + "' not found!");
        }

        return pt;
    }

    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File source = existsPosts() ?
                new File(UPDATED_MESSAGES) :
                new File(ORIGINAL_MESSAGES);
        Message[] arr = mapper.readValue(source, Message[].class);

        Arrays.stream(arr).forEach(pt -> messenger.put(pt.getId(), pt));
    }

    private boolean existsPosts() {
        File f = new File(UPDATED_MESSAGES);
        return f.exists() && !f.isDirectory();
    }

    public void createNewMessage(String user, String message) {
        if (messenger.containsKey(user)){
            messenger.get(user).getUserList(user).add(message);
        }else {
            throw new NotFoundException("Message History '" + user + "' not found.");
        }
    }

    public Message create(Message newMessageHistory) {
        int nextId = (getMaxId() + 1);

        newMessageHistory.setId(nextId);
        newMessageHistory.setCreateDate(Timestamp.valueOf(Instant.now().toString()));
        newMessageHistory.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
        messenger.put(nextId,newMessageHistory);

        return newMessageHistory;
    }

    private int getMaxId() {
        Set<Integer> ids = messenger.keySet();
        return ids.isEmpty() ? 0 : ids.stream()
                .max(Integer::compareTo)
                .get();
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        File destination = new File(UPDATED_MESSAGES);

        writer.writeValue(destination, messenger.values());
    }

    public int getTotalMessages() {
        return messenger.keySet().size();
    }
}



