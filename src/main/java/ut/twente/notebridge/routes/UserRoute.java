package ut.twente.notebridge.routes;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.UserDao;
import ut.twente.notebridge.model.User;

@Path("/users")
public class UserRoute {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") String id) {
        return UserDao.INSTANCE.getUser(id);
    }

}
