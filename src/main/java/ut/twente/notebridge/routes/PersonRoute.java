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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


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

	@POST
	@Path("/image")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		//Your local disk path where you want to store the file
		System.out.println("PersonRoute.uploadFile is called");
		String uploadedFileLocation = "D:/uploadedFiles/" + fileDetail.getFileName();
		System.out.println(uploadedFileLocation);
		// save it
		File  objFile=new File(uploadedFileLocation);
		if(objFile.exists())
		{
			objFile.delete();

		}

		saveToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

	}
	private void saveToFile(InputStream uploadedInputStream,
	                        String uploadedFileLocation) {

		try {
			OutputStream out = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
