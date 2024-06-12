package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;

public enum MessageDao {
    INSTANCE;

    private static final String ORIGINAL_MESSAGES = Utils.getAbsolutePathToResources() + "/mock-post-dataset.json";

    private static final String UPDATED_MESSAGES = Utils.getAbsolutePathToResources() + "/updated-mock-user-dataset.json";


    private final HashMap<String, MessageHistory> messenger=new HashMap<>();

    public List<Message> getMessages(int pageSize, int pageNumber, String sortBy, String user) {
        List<Message> list = null;
        System.out.println("GET messages called");
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
					SELECT json_agg(privatemessage)
					FROM notebridge.privatemessage, notebridge.privatemessagehistory
					WHERE notebridge.privatemessagehistory.user1=? OR notebridge.privatemessagehistory.user2=?;
					""");
            ps.setInt(1, Integer.parseInt(user));
            ps.setInt(2, Integer.parseInt(user));
            ResultSet rs = ps.executeQuery();
            ObjectMapper mapper = JsonMapper.builder()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .build();
            if (rs.next()) {
                list = Arrays.asList(mapper.readValue(rs.getString("json_agg"), Message[].class));
            }
            ps.close();
            rs.close();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return (List<Message>) Utils.pageSlice(list,pageSize,pageNumber);
//        List<Message> list = null;
//
//        if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
//           list=messenger.get(user).getMessagesSortedOnTime();
//        else
//            throw new NotSupportedException("Sort field not supported");
//
    }

    public List<BaseUser> getContacts(int pageSize, int pageNumber, String sortBy,String user){
        List<BaseUser> contacts=null;
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
     
					SELECT json_agg(u)
					FROM baseuser u,
					privatemessagehistory h
					WHERE (h.user1=? OR h.user2=?) AND (u.id=user1 OR u.id=user2) AND u.id!=?;
					""");
            ps.setInt(1, Integer.parseInt(user));
            ps.setInt(2, Integer.parseInt(user));
            ps.setInt(3, Integer.parseInt(user));
            ResultSet rs = ps.executeQuery();
            ObjectMapper mapper = JsonMapper.builder()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .build();
            if (rs.next()) {
                contacts = Arrays.asList(mapper.readValue(rs.getString("json_agg"), BaseUser[].class));
            }

        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return (List<BaseUser>) Utils.pageSlice(contacts,pageSize,pageNumber);
    }

    public void delete(Integer id, Integer user) {
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
					UPDATE notebridge.privatemessagehistory
					SET user1 = CASE WHEN user1 = ? THEN NULL ELSE user1 END,
					user2 = CASE WHEN user2 = ? THEN NULL ELSE user2 END
					WHERE id = ? AND (user1 = ? OR user2 = ?);
					""");
        ps.setInt(1, user);
        ps.setInt(2, user);
        ps.setInt(3, user);
        ps.setInt(4, id);
        ps.setInt(5, user);
        ps.executeQuery();
        ps.close();
            PreparedStatement ps1 = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
     
					DELETE FROM notebridge.privatemessagehistory
					WHERE user1 IS NULL AND user2 IS NULL;
					""");
            ps1.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        if(messenger.containsKey(id)) {
//            messenger.remove(id);
//        } else {
//            throw new NotFoundException("Message History '" + id + "' not found.");
//        }
    }

    public void deleteMessage(Message message){
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
					DELETE FROM privatemessage
					WHERE user_id=? AND content=? AND createddate=?
					""");
            ps.setInt(1, Integer.parseInt(message.getUser()));
            ps.setString(2, message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String string  = dateFormat.format(message.getDate());
            ps.setString(3, string);
            ps.executeQuery();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        if(messenger.containsKey(id)) {
//            if (messenger.get(id).getUserList(message.getUser()).contains(message)){
//                messenger.get(id).getUserList(message.getUser()).remove(message);
//            }else {
//                throw new NotFoundException("Message '" + message + "' not found.");
//            }
//        } else {
//            throw new NotFoundException("Message History '" + id + "' not found.");
//        }
    }
    public List<Message> getMessages(String user) {
        List<Message> list = null;
        System.out.println("GET messages called");
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
					SELECT json_agg(privatemessage)
					FROM notebridge.privatemessage, notebridge.privatemessagehistory
					WHERE notebridge.privatemessagehistory.user1=? OR notebridge.privatemessagehistory.user2=?;
					""");
            ps.setInt(1, Integer.parseInt(user));
            ps.setInt(2, Integer.parseInt(user));
            ResultSet rs = ps.executeQuery();
            ObjectMapper mapper = JsonMapper.builder()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .build();
            if (rs.next()) {
                list = Arrays.asList(mapper.readValue(rs.getString("json_agg"), Message[].class));
            }
            ps.close();
            rs.close();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return list;
//        var pt = messenger.get(id);
//
//        if (pt == null) {
//            throw new NotFoundException("Message History '" + id + "' not found!");
//        }
//
//        return pt;
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

    public void createNewMessage(String contact, Message message) {
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
				INSERT INTO privatemessage(content,createddate,user_id,messagehistory_id)
				VALUES(?,current_timestamp,?,get_history_id(?,?));
					""");
            ps.setString(1,message.getMessage());
            ps.setInt(2, Integer.parseInt(message.getUser()));
            ps.setInt(3, Integer.parseInt(message.getUser()));
            ps.setInt(4, Integer.parseInt(contact));
            ps.executeQuery();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        if (messenger.containsKey(id)){
//            messenger.get(id).getUserList(id).add(new Message(id,message));
//        }else {
//            throw new NotFoundException("Message History '" + id + "' not found.");
//        }
    }

    public void create(String user1, String user2) {
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
				INSERT INTO privatemessagehistory(user1,user2)
				VALUES(?,?);
					""");
            ps.setInt(1, Integer.parseInt(user1));
            ps.setInt(2, Integer.parseInt(user2));
            ps.executeQuery();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        String nextId = "" + (getMaxId() + 1);
//
//        newMessageHistory.setId(Integer.parseInt(nextId));
//        newMessageHistory.setCreateDate(Timestamp.valueOf(Instant.now().toString()));
//        messenger.put(nextId,newMessageHistory);
//
//        return newMessageHistory;
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



