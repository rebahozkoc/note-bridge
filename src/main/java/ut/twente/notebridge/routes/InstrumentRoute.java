package ut.twente.notebridge.routes;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.InstrumentDao;
import ut.twente.notebridge.model.PersonInstrument;
import ut.twente.notebridge.utils.Security;

import java.util.List;

@Path("/instruments")
public class InstrumentRoute {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInstruments() {
		try {
			String json = InstrumentDao.INSTANCE.getInstruments();
			return Response.status(Response.Status.OK).entity(json).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

		}
	}

	@GET
	@Path("/persons/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInstrumentsOfPerson(@PathParam("id") int personId) {
		try {
			List<PersonInstrument> instruments = InstrumentDao.INSTANCE.getPersonInstruments(personId);

			return Response.status(Response.Status.OK).entity(instruments).build();
		} catch (NotFoundException e) {

			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

		}
	}

	@POST
	@Path("/persons")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPersonInstrument(PersonInstrument instrument, @Context HttpServletRequest request) {
		HttpSession userSession = request.getSession(false);
		if (!Security.isAuthorized(userSession, "person") || (int) userSession.getAttribute("userId") != instrument.getPersonId()) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
		}
		try {
			PersonInstrument personInstrument = InstrumentDao.INSTANCE.addPersonInstrument(instrument);
			return Response.status(Response.Status.OK).entity(personInstrument).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/persons/{name}")
	public Response deletePersonInstrument(@PathParam("name") String instrumentName, @Context HttpServletRequest request) {
		HttpSession userSession = request.getSession(false);
		if (!Security.isAuthorized(userSession, "person")) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
		}
		try {
			PersonInstrument instrument = new PersonInstrument();
			instrument.setInstrumentName(instrumentName);
			instrument.setPersonId((int) userSession.getAttribute("userId"));
			InstrumentDao.INSTANCE.deletePersonInstrument(instrument);
			return Response.status(Response.Status.OK).entity("Deleted").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
