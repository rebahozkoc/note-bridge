package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.Security;

@Path("/sponsors")
public class SponsorRoute {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSponsor(Sponsor sponsor) {
		System.out.println("PersonRoute.createSponsor is called");
		try {
			if (Security.checkPasswordValidity(sponsor.getPassword())) {
				BaseUser baseUser = BaseUserDao.INSTANCE.create(sponsor);
				sponsor.setBaseUser(baseUser);
				return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.create(sponsor)).build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("Password is not valid").build();
			}
		} catch (Exception e) {
			System.out.println("Error while creating sponsor user");
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSponsor(@PathParam("id") int id) {
		return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.getSponsor(id)).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSponsor(@PathParam("id") Integer id, Sponsor sponsor) {

		//TODO: PREVENT UNAUTHORIZED UPDATE(USERS SHOULD BE ABLE TO UPDATE ONLY THEIR OWN ACCOUNT)

		Sponsor existingSponsor = SponsorDao.INSTANCE.getSponsor(id);
		if(sponsor.getUsername()!=null){
			existingSponsor.setUsername(sponsor.getUsername());
		}
		if(sponsor.getEmail()!=null){
			existingSponsor.setEmail(sponsor.getEmail());
		}
		if(sponsor.getPhoneNumber()!=null){
			existingSponsor.setPhoneNumber(sponsor.getPhoneNumber());
		}
		if(sponsor.getCompanyName()!=null){
			existingSponsor.setCompanyName(sponsor.getCompanyName());
		}
		if(sponsor.getWebsiteURL()!=null){
			existingSponsor.setWebsiteURL(sponsor.getWebsiteURL());
		}


		try{
			return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.update(existingSponsor)).build();
		}catch (Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public void deleteSponsor(@PathParam("id") String id) {
		SponsorDao.INSTANCE.delete(id);
	}
}
