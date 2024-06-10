package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public enum SponsorDao {
	INSTANCE;
	private static final String ORIGINAL_SPONSORS = Utils.getAbsolutePathToResources() + "/mock-sponsor-dataset.json";
	private static final String UPDATED_SPONSORS = Utils.getAbsolutePathToResources() + "/updated-mock-sponsor-dataset.json";

	private final HashMap<Integer, Sponsor> sponsors = new HashMap<>();

	public void delete(String id) {
		if (sponsors.containsKey(id)) {
			sponsors.remove(id);
		} else {
			throw new NotFoundException("Person '" + id + "' not found.");
		}
	}

	public List<Sponsor> getUsers(int pageSize, int pageNumber, String sortBy) {
		List<Sponsor> list = new ArrayList<>(sponsors.values());

		if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getId(), pt2.getId()));
		else if ("lastUpDate".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpdate(), pt2.getLastUpdate()));
		else throw new NotSupportedException("Sort field not supported");

		return (List<Sponsor>) Utils.pageSlice(list, pageSize, pageNumber);
	}

	public Sponsor getUser(int id) {
		var pt = sponsors.get(id);

		if (pt == null) {
			throw new NotFoundException("Person '" + id + "' not found!");
		}

		return pt;
	}

	public void load() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File source = existsUsers() ? new File(UPDATED_SPONSORS) : new File(ORIGINAL_SPONSORS);
		Sponsor[] arr = mapper.readValue(source, Sponsor[].class);

		Arrays.stream(arr).forEach(pt -> sponsors.put(pt.getId(), pt));
	}

	private boolean existsUsers() {
		File f = new File(UPDATED_SPONSORS);
		return f.exists() && !f.isDirectory();
	}

	public void save() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		File destination = new File(UPDATED_SPONSORS);

		writer.writeValue(destination, sponsors.values());
	}

	public Sponsor create(Sponsor newSponsor) {
		String sql = """
						INSERT INTO Sponsor (id,
						companyname, websiteurl)
						VALUES (?, ?, ?);
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, newSponsor.getId());
			if (newSponsor.getCompanyName() == null) {
				statement.setNull(2, java.sql.Types.VARCHAR);
			} else {
				statement.setString(2, newSponsor.getCompanyName());
			}
			if (newSponsor.getWebsiteURL() == null) {
				statement.setNull(3, java.sql.Types.VARCHAR);
			} else {
				statement.setString(3, newSponsor.getWebsiteURL());
			}

			statement.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return newSponsor;
	}

	private int getMaxId() {
		Set<Integer> ids = sponsors.keySet();
		return ids.isEmpty() ? 0 : ids.stream().max(Integer::compareTo).get();
	}

	public Sponsor update(Sponsor updated) {
		if (!updated.isValid()) throw new BadRequestException("Invalid user.");
		if (sponsors.get(updated.getId()) == null)
			throw new NotFoundException("Person id '" + updated.getId() + "' not found.");

		updated.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
		sponsors.put(updated.getId(), updated);

		return updated;
	}

	public int getTotalUsers() {
		return sponsors.keySet().size();
	}
}
