package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import ut.twente.notebridge.dto.CommentDtoList;
import ut.twente.notebridge.model.Like;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;
import ut.twente.notebridge.model.Post;

import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public enum PostDao {
	INSTANCE;

	private final HashMap<Integer, Post> posts = new HashMap<>();

	public void delete(int id) {
		String sql = """
				DELETE FROM post WHERE id=?
				
		"""; // Assuming delete_post takes one parameter

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not delete post.");
		}
	}

	public List<Post> getPosts(int pageSize, int pageNumber, String sortBy) {
		List<Post> list = new ArrayList<>(posts.values());
		System.out.println("GET posts called");
		try {
			Statement statement = DatabaseConnection.INSTANCE.getConnection().createStatement();
			String sql = """
					SELECT json_agg(post) FROM post
					""";

			ResultSet rs = statement.executeQuery(sql);
			ObjectMapper mapper = JsonMapper.builder()
					.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
					.build();
			if (rs.next()) {
				list = Arrays.asList(mapper.readValue(rs.getString("json_agg"), Post[].class));
			}
		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get posts.");
		}

		/*
		if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getId(), pt2.getId()));
		else if ("lastUpDate".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpdate(), pt2.getLastUpdate()));
		else throw new NotSupportedException("Sort field not supported");
		*/
		return (List<Post>) Utils.pageSlice(list, pageSize, pageNumber);
	}

	public Post getPost(int id) {

		String sql = "SELECT row_to_json(t) post FROM(SELECT * FROM Post WHERE id=?) t"; // Assuming delete_post takes one parameter

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				String json = rs.getString("post");

				ObjectMapper mapper = JsonMapper.builder()
						.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
						.build();
				return mapper.readValue(json, Post.class);

			} else {
				//no rows returned, post with that id does not exist
				throw new NotFoundException("Post '" + id + "' not found.");
			}

		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while getting post.");
		}
	}
	public CommentDtoList getComments(int id) {
		String sql= """
				SELECT jsonb_build_object(
				           'comments', jsonb_agg(
				                          jsonb_build_object(
				                              'personId', b.id, 'username', b.username, 'picture', b.picture,
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
				ObjectMapper mapper = JsonMapper.builder()
						.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
						.build();
				return mapper.readValue(json, CommentDtoList.class);
			} else {
				throw new NotFoundException("No comments found for post with id " + id);
			}
		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while getting comments");
		}
	}

	public Like beingLiked (Like like){
		String sql = """
				INSERT INTO personlikespost (personid, postid) VALUES (?, ?)
				""";
		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, like.getPersonId());
			statement.setInt(2, like.getPostId());
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 1) {
				return like;
			} else {
				throw new BadRequestException("Like failed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while liking post");
		}
	}

	public Post create(Post newPost) {
		String sql = """
						INSERT INTO Post (createDate,
						lastUpdate, personId,
						title, description,
						sponsoredBy, sponsoredFrom,
						sponsoredUntil, eventType, location)
						VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
				""";


		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			Timestamp currentTime = Timestamp.from(Instant.now());
			newPost.setCreateDate(currentTime);
			newPost.setLastUpdate(currentTime);
			statement.setTimestamp(1, currentTime);
			statement.setTimestamp(2, currentTime);
			statement.setInt(3, newPost.getPersonId());
			statement.setString(4, newPost.getTitle());
			if (newPost.getDescription() == null) {
				statement.setNull(5, java.sql.Types.VARCHAR);
			} else {
				statement.setString(5, newPost.getDescription());
			}
			if (newPost.getSponsoredBy() == null) {
				statement.setNull(6, java.sql.Types.INTEGER);
			} else {
				statement.setInt(6, newPost.getSponsoredBy());
			}
			if (newPost.getSponsoredFrom() == null) {
				statement.setNull(7, java.sql.Types.TIMESTAMP);
			} else {
				statement.setTimestamp(7, newPost.getSponsoredFrom());
			}
			if (newPost.getSponsoredUntil() == null) {
				statement.setNull(8, java.sql.Types.TIMESTAMP);
			} else {
				statement.setTimestamp(8, newPost.getSponsoredUntil());
			}
			statement.setString(9, newPost.getEventType());
			if (newPost.getLocation() == null) {
				statement.setNull(10, java.sql.Types.VARCHAR);
			} else {
				statement.setString(10, newPost.getLocation());
			}

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating post failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newPost.setId(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Creating post failed, no ID obtained.");
				}
			}
			return newPost;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Creating post failed, no ID obtained.");
		}
	}

	public List<Post> getPostsByPersonId(int personId) {
		String sql = """
				SELECT json_agg(post) FROM post WHERE personId=?
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setInt(1, personId);
			ResultSet rs = statement.executeQuery();
			ObjectMapper mapper = JsonMapper.builder()
					.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
					.build();
			if (rs.next()) {
				return Arrays.asList(mapper.readValue(rs.getString("json_agg"), Post[].class));
			} else {
				throw new NotFoundException("No posts found for person with id " + personId);
			}
		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while getting posts");
		}
	}

	private int getMaxId() {
		// TODO delete this method if not used

		Set<Integer> ids = posts.keySet();
		return ids.isEmpty() ? 0 : ids.stream().max(Integer::compareTo).get();
	}

	public Post update(Post updated) {
		// TODO delete this method if not used

		if (!updated.isValid()) throw new BadRequestException("Invalid post.");
		if (posts.get(updated.getId()) == null)
			throw new NotFoundException("Post id '" + updated.getId() + "' not found.");

		updated.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
		posts.put(updated.getId(), updated);

		return updated;
	}

	public int getTotalPosts() {
		// TODO delete this method if not used

		return posts.keySet().size();
	}

	public void createImages(Post post, List<FormDataBodyPart> parts) {
		for (FormDataBodyPart part : parts) {
			InputStream uploadedInputStream = part.getValueAs(InputStream.class);
			String fileName = part.getFormDataContentDisposition().getFileName();
			String uuid = java.util.UUID.randomUUID().toString();
			String uploadedFileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + uuid + fileName;
			System.out.println(uploadedFileLocation);
			// save it
			File objFile = new File(uploadedFileLocation);
			if (objFile.exists()) {
				boolean res = objFile.delete();
			}

			Utils.saveToFile(uploadedInputStream, uploadedFileLocation);

			String sql = """
					INSERT INTO picture (postid, pictureurl) VALUES (?, ?)
					""";
			try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
				statement.setInt(1, post.getId());
				statement.setString(2, uuid + fileName);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not create image.");
			}
		}
	}
}
