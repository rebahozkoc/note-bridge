package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.dto.CommentDtoList;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.ResourceCollection;


@Path("/posts")
public class PostRoute {
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Post getPost(@PathParam("id") int id) {

		return PostDao.INSTANCE.getPost(id);
	}

	@GET
	@Path("/{id}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public CommentDtoList getComments(@PathParam("id") int id) {
		return PostDao.INSTANCE.getComments(id);
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Post updatePost(@PathParam("id") String id, Post post) {
		return PostDao.INSTANCE.update(post);
	}

	@DELETE
	@Path("/{id}")
	public void deletePost(@PathParam("id") int id) {
		try{
			PostDao.INSTANCE.delete(id);

		} catch (Exception e) {
			throw new NotFoundException("Post '" + id + "' not found!");
		}

	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceCollection<Post> getPosts(
			@QueryParam("sortBy") String sortBy,
			@QueryParam("pageSize") int pageSize,
			@QueryParam("pageNumber") int pageNumber
	) {
		int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
		int pn = pageNumber > 0 ? pageNumber : 1;
		var resources = PostDao.INSTANCE.getPosts(ps, pn, sortBy).toArray(new Post[0]);
		var total = PostDao.INSTANCE.getTotalPosts();

		return new ResourceCollection<>(resources, ps, pn, total);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Post createPost(Post post) {
		return PostDao.INSTANCE.create(post);
	}
}
