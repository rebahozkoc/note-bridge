package ut.twente.notebridge.routes;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


@Path("/persons")
public class PersonRoute {

	//When person wants to create an account for the first time
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPerson(Person person) {
		System.out.println("PersonRoute.createPerson is called");
		try {
			if (Security.checkPasswordValidity(person.getPassword())) {
				BaseUser baseUser = BaseUserDao.INSTANCE.create(person);
				person.setBaseUser(baseUser);
				return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.create(person)).build();
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("Password is not valid").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	//When user wants to view their account
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerson(@PathParam("id") int id) {
		return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.getUser(id)).build();
	}

	//When person wants to make update on their account
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePerson(@PathParam("id") Integer id, Person person) {

		//TODO: PREVENT UNAUTHORIZED UPDATE(USERS SHOULD BE ABLE TO UPDATE ONLY THEIR OWN ACCOUNT)

		Person existingPerson = PersonDao.INSTANCE.getUser(id);
		if(person.getUsername()!=null){
			existingPerson.setUsername(person.getUsername());
		}
		if(person.getEmail()!=null){
			existingPerson.setEmail(person.getEmail());
		}
		if(person.getPhoneNumber()!=null){
			existingPerson.setPhoneNumber(person.getPhoneNumber());
		}
		if(person.getName()!=null){
			existingPerson.setName(person.getName());
		}
		if(person.getLastname()!=null){
			existingPerson.setLastname(person.getLastname());
		}
		if (person.getDescription() != null) {
			existingPerson.setDescription(person.getDescription());
		}


		try{
			return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.update(existingPerson)).build();
		}catch (Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public void deletePerson(@PathParam("id") String id) {
		PersonDao.INSTANCE.delete(id);
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
			Person person = PersonDao.INSTANCE.getUser(id);
			System.out.println("PersonRoute.putImage is called");
			BaseUser baseUser = BaseUserDao.INSTANCE.setProfilePicture(id, uploadedInputStream, fileDetail.getFileName());
			person.setBaseUser(baseUser);
			PersonDao.INSTANCE.update(person);
			return Response.status(Response.Status.OK).entity(person).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("{id}/image")
	public Response getImage(@PathParam("id") Integer id) {
		Person person = PersonDao.INSTANCE.getUser(id);
		String fileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + person.getPicture();
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
