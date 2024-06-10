package ut.twente.notebridge.dao;

import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

public enum BaseUserDao {
	INSTANCE;

	public BaseUser create(BaseUser newUser) {
		String sql = """
						INSERT INTO BaseUser (createDate,
						lastUpdate, username,
						picture, phoneNumber,
						password, email)
						VALUES (?, ?, ?, ?, ?, ?, ?);
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			Timestamp currentTime = Timestamp.from(Instant.now());
			newUser.setCreateDate(currentTime);
			newUser.setLastUpdate(currentTime);
			statement.setTimestamp(1, currentTime);
			statement.setTimestamp(2, currentTime);
			statement.setString(3, newUser.getUsername());
			if (newUser.getPicture() == null) {
				statement.setNull(4, java.sql.Types.VARCHAR);
			} else {
			statement.setString(4, newUser.getPicture());
			}
			if (newUser.getPhoneNumber() == null) {
				statement.setNull(5, java.sql.Types.VARCHAR);
			} else {
				statement.setString(5, newUser.getPhoneNumber());
			}
			statement.setString(6, Security.hashPassword(newUser.getPassword()));
			statement.setString(7, newUser.getEmail());

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
			throw new RuntimeException(e);
		}
	}


}
