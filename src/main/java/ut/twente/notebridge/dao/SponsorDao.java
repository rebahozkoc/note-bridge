package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public enum SponsorDao {
	INSTANCE;

	private final HashMap<Integer, Sponsor> sponsors = new HashMap<>();

	public void delete(int id) {
		// TODO implement delete

	}

	public List<Sponsor> getSponsors(int pageSize, int pageNumber, String sortBy) {
		// TODO implement getUsers or delete
		List<Sponsor> list = new ArrayList<>(sponsors.values());

		if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getId(), pt2.getId()));
		else if ("lastUpDate".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpdate(), pt2.getLastUpdate()));
		else throw new NotSupportedException("Sort field not supported");

		return (List<Sponsor>) Utils.pageSlice(list, pageSize, pageNumber);
	}

	public Sponsor getSponsor(int id) {
		String sql = "SELECT row_to_json(t) sponsor FROM(SELECT * FROM sponsor JOIN baseUser ON sponsor.id = baseUser.id WHERE sponsor.id=?) t"; // Assuming delete_post takes one parameter

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				String json = rs.getString("sponsor");

				ObjectMapper mapper = JsonMapper.builder()
						.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
						.build();
				Sponsor sponsor = mapper.readValue(json, Sponsor.class);
				sponsor.setPassword("hidden");
				return sponsor;

			} else {
				//no rows returned, post with that id does not exist
				throw new NotFoundException();
			}

		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while getting sponsor user");
		}
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
				statement.setString(2, Security.sanitizeInput(newSponsor.getCompanyName()));
			}
			if (newSponsor.getWebsiteURL() == null) {
				statement.setNull(3, java.sql.Types.VARCHAR);
			} else {
				statement.setString(3, Security.sanitizeInput(newSponsor.getWebsiteURL()));
			}

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while creating sponsor user");
		}

		return newSponsor;
	}

	public Sponsor update(Sponsor updated) {
		// TODO: add authentication layer
		BaseUserDao.INSTANCE.update(updated);
		String sql = """
						UPDATE Sponsor
						SET companyname = ?,
						websiteurl = ?
						WHERE id = ?;
				""";
		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setString(1, Security.sanitizeInput(updated.getCompanyName()));
			statement.setString(2, Security.sanitizeInput(updated.getWebsiteURL()));
			statement.setInt(3, updated.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while updating person user");
		}

		return updated;
	}
}
