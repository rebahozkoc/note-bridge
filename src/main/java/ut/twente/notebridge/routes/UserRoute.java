package ut.twente.notebridge.routes;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.UserDao;
import ut.twente.notebridge.model.User;

@Path("/users")
public class UserRoute {

    //When user wants to create an account for the first time
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(User user) {
        return UserDao.INSTANCE.create(user);
    }

    //When user wants to view their account
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") String id) {
        return UserDao.INSTANCE.getUser(id);
    }

    //When user wants to make update on their account
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User updateUser(@PathParam("id") String id, User user) {
        return UserDao.INSTANCE.update(user);
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") String id) {
        UserDao.INSTANCE.delete(id);
    }


}
