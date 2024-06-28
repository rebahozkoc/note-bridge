package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import ut.twente.notebridge.dto.PostDto;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.model.Person;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to interact with the database for the Person model.
 */
public enum PersonDao {
    /**
     * The instance of the PersonDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Deletes a person from the database.
     *
     * @param id The id of the person to be deleted
     */
    public void delete(int id) {
        String sql = "DELETE FROM person WHERE id = ?";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting person with id " + id);
        }
    }

    /**
     * Deletes all persons from the database.
     * Testing purposes only.
     */
    public void deleteAll() {
        String sql = "DELETE FROM person";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " persons");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all persons");
        }
    }

    /**
     * Returns a person in the database.
     *
     * @param id The id of the person
     * @return the person in the database
     */
    public Person getUser(int id) {
        String sql = "SELECT row_to_json(t) person FROM(SELECT * FROM person JOIN baseUser ON person.id = baseUser.id WHERE person.id=?) t"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String json = rs.getString("person");

                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                Person person = mapper.readValue(json, Person.class);
                person.setPassword("hidden");
                return person;

            } else {
                //no rows returned, post with that id does not exist
                throw new NotFoundException();
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting person user");
        }
    }

    /**
     * Creates a person in the database.
     *
     * @param newPerson The person to be created
     * @return the created person
     */
    public Person create(Person newPerson) {
        String sql = """
                		INSERT INTO Person (id,
                		name, lastname)
                		VALUES (?, ?, ?);
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, newPerson.getId());
            if (newPerson.getName() == null) {
                statement.setNull(2, java.sql.Types.VARCHAR);
            } else {
                statement.setString(2, Security.sanitizeInput(newPerson.getName()));
            }
            if (newPerson.getLastname() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, Security.sanitizeInput(newPerson.getLastname()));
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while creating person user");
        }

        return newPerson;
    }

    /**
     * Updates a person in the database.
     * Uses the BaseUserDao to update the baseuser table as well.
     *
     * @param updated The person to be updated
     * @return the updated person
     */
    public Person update(Person updated) {
        BaseUserDao.INSTANCE.update(updated);
        String sql = """
                		UPDATE Person
                		SET name = ?,
                		lastname = ?
                		WHERE id = ?;
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setString(1, Security.sanitizeInput(updated.getName()));
            statement.setString(2, Security.sanitizeInput(updated.getLastname()));
            statement.setInt(3, updated.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while updating person user");
        }

        return updated;
    }

    /**
     * Returns the id of a person in the database.
     *
     * @param username The username of the person
     * @return the id of the person
     */
    public Integer getID(String username) {
        String sql = "SELECT id FROM baseuser WHERE username=?";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);

            } else {
                //no rows returned, post with that id does not exist
                throw new NotFoundException();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting id");
        }
    }

    /**
     * Returns all interested posts of a person in the database.
     *
     * @param personId The id of the person
     * @return all interested posts of a person in the database
     */
    public List<PostDto> getInterestedPosts(int personId) {
        String sql = """
                SELECT json_agg(t) FROM(SELECT p.*
                FROM post p, personinterestedinpost i
                WHERE p.id=i.postid AND i.personid=?) t;
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, personId);
            ResultSet rs = statement.executeQuery();
            //Since we get json_agg column, in case there is no instance we return null value for our json
            if (rs.next()) {
                String json = rs.getString(1);
                if (json == null) {
                    return new ArrayList<>();
                } else {
                    ObjectMapper mapper = JsonMapper.builder()
                            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                            .build();

                    List<PostDto> posts = Arrays.asList(mapper.readValue(json, PostDto[].class));
                    for (PostDto post : posts) {
                        post.setImage(PostDao.INSTANCE.getFirstImage(post.getId()));
                    }
                    return posts;
                }
            } else {
                throw new SQLException();
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting interested posts");
        }

    }

    /**
     * Checks if two users are contacts.
     *
     * @param user1 The id of the first user
     * @param user2 The id of the second user
     * @return true if the users are contacts, false otherwise
     */
    public Boolean isContact(int user1, int user2) {
        String sql = """
                SELECT EXISTS(SELECT* FROM privatemessagehistory WHERE (user1=? AND user2=?) OR (user1=? AND user2=?));
                                                                                                                             
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, user1);
            statement.setInt(2, user2);
            statement.setInt(3, user2);
            statement.setInt(4, user1);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            } else {
                throw new SQLException();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while checking if users are contacts");
        }
    }

    /**
     * Creates a contact between two users.
     *
     * @param contactTuple The contact tuple
     */
    public void createContact(MessageHistory contactTuple) {
        String sql = """
                INSERT INTO privatemessagehistory (user1,user2)
                VALUES (?,?);
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(contactTuple.getUser1()));
            statement.setInt(2, Integer.parseInt(contactTuple.getUser2()));
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while creating contact");
        }
    }

}
