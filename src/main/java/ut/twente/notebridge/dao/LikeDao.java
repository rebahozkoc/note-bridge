package ut.twente.notebridge.dao;

import ut.twente.notebridge.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is used to interact with the database for the Like model.
 */
public enum LikeDao {
    /**
     * The instance of the LikeDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Checks if a person liked a post.
     *
     * @param postId   The id of the post
     * @param personId The id of the person
     * @return true if the person liked the post, false otherwise
     */
    // Check if the user with given id, liked the post with given id
    public Boolean isLiked(int postId, int personId) {
        String sql = """
                    SELECT EXISTS(SELECT* FROM personlikespost WHERE postid=? AND personid=?);
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
     * Extracts the total number of likes for a post.
     *
     * @param postId The id of the post
     */
    public int getTotalLikes(int postId) {
        String sql = """
                    SELECT COUNT(*) FROM personlikespost WHERE postid=? GROUP BY postid;
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
}

