package ut.twente.notebridge.routes;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.model.ResourceCollection;

@Path("/message")
public class MessageRoute {

    @GET
    @Path("/messages/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Message> getMessageHistory(@PathParam("user") String user) {

        return MessageDao.INSTANCE.getMessages(user);

    }

    @GET
    @Path("/contact/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ResourceCollection<BaseUser> getContacts(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
            @PathParam("id") String id
    ){
        int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
        int pn = pageNumber > 0 ? pageNumber : 1;
        var resources = MessageDao.INSTANCE.getContacts(ps, pn, sortBy, id).toArray(new BaseUser[0]);
        var total = MessageDao.INSTANCE.getTotalMessages();

        return new ResourceCollection<>(resources, ps, pn, total);
    }

    @PUT
    @Path("/newhistory{user1}/{user2}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void newMessageHistory(@PathParam("user1") String user1, @PathParam("user2") String user2) {
        MessageDao.INSTANCE.create(user1, user2);
    }

    @PUT
    @Path("/newmessage{user}/{contact}/{message}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addMessage(@PathParam("user") String user, @PathParam("contact") String contact, @PathParam("message") String message) {
        Message m=new Message();
        m.setUser(user);
        m.setMessage(message);
        MessageDao.INSTANCE.createNewMessage(contact,m);
    }

    @DELETE
    @Path("/deletehistory{id}/{user}")
    public void deleteMessageHistory(@PathParam("id") Integer id, @PathParam("user") Integer user) {
        MessageDao.INSTANCE.delete(id, user);
    }

    @PUT
    @Path("/deletemessage{user}/{message}/{time}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteMessage(@PathParam("user") String user, @PathParam("message") String message, @PathParam("time") String time) {
        Message m=new Message();
        m.setDate(Timestamp.valueOf(time));//probably will need adjusting
        m.setUser(user);
        m.setMessage(message);
        MessageDao.INSTANCE.deleteMessage(m);
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
