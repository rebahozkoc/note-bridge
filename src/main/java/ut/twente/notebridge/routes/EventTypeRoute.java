package ut.twente.notebridge.routes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.EventTypeDao;

/**
 * This class is used to handle the event type routes.
 */
@Path("/eventtypes")
public class EventTypeRoute {

    /**
     * This method is used to get all event types.
     *
     * @return The response with the event types
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventTypes() {
        try {
            return Response.ok().entity(EventTypeDao.INSTANCE.getEventTypes()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
