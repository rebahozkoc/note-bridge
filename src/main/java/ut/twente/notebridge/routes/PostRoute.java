package ut.twente.notebridge.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import ut.twente.notebridge.dao.LikeDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.dto.CommentDtoList;
import ut.twente.notebridge.model.Like;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.ResourceCollection;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/posts")
public class PostRoute {
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Post getPost(@PathParam("id") int id) {

		return PostDao.INSTANCE.getPost(id);
	}

	// Returns the like count for a post with a given id
	@GET
	@Path("/{id}/likes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLikes(@PathParam("id") int id) {
		Map<String,Integer> responseObj = new HashMap<>();

		try{
			int totalLikes = LikeDao.INSTANCE.getTotalLikes(id);
			responseObj.put("totalLikes", totalLikes);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();

		}catch (Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while getting total likes").build();
		}

	}

	//Returns if the user liked the post with a given id
	@GET
	@Path("/{id}/like")
	@Produces(MediaType.APPLICATION_JSON)
	public Response didUserLike(@PathParam("id") int id, @Context HttpServletRequest request){
		Map<String,String> responseObj = new HashMap<>();
		// In case user is not authenticated, return unauthorized
		if(request.getSession(false)==null){
			return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
		}
		int userId=(int) request.getSession().getAttribute("userId");
		try{
			// Check if user liked the post
			Boolean isLiked = LikeDao.INSTANCE.isLiked(id,userId);
			// Create the return Object & return as JSON
			responseObj.put("isLiked", isLiked.toString());
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();
		}catch (Exception e){
			// In case there is an exception while checking if the user liked or not, return internal server error
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while checking if user liked the post").build();
		}
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
	public Post updatePost(@PathParam("id") Integer id, Post post) {
		post.setId(id);
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

	@POST
	@Path("{id}/images")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createImages(
			@PathParam("id") Integer id,
			FormDataMultiPart multiPart) {

		try {
			List<FormDataBodyPart> parts = multiPart.getFields("images");
			Post post = PostDao.INSTANCE.getPost(id);
			PostDao.INSTANCE.createImages(post, parts);

			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("{id}/image")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImage(@PathParam("id") Integer id) {
		System.out.println("PostRoute.getImage is called");

		List<String> imageFiles = PostDao.INSTANCE.getImages(id);
		List<String> imageList = new java.util.ArrayList<>();

		for (String imageFile: imageFiles){
			String fileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + imageFile;
			File file = new File(fileLocation);
			if (file.exists()) {
				try {
					byte[] fileContent = FileUtils.readFileToByteArray(file);
					String encodedString = Base64.getEncoder().encodeToString(fileContent);
					imageList.add(encodedString);
				} catch (IOException e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while encoding image to base64").build();
				}
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Image file not found at " + fileLocation).build();
			}


		}
		return Response.ok(imageList).build();

	}


	@POST
	@Path("/{id}/likes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Like likePost(@PathParam("id") int postId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		int personId = (int) session.getAttribute("userId");

		Like like= new Like();
		like.setPostId(postId);
		like.setPersonId(personId);
		return PostDao.INSTANCE.beingLiked(like);
	}

	//This route is defined to get all posts of a user, when user is logged in
	//and desires to display all their posts.
	@GET
	@Path("/person/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPostsByPerson(@PathParam("personId") int personId,
									 @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
		}else{
			int personIdSession = (int) session.getAttribute("userId");
			if(personIdSession!=personId){
				return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to display others' posts.").build();
			}else{
				try{
					return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.getPostsByPersonId(personId)).build();
				}catch (Exception e){
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server failed to retreive posts.").build();
				}

			}
		}
	}

}
