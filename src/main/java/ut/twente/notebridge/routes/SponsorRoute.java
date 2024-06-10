package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.Security;

@Path("/sponsors")
public class SponsorRoute {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Sponsor createSponsor(Sponsor sponsor) {
            System.out.println("PersonRoute.createPerson is called");
            try {

                if(Security.checkPasswordValidity(sponsor.getPassword())){
                   throw new BadRequestException("Password is not valid");
                }else{
                    BaseUser baseUser = BaseUserDao.INSTANCE.create(sponsor);
                    sponsor.setBaseUser(baseUser);
                    return SponsorDao.INSTANCE.create(sponsor);
                }
                

            } catch (Exception e) {
                System.out.println("Error while creating sponsor user");
                e.printStackTrace();
                throw new BadRequestException("Error while creating sponsor user");
            }
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
