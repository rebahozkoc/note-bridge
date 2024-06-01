package ut.twente.notebridge.routes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.ResourceCollection;

@Path("/posts")
public class PostRoute {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResourceCollection<Post> getPosts(
			@QueryParam("sortBy") String sortBy,
			@QueryParam("pageSize") int pageSize,
			@QueryParam("pageNumber") int pageNumber
	){
		int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
		int pn = pageNumber > 0 ? pageNumber : 1;
		var resources = PostDao.INSTANCE.getPosts(ps, pn, sortBy).toArray(new Post[0]);
		var total = PostDao.INSTANCE.getTotalPosts();

		return new ResourceCollection<>(resources, ps, pn, total);
	}
}
