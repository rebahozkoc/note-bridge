package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.model.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum PersonDao {
	INSTANCE;

	private final HashMap<Integer, Person> users = new HashMap<>();

	public void delete(int id) {
		String sql = "DELETE FROM person WHERE id = ?"; // Assuming delete_post takes one parameter

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

	public void deleteAll(){
		String sql = "DELETE FROM person"; // Assuming delete_post takes one parameter

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			int affectedRows = statement.executeUpdate();
			System.out.println("Deleted " + affectedRows + " persons");

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while deleting all persons");
		}
	}

	public List<Person> getUsers(int pageSize, int pageNumber, String sortBy) {
		// TODO implement getUsers or delete
		List<Person> list = new ArrayList<>(users.values());
		return list;
	}

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
}
