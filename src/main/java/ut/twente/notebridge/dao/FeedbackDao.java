package ut.twente.notebridge.dao;

import ut.twente.notebridge.model.Feedback;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * This class is used to interact with the database for the Feedback model.
 */
public enum FeedbackDao {
    /**
     * The instance of the FeedbackDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Creates a new feedback in the database.
     *
     * @param newFeedback The feedback to be created
     * @return The created feedback
     */
    public Feedback create(Feedback newFeedback) {
        String sql = """
                		INSERT INTO Feedback (createDate,
                		lastUpdate,
                		email, message)
                		VALUES (?, ?, ?, ?);
                """;


        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp currentTime = Timestamp.from(Instant.now());
            newFeedback.setCreateDate(currentTime);
            newFeedback.setLastUpdate(currentTime);
            statement.setTimestamp(1, currentTime);
            statement.setTimestamp(2, currentTime);
            statement.setString(3, Security.sanitizeInput(newFeedback.getEmail()));
            if (newFeedback.getMessage() == null) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, Security.sanitizeInput(newFeedback.getMessage()));
            }


            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating feedback failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newFeedback.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating feedback failed, no ID obtained.");
                }
            }
            return newFeedback;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Creating feedback failed, no ID obtained.");
        }
    }

}
