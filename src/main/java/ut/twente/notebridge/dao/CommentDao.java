package ut.twente.notebridge.dao;

import ut.twente.notebridge.model.Comment;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;

import java.sql.*;
import java.time.Instant;

public enum CommentDao {

    INSTANCE;


    public Comment create(Comment comment) {
        String sql= """
                INSERT INTO comment (createdate,lastupdate,content,postid,personid)
                VALUES (?,?,?,?,?);
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp currentTime = Timestamp.from(Instant.now());
            comment.setCreateDate(currentTime);
            comment.setLastUpdate(currentTime);
            statement.setTimestamp(1, currentTime);
            statement.setTimestamp(2, currentTime);
            statement.setString(3, Security.sanitizeInput(comment.getContent()));
            statement.setInt(4, comment.getPostId());
            statement.setInt(5, comment.getPersonId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }

            return comment;
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Creating comment failed, no ID obtained.");
        }
    }
}
