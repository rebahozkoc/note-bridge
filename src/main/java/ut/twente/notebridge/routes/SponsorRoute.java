package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.Sponsor;

@Path("/sponsors")
public class SponsorRoute {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Sponsor createSponsor(Sponsor person) {
        return SponsorDao.INSTANCE.create(person);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Sponsor getSponsor(@PathParam("id") int id) {
        return SponsorDao.INSTANCE.getUser(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Sponsor updateSponsor(@PathParam("id") String id, Sponsor person) {
        return SponsorDao.INSTANCE.update(person);
    }

    @DELETE
    @Path("/{id}")
    public void deleteSponsor(@PathParam("id") String id) {
        SponsorDao.INSTANCE.delete(id);
    }
}
