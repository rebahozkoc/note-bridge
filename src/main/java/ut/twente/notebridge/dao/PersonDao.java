package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;
import ut.twente.notebridge.model.Person;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public enum PersonDao {
	INSTANCE;

	private static final String ORIGINAL_USERS = Utils.getAbsolutePathToResources() + "/mock-user-dataset.json";
	private static final String UPDATED_USERS = Utils.getAbsolutePathToResources() + "/updated-mock-user-dataset.json";

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
		else
			throw new NotSupportedException("Sort field not supported");

		return (List<Person>) Utils.pageSlice(list, pageSize, pageNumber);
	}

	public Person getUser(int id) {
		var pt = users.get(id);

		if (pt == null) {
			throw new NotFoundException("Person '" + id + "' not found!");
		}

		return pt;
	}

	public void load() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File source = existsUsers() ?
				new File(UPDATED_USERS) :
				new File(ORIGINAL_USERS);
		Person[] arr = mapper.readValue(source, Person[].class);

		Arrays.stream(arr).forEach(pt -> users.put(pt.getId(), pt));
	}

	private boolean existsUsers() {
		File f = new File(UPDATED_USERS);
		return f.exists() && !f.isDirectory();
	}

	public void save() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		File destination = new File(UPDATED_USERS);

		writer.writeValue(destination, users.values());
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

	private int getMaxId() {
		Set<Integer> ids = users.keySet();
		return ids.isEmpty() ? 0 : ids.stream()
				.max(Integer::compareTo)
				.get();
	}

	public Person update(Person updated) {
		if (!updated.isValid())
			throw new BadRequestException("Invalid user.");
		if (users.get(updated.getId()) == null)
			throw new NotFoundException("Person id '" + updated.getId() + "' not found.");

		updated.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
		users.put(updated.getId(), updated);

		return updated;
	}

	public int getTotalUsers() {
		return users.keySet().size();
	}
}
