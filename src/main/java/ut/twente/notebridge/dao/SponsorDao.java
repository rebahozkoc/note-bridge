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

	private final HashMap<Integer, Sponsor> sponsors = new HashMap<>();

	public void delete(String id) {
		// TODO implement delete
		if (sponsors.containsKey(id)) {
			sponsors.remove(id);
		} else {
			throw new NotFoundException("Person '" + id + "' not found.");
		}
	}

	public List<Sponsor> getUsers(int pageSize, int pageNumber, String sortBy) {
		// TODO implement getUsers or delete
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
			e.printStackTrace();
			throw new RuntimeException("Error while creating sponsor user");
		}

		return newSponsor;
	}

	public Sponsor update(Sponsor updated) {
		if (!updated.isValid()) throw new BadRequestException("Invalid user.");
		if (sponsors.get(updated.getId()) == null)
			throw new NotFoundException("Person id '" + updated.getId() + "' not found.");

		updated.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
		sponsors.put(updated.getId(), updated);

		return updated;
	}
}
