package ut.twente.notebridge.routes;


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.dao.SponsorDao;
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
			System.out.println("Error while creating user");
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	//When user wants to view their account
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPerson(@PathParam("id") int id) {
		return PersonDao.INSTANCE.getUser(id);
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
}
