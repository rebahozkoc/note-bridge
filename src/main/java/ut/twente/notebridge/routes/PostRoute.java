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
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.CommentDao;
import ut.twente.notebridge.dao.InterestDao;
import ut.twente.notebridge.dao.LikeDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.dto.CommentDtoList;
import ut.twente.notebridge.dto.PostDto;
import ut.twente.notebridge.model.Interest;
import ut.twente.notebridge.model.Like;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.ResourceCollection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/posts")
public class PostRoute {
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPost(@PathParam("id") int id) {
		try{
			return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.getPost(id)).build();
		}catch (Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}


	@GET
	@Path("/sponsored")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSponsoredPosts(){
		try{
			return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.getSponsoredPosts()).build();
		}catch(Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts(
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("pageNumber") Integer pageNumber,
			@QueryParam("sortBy") String sortBy,
			@QueryParam("personId") Integer personId,
			@QueryParam("search") String search,
			@QueryParam("filterBy") String filterBy
	) {
		try {
			int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
			int pn = pageNumber > 0 ? pageNumber : 1;

			StringBuilder query = new StringBuilder();


			PostDto[] resources = PostDao.INSTANCE.getPosts(ps, pn, sortBy, personId, search, filterBy, query).toArray(new PostDto[0]);

			//Changing json_agg(t) to COUNT in order to get total posts query
			String s = "json_agg(t)";
			String r = "COUNT(t)";
			int start = query.indexOf(s);
			while (start != -1) {
				int end = start + s.length();
				query.replace(start, end, r);
				start = query.indexOf(s, start + r.length());
			}

			int total = PostDao.INSTANCE.getTotalPosts(query.toString());


			return Response.ok().entity(new ResourceCollection<>(resources, ps, pn, total)).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

	}

	// Returns the like count for a post with a given id
	@GET
	@Path("/{id}/likes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLikes(@PathParam("id") int id) {
		Map<String, Integer> responseObj = new HashMap<>();

		try {
			int totalLikes = LikeDao.INSTANCE.getTotalLikes(id);
			responseObj.put("totalLikes", totalLikes);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while getting total likes").build();
		}

	}


	@GET
	@Path("/{id}/interests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInterests(@PathParam("id") int id) {
		Map<String, Integer> responseObj = new HashMap<>();

		try {
			int totalInterests = InterestDao.INSTANCE.getTotalInterest(id);
			responseObj.put("totalInterests", totalInterests);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while getting total likes").build();
		}

	}

	//Returns if the user liked the post with a given id
	@GET
	@Path("/{id}/like")
	@Produces(MediaType.APPLICATION_JSON)
	public Response didUserLike(@PathParam("id") int id, @Context HttpServletRequest request) {
		Map<String, Boolean> responseObj = new HashMap<>();
		// In case user is not authenticated, return unauthorized
		if (request.getSession(false) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
		}
		int userId = (int) request.getSession().getAttribute("userId");
		try {
			// Check if user liked the post
			Boolean isLiked = LikeDao.INSTANCE.isLiked(id, userId);
			// Create the return Object & return as JSON
			responseObj.put("isLiked", isLiked);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();
		} catch (Exception e) {
			// In case there is an exception while checking if the user liked or not, return internal server error
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while checking if user liked the post").build();
		}
	}

	@GET
	@Path("/{id}/interested")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isUserInterested(@PathParam("id") int id, @Context HttpServletRequest request) {
		Map<String, Boolean> responseObj = new HashMap<>();
		// In case user is not authenticated, return unauthorized
		if (request.getSession(false) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
		}
		int userId = (int) request.getSession().getAttribute("userId");
		try {
			// Check if user liked the post
			Boolean isInterested = InterestDao.INSTANCE.isInterested(id, userId);
			// Create the return Object & return as JSON
			responseObj.put("isInterested", isInterested);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();
		} catch (Exception e) {
			// In case there is an exception while checking if the user liked or not, return internal server error
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while checking if user liked the post").build();
		}
	}


	@POST
	@Path("/{id}/likes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response likePost(@PathParam("id") int postId, @Context HttpServletRequest request) {
		HttpSession userSession = request.getSession(false);
		if (userSession == null) {
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
			}
		}
		int userId = (int) userSession.getAttribute("userId");

		boolean isPerson;
		try {
			isPerson = BaseUserDao.INSTANCE.isPerson(userId);
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while checking if user is a person").build();
		}
		if (isPerson) {
			Like like = new Like();
			like.setPostId(postId);
			like.setPersonId(userId);
			try {
				return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.toggleLike(like)).build();
			} catch (Exception e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while liking the post").build();
			}

		} else {
			return Response.status(Response.Status.FORBIDDEN).entity("Only persons can like a post").build();
		}
	}

	@POST
	@Path("/{id}/interested")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response interestedInPost(@PathParam("id") int postId, @Context HttpServletRequest request) {
		HttpSession userSession = request.getSession(false);
		if (userSession == null) {
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
			}
		}
		int userId = (int) userSession.getAttribute("userId");

		boolean isPerson;
		try {
			isPerson = BaseUserDao.INSTANCE.isPerson(userId);
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while checking if user is a person").build();
		}
		if (isPerson) {
			Interest interest = new Interest();
			interest.setPostId(postId);
			interest.setPersonId(userId);
			try {
				return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.toggleInterest(interest)).build();
			} catch (Exception e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while liking the post").build();
			}

		} else {
			return Response.status(Response.Status.FORBIDDEN).entity("Only persons can like a post").build();
		}
	}

	@GET
	@Path("/{id}/interestedusers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInterestedUsernames(@PathParam("id") int id) {
		try {
			String usernameListJson = InterestDao.INSTANCE.getInterestedUsernames(id);
			return Response.status(Response.Status.OK).entity(usernameListJson).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

		}
	}

	@GET
	@Path("/{id}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public CommentDtoList getComments(@PathParam("id") int id) {
		return CommentDao.INSTANCE.getComments(id);
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePost(@PathParam("id") Integer id, Post post, @Context HttpServletRequest request) {
		post.setId(id);
		HttpSession userSession = request.getSession(false);
		if (!Security.isAuthorized(userSession, "person") || (int) userSession.getAttribute("userId") != post.getPersonId()) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
		}
		post.setId(id);
		try {
			return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.update(post)).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deletePost(@PathParam("id") int id, @Context HttpServletRequest request) {
		HttpSession userSession = request.getSession(false);
		Post post = PostDao.INSTANCE.getPost(id);
		if (!Security.isAuthorized(userSession, "person") || (int) userSession.getAttribute("userId") != post.getPersonId()) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
		}
		try {
			PostDao.INSTANCE.delete(id);
			return Response.status(Response.Status.OK).entity("Post deleted").build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPost(Post post, @Context HttpServletRequest request) {
		HttpSession userSession = request.getSession(false);
		if (!Security.isAuthorized(userSession, "person") || (int) userSession.getAttribute("userId") != post.getPersonId()) {
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
			}
		}

		try {
			return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.create(post)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
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

		for (String imageFile : imageFiles) {
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
		} else {
			int personIdSession = (int) session.getAttribute("userId");
			if (personIdSession != personId) {
				return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to display others' posts.").build();
			} else {
				try {
					return Response.status(Response.Status.OK).entity(PostDao.INSTANCE.getPostsByPersonId(personId)).build();
				} catch (Exception e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server failed to retreive posts.").build();
				}

			}
		}
	}

}
