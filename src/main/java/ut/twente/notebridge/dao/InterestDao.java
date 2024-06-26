package ut.twente.notebridge.dao;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ut.twente.notebridge.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is used to interact with the database for the Interest model.
 */
public enum InterestDao {
    /**
     * The instance of the InterestDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Checks if a person is interested in a post.
     *
     * @param postId   The id of the post
     * @param personId The id of the person
     * @return true if the person is interested in the post, false otherwise
     */
    public Boolean isInterested(int postId, int personId) {
        String sql = """
                    SELECT EXISTS(SELECT* FROM personinterestedinpost WHERE postid=? AND personid=?);
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, postId);
            statement.setInt(2, personId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            } else {
                throw new SQLException();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while checking if post is liked");
        }

    }

    /**
     * Extracts the total number of interests for a post.
     *
     * @param postId The id of the post
     */
    public int getTotalInterest(int postId) {
        String sql = """
                    SELECT COUNT(*) FROM personinterestedinpost WHERE postid=? GROUP BY postid;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting total likes");
        }

    }

    /**
     * Returns a list of all interested users for a post.
     *
     * @param postId The id of the post
     * @return The list of interested users for the post as a JSON string
     */
    public String getInterestedUsernames(int postId) {
        String sql = """
                    SELECT jsonb_agg(jsonb_build_object('id',b.id,'username',b.username))
                                FROM personinterestedinpost i, baseuser b
                                WHERE i.personid=b.id AND i.postid=?
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String json = rs.getString(1);
                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return json;
            } else {
                throw new SQLException();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while getting total likes");
        }

    }
}
