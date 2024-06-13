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
import java.io.InputStream;


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
	public Person updatePerson(@PathParam("id") Integer id, Person person) {
		person.setId(id);
		return PersonDao.INSTANCE.update(person);
	}

	@DELETE
	@Path("/{id}")
	public void deletePerson(@PathParam("id") String id) {
		PersonDao.INSTANCE.delete(id);
	}

	@POST
	@Path("{id}/image")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(
			@PathParam("id") Integer id,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		try {
			// TODO: Convert this to PUT method
			//BaseUser baseUser = BaseUserDao.INSTANCE.getUser(id);
			Person person = PersonDao.INSTANCE.getUser(id);
			System.out.println("PersonRoute.uploadFile is called");
			//Your local disk path where you want to store the file
			String uuid = java.util.UUID.randomUUID().toString();
			String uploadedFileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + uuid + fileDetail.getFileName();
			System.out.println(uploadedFileLocation);
			// save it
			File objFile = new File(uploadedFileLocation);
			if (objFile.exists()) {
				boolean res = objFile.delete();
			}

			Utils.saveToFile(uploadedInputStream, uploadedFileLocation);
			person.setPicture(uuid + fileDetail.getFileName());
			PersonDao.INSTANCE.update(person);
			return Response.status(Response.Status.OK).entity(person).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}


}
