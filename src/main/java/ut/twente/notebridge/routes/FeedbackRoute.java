package ut.twente.notebridge.routes;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.FeedbackDao;
import ut.twente.notebridge.model.Feedback;

/**
 * This class is used to handle the feedback routes.
 * It is used for "Contact Us" section in the app.
 */
@Path("/feedback")
public class FeedbackRoute {

    /**
     * This method is used to receive feedback.
     *
     * @param feedback The feedback to be sent
     * @return The response with the feedback
     */
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response send(Feedback feedback) {
        System.out.println("FeedbackRoute.send is called");
        try {
            return Response.status(Response.Status.OK).entity(FeedbackDao.INSTANCE.create(feedback)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}
