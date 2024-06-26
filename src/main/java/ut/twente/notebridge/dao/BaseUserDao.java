package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * This class is used to interact with the database for the BaseUser model.
 * This class is used to create, read, update and delete users from the database.
 */
public enum BaseUserDao {

    /**
     * The instance of the BaseUserDao to achieve singleton design.
     */
    INSTANCE;

    /**
     * The create method is used to create a new user in the database.
     */
    public BaseUser create(BaseUser user) {
        if (user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        try {
            if (getUserByEmail(user.getEmail()) != null) {
                throw new IllegalArgumentException("Email already exists");
            }
        } catch (NotFoundException e) {
            //no user with that email exists, continue
        }
        try {
            if (getUserByUsername(user.getUsername()) != null) {
                throw new IllegalArgumentException("Username already exists");
            }
        } catch (NotFoundException e) {
            //no user with that username exists, continue
        }

        BaseUser savedUser = save(user);
        savedUser.setPassword("hidden");
        return savedUser;
    }

    /**
     * The save method is used to save a new user in the database.
     * Works together with create method.
     */
    public BaseUser save(BaseUser newUser) {
        String sql = """
                		INSERT INTO BaseUser (createDate,
                		lastUpdate, username,
                		picture, phoneNumber,
                		password, email,description)
                		VALUES (?, ?, ?, ?, ?, ?, ?,?);
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp currentTime = Timestamp.from(Instant.now());
            newUser.setCreateDate(currentTime);
            newUser.setLastUpdate(currentTime);
            statement.setTimestamp(1, currentTime);
            statement.setTimestamp(2, currentTime);
            statement.setString(3, Security.sanitizeInput(newUser.getUsername()));
            if (newUser.getPicture() == null) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, Security.sanitizeInput(newUser.getPicture()));
            }
            if (newUser.getPhoneNumber() == null) {
                statement.setNull(5, java.sql.Types.VARCHAR);
            } else {
                statement.setString(5, Security.sanitizeInput(newUser.getPhoneNumber()));
            }
            if (newUser.getDescription() == null) {
                statement.setNull(8, java.sql.Types.VARCHAR);
            } else {
                statement.setString(8, Security.sanitizeInput(newUser.getDescription()));
            }

            String hashedPassword = Security.hashPassword(newUser.getPassword());
            newUser.setPassword(hashedPassword);
            statement.setString(6, hashedPassword);
            statement.setString(7, Security.sanitizeInput(newUser.getEmail()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newUser.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            return newUser;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Creating user failed, no ID obtained.");
        }
    }

    /**
     * The getUserByEmail method is used to get a user by email from the database.
     *
     * @param email The email of the user
     */
    public BaseUser getUserByEmail(String email) {

        String sql = "SELECT row_to_json(t) baseuser FROM(SELECT * FROM BaseUser WHERE email=?) t"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String json = rs.getString("baseuser");

                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return mapper.readValue(json, BaseUser.class);

            } else {
                //no rows returned, user with that id does not exist
                throw new NotFoundException("The user with email " + email + " does not exist.");
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get user by email.");
        }
    }

    /**
     * The getUserByUsername method is used to get a user by username from the database.
     *
     * @param username The username of the user
     */
    private BaseUser getUserByUsername(String username) {
        String sql = "SELECT row_to_json(t) baseuser FROM(SELECT * FROM BaseUser WHERE username=?) t";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String json = rs.getString("baseuser");

                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return mapper.readValue(json, BaseUser.class);

            } else {
                //no rows returned, user with that id does not exist
                throw new NotFoundException("The user with username " + username + " does not exist.");
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get user by username.");
        }
    }

    /**
     * The getUser method is used to get a user by id from the database.
     *
     * @param id The id of the user
     */
    public BaseUser getUser(int id) {

        String sql = "SELECT row_to_json(t) baseuser FROM(SELECT * FROM BaseUser WHERE id=?) t";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String json = rs.getString("baseuser");

                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return mapper.readValue(json, BaseUser.class);

            } else {
                //no rows returned, post with that id does not exist
                throw new NotFoundException("The user with id " + id + " does not exist.");
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get user by id.");
        }
    }

    /**
     * The isPerson method is used to check if a user is a person.
     *
     * @param id The id of the user
     */
    public Boolean isPerson(int id) {
        String sql = """
                	SELECT EXISTS(SELECT id FROM Person WHERE id=?);
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not check if user is a person.");
        }
    }

    /**
     * The update method is used to update a user in the database.
     *
     * @param user The user to be updated
     */
    public BaseUser update(BaseUser user) {
        String sql = """
                		UPDATE BaseUser
                		SET lastUpdate = ?, username = ?, picture = ?, phoneNumber = ?, email = ?,description = ?
                		WHERE id = ?;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            Timestamp currentTime = Timestamp.from(Instant.now());
            user.setLastUpdate(currentTime);
            statement.setTimestamp(1, currentTime);
            statement.setString(2, Security.sanitizeInput(user.getUsername()));
            if (user.getPicture() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, Security.sanitizeInput(user.getPicture()));
            }
            if (user.getPhoneNumber() == null) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, Security.sanitizeInput(user.getPhoneNumber()));
            }
            if (user.getDescription() == null) {
                statement.setNull(6, java.sql.Types.VARCHAR);
            } else {
                statement.setString(6, Security.sanitizeInput(user.getDescription()));
            }
            statement.setString(5, Security.sanitizeInput(user.getEmail()));
            statement.setInt(7, user.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Updating user failed.");
        }
    }

    /**
     * The setProfilePicture method is used to set the profile picture of a user.
     *
     * @param id                  The id of the user
     * @param uploadedInputStream The input stream of the uploaded image
     * @param fileName            The name of the image file
     */
    public BaseUser setProfilePicture(int id, InputStream uploadedInputStream, String fileName) {
        //Your local disk path where you want to store the file
        BaseUser baseUser = getUser(id);
        String uuid = java.util.UUID.randomUUID().toString();
        String uploadedFileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + uuid + fileName;
        System.out.println(uploadedFileLocation);
        // save it
        File objFile = new File(uploadedFileLocation);
        if (objFile.exists()) {
            boolean res = objFile.delete();
        }

        Utils.saveToFile(uploadedInputStream, uploadedFileLocation);
        baseUser.setPicture(uuid + fileName);
        return baseUser;
    }

    /**
     * The delete method is used to delete a user from the database.
     *
     * @param id The id of the user
     */
    public void delete(int id) {
        String sql = """
                		DELETE FROM BaseUser
                		WHERE id = ?;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Deleting user failed for user with id " + id);
        }
    }

    /**
     * The deleteAll method is used to delete all users from the database.
     * This method is used for testing purposes.
     */
    public void deleteAll() {
        String sql = """
                		DELETE FROM BaseUser;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " users");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Deleting all users failed.");
        }
    }


}
