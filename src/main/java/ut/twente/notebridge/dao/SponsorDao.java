package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class is used to interact with the database for the Sponsor model.
 */
public enum SponsorDao {
    /**
     * The instance of the SponsorDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Deletes a sponsor from the database.
     *
     * @param id The id of the sponsor to be deleted
     */
    public void delete(int id) {
        String sql = "DELETE FROM sponsor WHERE id = ?";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting sponsor failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting sponsor with id " + id);
        }
    }

    /**
     * Deletes all sponsors from the database.
     * Testing purposes only.
     */
    public void deleteAll() {
        String sql = "DELETE FROM sponsor"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " persons");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all persons");
        }
    }


    /**
     * Gets the sponsor with the given id.
     *
     * @return the sponsor with the given id
     */
    public Sponsor getSponsor(int id) {
        String sql = "SELECT row_to_json(t) sponsor FROM(SELECT * FROM sponsor JOIN baseUser ON sponsor.id = baseUser.id WHERE sponsor.id=?) t"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String json = rs.getString("sponsor");

                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                Sponsor sponsor = mapper.readValue(json, Sponsor.class);
                sponsor.setPassword("hidden");
                return sponsor;

            } else {
                //no rows returned, post with that id does not exist
                throw new NotFoundException();
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting sponsor user");
        }
    }

    /**
     * Creates a new sponsor in the database.
     *
     * @param newSponsor The sponsor to be created
     * @return The created sponsor
     */
    public Sponsor create(Sponsor newSponsor) {
        String sql = """
                		INSERT INTO Sponsor (id,
                		companyname, websiteurl)
                		VALUES (?, ?, ?);
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, newSponsor.getId());
            if (newSponsor.getCompanyName() == null) {
                statement.setNull(2, java.sql.Types.VARCHAR);
            } else {
                statement.setString(2, Security.sanitizeInput(newSponsor.getCompanyName()));
            }
            if (newSponsor.getWebsiteURL() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, Security.sanitizeInput(newSponsor.getWebsiteURL()));
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while creating sponsor user");
        }

        return newSponsor;
    }

    /**
     * Updates a sponsor in the database.
     *
     * @param updated The sponsor to be updated
     * @return The updated sponsor
     */
    public Sponsor update(Sponsor updated) {
        BaseUserDao.INSTANCE.update(updated);
        String sql = """
                		UPDATE Sponsor
                		SET companyname = ?,
                		websiteurl = ?
                		WHERE id = ?;
                """;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setString(1, Security.sanitizeInput(updated.getCompanyName()));
            statement.setString(2, Security.sanitizeInput(updated.getWebsiteURL()));
            statement.setInt(3, updated.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while updating person user");
        }

        return updated;
    }

    /**
     * Puts a sponsor on a post.
     *
     * @param post The post to be sponsored
     * @return The sponsored post
     */
    public Post sponsorPost(Post post) {
        Post currentPost = PostDao.INSTANCE.getPost(post.getId());
        currentPost.setSponsoredBy(post.getSponsoredBy());
        currentPost.setSponsoredFrom(post.getSponsoredFrom());
        currentPost.setSponsoredUntil(post.getSponsoredUntil());

        return PostDao.INSTANCE.updateSponsorship(currentPost);
    }
}
