package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.Utils;
import ut.twente.notebridge.model.User;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public enum UserDao {
	INSTANCE;

	private static final String ORIGINAL_USERS = Utils.getAbsolutePathToResources() + "/mock-user-dataset.json";
	private static final String UPDATED_USERS = Utils.getAbsolutePathToResources() + "/updated-mock-user-dataset.json";

	private final HashMap<String, User> users = new HashMap<>();

	public void delete(String id) {
		if(users.containsKey(id)) {
			users.remove(id);
		} else {
			throw new NotFoundException("User '" + id + "' not found.");
		}
	}

	public List<User> getUsers(int pageSize, int pageNumber, String sortBy) {
		List<User> list = new ArrayList<>(users.values());

		if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(Integer.parseInt(pt1.getId()), Integer.parseInt(pt2.getId())));
		else if ("lastUpDate".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpDate(), pt2.getLastUpDate()));
		else
			throw new NotSupportedException("Sort field not supported");

		return (List<User>) Utils.pageSlice(list,pageSize,pageNumber);
	}

	public User getUser(String id) {
		var pt = users.get(id);

		if (pt == null) {
			throw new NotFoundException("User '" + id + "' not found!");
		}

		return pt;
	}

	public void load() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File source = existsUsers() ?
				new File(UPDATED_USERS) :
				new File(ORIGINAL_USERS);
		User[] arr = mapper.readValue(source, User[].class);

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

	public User create(User newUser) {
		String nextId = "" + (getMaxId() + 1);

		newUser.setId(nextId);
		newUser.setCreateDate(Instant.now().toString());
		newUser.setLastUpDate(Instant.now().toString());
		users.put(nextId,newUser);

		return newUser;
	}

	private int getMaxId() {
		Set<String> ids = users.keySet();
		return ids.isEmpty() ? 0 : ids.stream()
				.map(Integer::parseInt)
				.max(Integer::compareTo)
				.get();
	}

	public User update(User updated) {
		if(!updated.isValid())
			throw new BadRequestException("Invalid user.");
		if(users.get(updated.getId()) == null)
			throw new NotFoundException("User id '" + updated.getId() + "' not found.");

		updated.setLastUpDate(Instant.now().toString());
		users.put(updated.getId(),updated);

		return updated;
	}

	public int getTotalUsers() {
		return users.keySet().size();
	}
}
