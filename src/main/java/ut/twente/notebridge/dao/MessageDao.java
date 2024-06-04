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
import ut.twente.notebridge.Utils;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;

public enum MessageDao {
    INSTANCE;

    private static final String ORIGINAL_MESSAGES = Utils.getAbsolutePathToResources() + "/mock-messages-dataset.json";

    private static final String UPDATED_MESSAGES = Utils.getAbsolutePathToResources() + "/updated-mock-user-dataset.json";


    private final HashMap<String, MessageHistory> messenger=new HashMap<>();

    public List<Message> getMessages(int pageSize, int pageNumber, String sortBy, String user) {
        List<Message> list = null;

        if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
           list=messenger.get(user).getMessagesSortedOnTime();
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

    public void deleteMessage(String id , Message message){
        if(messenger.containsKey(id)) {
            if (messenger.get(id).getUserList(message.getUser()).contains(message)){
                messenger.get(id).getUserList(message.getUser()).remove(message);
            }else {
                throw new NotFoundException("Message '" + message + "' not found.");
            }
        } else {
            throw new NotFoundException("Message History '" + id + "' not found.");
        }
    }
    public MessageHistory getMessages(String id) {
        var pt = messenger.get(id);

        if (pt == null) {
            throw new NotFoundException("Message History '" + id + "' not found!");
        }

        return pt;
    }

    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File source = existsMessages() ?
                new File(UPDATED_MESSAGES) :
                new File(ORIGINAL_MESSAGES);
        MessageHistory[] arr = mapper.readValue(source, MessageHistory[].class);

        Arrays.stream(arr).forEach(pt -> messenger.put(String.valueOf(pt.getIdOfMessageHistory()), pt));
    }

    private boolean existsMessages() {
        File f = new File(UPDATED_MESSAGES);
        return f.exists() && !f.isDirectory();
    }

    public void createNewMessage(String id, String message) {
        if (messenger.containsKey(id)){
            messenger.get(id).getUserList(id).add(new Message(id,message));
        }else {
            throw new NotFoundException("Message History '" + id + "' not found.");
        }
    }

    public MessageHistory create(MessageHistory newMessageHistory) {
        String nextId = "" + (getMaxId() + 1);

        newMessageHistory.setId(Integer.parseInt(nextId));
        newMessageHistory.setCreateDate(Timestamp.valueOf(Instant.now().toString()));
        messenger.put(nextId,newMessageHistory);

        return newMessageHistory;
    }

    private int getMaxId() {
        Set<String> ids = messenger.keySet();
        return ids.isEmpty() ? 0 : ids.stream()
                .map(Integer::parseInt)
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



