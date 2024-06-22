package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;

public enum MessageDao {
    INSTANCE;

    public List<Message> getMessages(int pageSize, int pageNumber, String sortBy, String user1, String user2) {
        List<Message> list = null;
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
					SELECT json_agg(pm)
					FROM notebridge.privatemessage pm
					WHERE pm.messagehistory_id = (SELECT id FROM privatemessagehistory WHERE (user1=? AND user2=?) OR (user1=? AND user2=?));
					""");
            ps.setInt(1, Integer.parseInt(user1));
            ps.setInt(2, Integer.parseInt(user2));
            ps.setInt(3, Integer.parseInt(user2));
            ps.setInt(4, Integer.parseInt(user1));
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
    }

    public void deleteMessage(int id) {
        String sql = """
				DELETE FROM privatemessage WHERE id=?
				
		"""; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not delete message.");
        }
    }

    public Message createNewMessage(String contact, Message message) {
        String sql = "INSERT INTO privatemessage(content,createdate,user_id,messagehistory_id)" +
                "VALUES(?,current_timestamp,?,get_history_id(?,?));";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, message.getContent());
            statement.setInt(2, message.getUser_id());
            statement.setInt(3, Integer.parseInt(contact));
            statement.setInt(4, message.getUser_id());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getInt(1));
                    message.setMessagehistory_id(generatedKeys.getInt(4));
                } else {
                    throw new SQLException("Creating history failed, no ID obtained.");
                }
            }
            return message;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageHistory create(MessageHistory mh) {
        String sql = "INSERT INTO privatemessagehistory(user1,user2) " + "VALUES(?,?)";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, Integer.parseInt(mh.getUser1()));
            statement.setInt(2, Integer.parseInt(mh.getUser2()));
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mh.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating history failed, no ID obtained.");
                }
            }
            return mh;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public void deleteAllMessages(){
        String sql = "DELETE FROM privatemessage"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " messages");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all messages");
        }
    }

    public void deleteAllHistories(){
        String sql = "DELETE FROM privatemessagehistory";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " histories");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all histories");
        }
    }

    public int getTotalMessages() {
        return 0;
    }
}



