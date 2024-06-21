package ut.twente.notebridge.routes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.EventTypeDao;

@Path("/eventtypes")
public class EventTypeRoute {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts() {
		try {
			return Response.ok().entity(EventTypeDao.INSTANCE.getEventTypes()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
