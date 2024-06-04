package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.model.ResourceCollection;

@Path("/message")
public class MessageRoute {

    @GET
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageHistory getMessageHistory(@PathParam("user") String user) {

        return MessageDao.INSTANCE.getMessages(user);

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public MessageHistory newMessageHistory(MessageHistory message) {
        return MessageDao.INSTANCE.create(message);
    }

    @PUT
    @Path("/add{id}{message}{time}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addMessage(@PathParam("id") String user, @PathParam("message") String message, @PathParam("time") String time) {
        MessageDao.INSTANCE.createNewMessage(user,message);
    }

    @DELETE
    @Path("/{id}")
    public void deleteMessageHistory(@PathParam("id") String id) {
        MessageDao.INSTANCE.delete(id);
    }

    @PUT
    @Path("/delete{id}{message}{time}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteMessage(@PathParam("id") String user, @PathParam("message") String message, @PathParam("time") String time) {
        Message m=new Message();
        m.setDate(LocalDateTime.parse(time));//probably will need adjusting
        m.setUser(user);
        m.setMessage(message);
        MessageDao.INSTANCE.deleteMessage(user,m);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ResourceCollection<Message> getMessages(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
             @QueryParam("id") String id
    ){
        int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
        int pn = pageNumber > 0 ? pageNumber : 1;
        var resources = MessageDao.INSTANCE.getMessages(ps, pn, sortBy, id).toArray(new Message[0]);
        var total = MessageDao.INSTANCE.getTotalMessages();

        return new ResourceCollection<>(resources, ps, pn, total);
    }
}
