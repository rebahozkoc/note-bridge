package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;
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

	public void delete(String id) {
		if (users.containsKey(id)) {
			users.remove(id);
		} else {
			throw new NotFoundException("Person '" + id + "' not found.");
		}
	}

	public List<Person> getUsers(int pageSize, int pageNumber, String sortBy) {
		List<Person> list = new ArrayList<>(users.values());

		if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getId(), pt2.getId()));
		else if ("lastUpDate".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpdate(), pt2.getLastUpdate()));
		else throw new NotSupportedException("Sort field not supported");

		return (List<Person>) Utils.pageSlice(list, pageSize, pageNumber);
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
			throw new RuntimeException(e);
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
				statement.setString(2, newPerson.getName());
			}
			if (newPerson.getLastname() == null) {
				statement.setNull(3, java.sql.Types.VARCHAR);
			} else {
				statement.setString(3, newPerson.getLastname());
			}

			statement.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return newPerson;
	}

	public Person update(Person updated) {
		// TODO: add authentication layer
		BaseUserDao.INSTANCE.update(updated);
		String sql = """
						UPDATE Person
						SET name = ?,
						lastname = ?
						WHERE id = ?;
				""";
		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setString(1, updated.getName());
			statement.setString(2, updated.getLastname());
			statement.setInt(3, updated.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return updated;
	}
}
