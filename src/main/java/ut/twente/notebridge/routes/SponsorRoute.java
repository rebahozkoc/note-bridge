package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		if (sponsor.getDescription() != null) {
			existingSponsor.setDescription(sponsor.getDescription());
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

	@PUT
	@Path("{id}/image")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putImage(
			@PathParam("id") Integer id,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		try {
			Sponsor sponsor = SponsorDao.INSTANCE.getSponsor(id);
			System.out.println("SponsorRoute.putImage is called");
			BaseUser baseUser = BaseUserDao.INSTANCE.setProfilePicture(id, uploadedInputStream, fileDetail.getFileName());
			sponsor.setBaseUser(baseUser);
			SponsorDao.INSTANCE.update(sponsor);
			return Response.status(Response.Status.OK).entity(sponsor).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("{id}/image")
	public Response getImage(@PathParam("id") Integer id) {
		Sponsor sponsor = SponsorDao.INSTANCE.getSponsor(id);
		String fileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + sponsor.getPicture();
		File file = new File(fileLocation);

		if (!file.exists()) {
			return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();
		}

		try {
			String mimeType = Files.probeContentType(Paths.get(fileLocation));
			Response.ResponseBuilder response = Response.ok(file);
			response.header("Content-Disposition", "inline; filename=" + file.getName());
			response.type(mimeType);
			return response.build();
		} catch (IOException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reading file").build();
		}
	}
}