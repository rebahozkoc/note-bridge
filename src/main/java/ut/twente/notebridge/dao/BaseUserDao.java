package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;

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
			String hashedPassword = Security.hashPassword(newUser.getPassword());
			newUser.setPassword(hashedPassword);
			statement.setString(6, hashedPassword);
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
				//no rows returned, post with that id does not exist
				throw new NotFoundException();
			}

		} catch (SQLException | JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Boolean isPerson(int id){
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
			throw new RuntimeException(e);
		}
	}
}
