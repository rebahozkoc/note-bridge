package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.ResourceCollection;

@Path("/message")
public class MessageRoute {

    @GET
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Message getMessageHistory(@PathParam("user") String user) {

        return MessageDao.INSTANCE.getMessages(user);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Message newMessageHistory(Message message) {
        return MessageDao.INSTANCE.create(message);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addMessage(@PathParam("id") String user, String message) {
        MessageDao.INSTANCE.createNewMessage(user,message);
    }

    @DELETE
    @Path("/{id}")
    public void deleteMessageHistory(@PathParam("id") String id) {
        MessageDao.INSTANCE.delete(id);
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ResourceCollection<Message> getMessages(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber
    ){
        int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
        int pn = pageNumber > 0 ? pageNumber : 1;
        var resources = MessageDao.INSTANCE.getMessages(ps, pn, sortBy).toArray(new Message[0]);
        var total = MessageDao.INSTANCE.getTotalMessages();

        return new ResourceCollection<>(resources, ps, pn, total);
    }
}
