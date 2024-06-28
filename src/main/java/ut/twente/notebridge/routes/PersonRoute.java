package ut.twente.notebridge.routes;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.LikeDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to handle person-related routes.
 * CRUD operations for persons are defined here.
 * Necessary authorization checks using user session is also implemented.
 */
@Path("/persons")
public class PersonRoute {

    //When person wants to create an account for the first time

    /**
     * This method is used to create a person.
     *
     * @param person The person to be created
     * @return The response with the created person
     */
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

    /**
     * This method is used to get a person.
     *
     * @param id The id of the person requested.
     * @return The response with the person
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerson(@PathParam("id") int id) {
        return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.getUser(id)).build();
    }

    //When person wants to make update on their account

    /**
     * This method is used to update a person.
     *
     * @param id     The id of the person to be updated
     * @param person The person to be updated
     * @return The response with the updated person
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("id") Integer id, Person person, @Context HttpServletRequest request) {

        HttpSession userSession = request.getSession(false);
        if (userSession == null || (int) userSession.getAttribute("userId") != id) {
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
            }
        }

        Person existingPerson = PersonDao.INSTANCE.getUser(id);
        if (person.getUsername() != null) {
            existingPerson.setUsername(person.getUsername());
        }
        if (person.getEmail() != null) {
            existingPerson.setEmail(person.getEmail());
        }
        if (person.getPhoneNumber() != null) {
            existingPerson.setPhoneNumber(person.getPhoneNumber());
        }
        if (person.getName() != null) {
            existingPerson.setName(person.getName());
        }
        if (person.getLastname() != null) {
            existingPerson.setLastname(person.getLastname());
        }
        if (person.getDescription() != null) {
            existingPerson.setDescription(person.getDescription());
        }


        try {
            return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.update(existingPerson)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to delete a person.
     *
     * @param id The id of the person to be deleted
     * @return The response with the result in text format.
     */
    @DELETE
    @Path("/{id}")
    public Response deletePerson(@PathParam("id") int id, @Context HttpServletRequest request) {

        HttpSession userSession = request.getSession(false);
        if (userSession == null || (int) userSession.getAttribute("userId") != id) {
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
            }
        }
        try {
            PersonDao.INSTANCE.delete(id);
            BaseUserDao.INSTANCE.delete(id);
            userSession.invalidate();
            return Response.status(Response.Status.OK).entity("Person with id " + id + " is deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to upload an image for a Person for their profile picture.
     * The image is stored in the filesystem and the path is stored in the database.
     *
     * @return The response with the updated person
     */
    @PUT
    @Path("{id}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putImage(
            @PathParam("id") Integer id,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail, @Context HttpServletRequest request) {

        HttpSession userSession = request.getSession(false);
        if (userSession == null || (int) userSession.getAttribute("userId") != id) {
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
            }
        }

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

    /**
     * This method is used to get the image of a person, which is stored through the previous method.
     * The image is read from the filesystem and returned as a response.
     *
     * @return The response with the image
     */
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

    /**
     * This method is used to get id of a person by their username.
     *
     * @param username The username of the person
     * @return The response with the id
     */
    @GET
    @Path("/getid/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getID(@PathParam("username") String username) {
        return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.getID(username)).build();
    }

    /**
     * This method is used to get all interested posts of a given person.
     *
     * @param id The id of a given person.
     * @return The response with the interested posts
     */
    @GET
    @Path("{id}/interestedposts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterestedPosts(@PathParam("id") int id) {
        try {
            return Response.status(Response.Status.OK).entity(PersonDao.INSTANCE.getInterestedPosts(id)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to check if a person is a contact of another person.
     *
     * @param requestedUserId The id of the person to be checked
     * @return The response with the result, either true or false.
     */
    @GET
    @Path("/contact/{requestedUserId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isContact(@PathParam("requestedUserId") int requestedUserId, @Context HttpServletRequest request) {
        Map<String, Boolean> responseObj = new HashMap<>();
        // In case user is not authenticated, return unauthorized
        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }

        int userId = (int) request.getSession().getAttribute("userId");
        try {
            Boolean isContact = PersonDao.INSTANCE.isContact(userId, requestedUserId);
            // Create the return Object & return as JSON
            responseObj.put("isContact", isContact);
            ObjectMapper mapper = new ObjectMapper();
            return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(responseObj)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to add a person as a contact of another person.
     *
     * @param requestedUserId The id of the person to be added as a contact
     * @return The response with the result
     */
    @POST
    @Path("/contact/{requestedUserId}")
    public Response addContact(@PathParam("requestedUserId") int requestedUserId, @Context HttpServletRequest request) {
        // In case user is not authenticated, return unauthorized
        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }

        int userId = (int) request.getSession().getAttribute("userId");
        try {
            MessageHistory mh = new MessageHistory();
            mh.setUser1(String.valueOf(userId));
            mh.setUser2(String.valueOf(requestedUserId));
            PersonDao.INSTANCE.createContact(mh);
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
