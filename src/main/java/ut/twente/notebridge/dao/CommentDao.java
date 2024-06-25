package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import ut.twente.notebridge.dto.CommentDtoList;
import ut.twente.notebridge.model.Comment;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;

import java.sql.*;
import java.time.Instant;

public enum CommentDao {

	INSTANCE;


	public Comment create(Comment comment) {
		String sql = """
				INSERT INTO comment (createdate,lastupdate,content,postid,personid)
				VALUES (?,?,?,?,?);
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			Timestamp currentTime = Timestamp.from(Instant.now());
			comment.setCreateDate(currentTime);
			comment.setLastUpdate(currentTime);
			statement.setTimestamp(1, currentTime);
			statement.setTimestamp(2, currentTime);
			statement.setString(3, Security.sanitizeInput(comment.getContent()));
			statement.setInt(4, comment.getPostId());
			statement.setInt(5, comment.getPersonId());

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating comment failed, no rows affected.");
			}
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					comment.setId(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Creating comment failed, no ID obtained.");
				}
			}

			return comment;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Creating comment failed, no ID obtained.");
		}
	}

	public CommentDtoList getComments(int id) {
		String sql = """
				SELECT jsonb_build_object(
				           'comments', jsonb_agg(
				                          jsonb_build_object(
				                      				'id', c.id, 'personId', b.id, 'username', b.username, 'picture', b.picture,
				                              'content', c.content, 'createDate', c.createdate
				                          ) ORDER BY c.createdate DESC)
				       )
				FROM BaseUser b, Comment c
				WHERE b.id = c.personid AND c.postid =?
				GROUP BY c.postid;
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				String json = rs.getString("jsonb_build_object");
				ObjectMapper mapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
				return mapper.readValue(json, CommentDtoList.class);
			} else {
				throw new NotFoundException("No comments found for post with id " + id);
			}
		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while getting comments");
		}
	}

	public void delete(int id) {
		String sql = "DELETE FROM comment WHERE id=?"; // Assuming delete_post takes one parameter

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, id);
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new NotFoundException("Comment with id " + id + " not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not delete comment.");
		}
	}
}
