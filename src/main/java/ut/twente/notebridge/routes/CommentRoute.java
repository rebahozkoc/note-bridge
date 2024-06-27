package ut.twente.notebridge.routes;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.CommentDao;
import ut.twente.notebridge.model.Comment;
import ut.twente.notebridge.utils.Security;

/**
 * This class is used to handle the comment routes.
 */
@Path("/comments")
public class CommentRoute {

    /**
     * This method is used to create a comment.
     *
     * @param comment The comment to be created
     * @param request The request
     * @return The response with the created comment
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComment(Comment comment, @Context HttpServletRequest request) {
        HttpSession userSession = request.getSession(false);
        if (!Security.isAuthorized(userSession, "person") || (int) userSession.getAttribute("userId") != comment.getPersonId()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
        }
        try {
            return Response.status(Response.Status.OK).entity(CommentDao.INSTANCE.create(comment)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to delete a comment.
     *
     * @param id      The id of the comment to be deleted
     * @param request The request
     * @return The response with the result
     */
    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("id") int id, @Context HttpServletRequest request) {
        HttpSession userSession = request.getSession(false);
        if (!Security.isAuthorized(userSession, "person")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
        }
        try {
            CommentDao.INSTANCE.delete(id);
            return Response.status(Response.Status.OK).entity("Deleted comment").build();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof NotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
