package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import ut.twente.notebridge.utils.Utils;
import ut.twente.notebridge.model.Post;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public enum PostDao {
	INSTANCE;

	private static final String ORIGINAL_POSTS = Utils.getAbsolutePathToResources() + "/mock-post-dataset.json";
	private static final String UPDATED_POSTS = Utils.getAbsolutePathToResources() + "/updated-mock-post-dataset.json";

	private final HashMap<Integer, Post> posts = new HashMap<>();

	public void delete(int id) {
		if(posts.containsKey(id)) {
			posts.remove(id);
		} else {
			throw new NotFoundException("Post '" + id + "' not found.");
		}
	}

	public List<Post> getPosts(int pageSize, int pageNumber, String sortBy) {
		List<Post> list = new ArrayList<>(posts.values());

		if (sortBy == null || sortBy.isEmpty() || "id".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getId(), pt2.getId()));
		else if ("lastUpDate".equals(sortBy))
			list.sort((pt1, pt2) -> Utils.compare(pt1.getLastUpdate(), pt2.getLastUpdate()));
		else
			throw new NotSupportedException("Sort field not supported");

		return (List<Post>) Utils.pageSlice(list,pageSize,pageNumber);
	}

	public Post getPost(int id) {
		var pt = posts.get(id);

		if (pt == null) {
			throw new NotFoundException("Post '" + id + "' not found!");
		}

		return pt;
	}

	public void load() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File source = existsPosts() ?
				new File(UPDATED_POSTS) :
				new File(ORIGINAL_POSTS);
		Post[] arr = mapper.readValue(source, Post[].class);

		Arrays.stream(arr).forEach(pt -> posts.put(pt.getId(), pt));
	}

	private boolean existsPosts() {
		File f = new File(UPDATED_POSTS);
		return f.exists() && !f.isDirectory();
	}

	public void save() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		File destination = new File(UPDATED_POSTS);

		writer.writeValue(destination, posts.values());
	}

	public Post create(Post newPost) {
		int nextId = getMaxId() + 1;

		newPost.setId(nextId);
		newPost.setCreateDate(Timestamp.valueOf(Instant.now().toString()));
		newPost.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
		posts.put(nextId,newPost);

		return newPost;
	}

	private int getMaxId() {
		Set<Integer> ids = posts.keySet();
		return ids.isEmpty() ? 0 : ids.stream()
				.max(Integer::compareTo)
				.get();
	}

	public Post update(Post updated) {
		if(!updated.isValid())
			throw new BadRequestException("Invalid post.");
		if(posts.get(updated.getId()) == null)
			throw new NotFoundException("Post id '" + updated.getId() + "' not found.");

		updated.setLastUpdate(Timestamp.valueOf(Instant.now().toString()));
		posts.put(updated.getId(),updated);

		return updated;
	}

	public int getTotalPosts() {
		return posts.keySet().size();
	}
}
