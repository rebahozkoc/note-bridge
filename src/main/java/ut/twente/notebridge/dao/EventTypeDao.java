package ut.twente.notebridge.dao;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ut.twente.notebridge.model.EventType;
import ut.twente.notebridge.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EventTypeDao {
	INSTANCE;


	public List<EventType> getEventTypes() {
		List<EventType> list = new ArrayList<>();
		String sql = """
				SELECT json_agg(t) FROM (
					SELECT name FROM eventtype
					ORDER BY name
					) t;
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {

			ResultSet rs = statement.executeQuery();
			ObjectMapper mapper = JsonMapper.builder()
					.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
					.build();
			if (rs.next()) {
				list = Arrays.asList(mapper.readValue(rs.getString("json_agg"), EventType[].class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get posts.");
		}
		return list;
	}
}
