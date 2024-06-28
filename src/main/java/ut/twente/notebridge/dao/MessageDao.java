package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;

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

/**
 * This class is used to interact with the database for the Message model.
 */
public enum MessageDao {
    /**
     * The instance of the MessageDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Returns a list of all messages between two users in the database.
     *
     * @param user1 The id of the first user
     * @param user2 The id of the second user
     * @return The list of messages
     */
    public List<Message> getMessages(int pageSize, int pageNumber, String sortBy, String user1, String user2) {
        List<Message> list = null;
        try {
            PreparedStatement ps = DatabaseConnection.INSTANCE.getConnection().prepareStatement("""
                    SELECT json_agg(pm)
                    FROM privatemessage pm
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
        return (List<Message>) Utils.pageSlice(list, pageSize, pageNumber);
    }

    /**
     * Returns a list of all contacts of a user in the database.
     *
     * @param user The id of the user
     * @return The list of messages
     */
    public List<BaseUser> getContacts(int pageSize, int pageNumber, String sortBy, String user) {
        List<BaseUser> contacts = null;
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
        return (List<BaseUser>) Utils.pageSlice(contacts, pageSize, pageNumber);
    }

    /**
     * Deletes a message from the database.
     */
    public void delete(Integer id, Integer user) {
        String sql = """
                               UPDATE privatemessagehistory
                SET user1 = CASE WHEN user1 = ? THEN NULL ELSE user1 END,
                user2 = CASE WHEN user2 = ? THEN NULL ELSE user2 END
                WHERE id = ? AND (user1 = ? OR user2 = ?);
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, user);
            statement.setInt(2, user);
            statement.setInt(3, user);
            statement.setInt(4, id);
            statement.setInt(5, user);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting person with id " + id);
        }
        String sql2 = """
                              DELETE FROM notebridge.privatemessagehistory
                WHERE user1 IS NULL AND user2 IS NULL;
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql2)) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a message from the database.
     */
    public void deleteMessage(Message m, String timestamp) {
        String sql = """
                		DELETE FROM privatemessage WHERE user_id=? AND createddate=?::timestamp AND content=?
                		
                """; // Assuming delete_post takes one parameter
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, m.getId());
            statement.setString(2, timestamp);
            statement.setString(3, m.getContent());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not delete message.");
        }
    }

    /**
     * Creates a new message in the database.
     *
     * @param contact The id of the contact
     * @param message The message to be created
     * @return The created message
     */
    public Message createNewMessage(String contact, Message message) {
        String sql = """
                INSERT INTO privatemessage(content,createddate,user_id,messagehistory_id)
                      VALUES(?,current_timestamp(3),?,get_history_id(?,?));
                		""";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, message.getContent());
            statement.setInt(2, message.getUser_id());
            statement.setInt(3, message.getUser_id());
            statement.setInt(4, Integer.parseInt(contact));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getInt(1));
                    message.setCreateddate(generatedKeys.getTimestamp(3));
                    message.setMessagehistory_id(generatedKeys.getInt(5));
                } else {
                    throw new SQLException("Creating post failed, no ID obtained.");
                }
            }
            return message;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new message history in the database.
     *
     * @param mh The message history to be created
     * @return The created message history
     */
    public MessageHistory create(MessageHistory mh) {
        String sql = "INSERT INTO privatemessagehistory(user1,user2) " +
                "VALUES(?,?)";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, Integer.parseInt(mh.getUser1()));
            statement.setInt(2, Integer.parseInt(mh.getUser2()));
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating history failed, no rows affected.");
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

    /**
     * Deletes all messages in the database.
     */
    public void deleteAllMessages() {
        String sql = "DELETE FROM privatemessage"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " messages");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all messages");
        }
    }

    /**
     * Deletes all message histories in the database.
     */
    public void deleteAllHistories() {
        String sql = "DELETE FROM privatemessagehistory";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " histories");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all histories");
        }
    }

    /**
     * Sets a message as read in the database.
     */
    public void readMessages(int id) {

        String sql = "UPDATE privatemessage SET isread=true WHERE id=?";
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection()
                .prepareStatement(sql)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            System.out.println("Read " + affectedRows + " messages");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns total number of unread messages for a user.
     */
    public int countUnreadMessages(int user, int contact) {
        String sql = "SELECT COUNT(id) FROM privatemessage WHERE isread=false AND messagehistory_id=get_history_id(?,?) AND user_id=?";
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection()
                .prepareStatement(sql)) {
            statement.setInt(1, user);
            statement.setInt(2, contact);
            statement.setInt(3, contact);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);

            } else {
                //no rows returned, post with that id does not exist
                throw new NotFoundException();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public int getTotalMessages() {
        return 0;
    }
}



