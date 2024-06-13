package ut.twente.notebridge.dao;

import ut.twente.notebridge.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum LikeDao {
    INSTANCE;

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
            }else{
                throw new SQLException();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while checking if post is liked");
        }

    }
}

