package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.model.ResourceCollection;

@Path("/message")
public class MessageRoute {

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
        MessageHistory mh=new MessageHistory();
        mh.setUser1(user1);
        mh.setUser2(user2);
        MessageDao.INSTANCE.create(mh);
    }

    @POST
    @Path("/newmessage/{user}/{contact}/{message}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Message addMessage(@PathParam("user") String user, @PathParam("contact") String contact, @PathParam("message") String message) {
        Message m=new Message();
        m.setUser_id(Integer.valueOf(user));
        m.setContent(URLDecoder.decode(message, StandardCharsets.UTF_8));
        return MessageDao.INSTANCE.createNewMessage(contact,m);
    }

    @DELETE
    @Path("/deletehistory{id}/{user}")
    public void deleteMessageHistory(@PathParam("id") Integer id, @PathParam("user") Integer user) {
        MessageDao.INSTANCE.delete(id, user);
    }

    @DELETE
    @Path("/deletemessage/{id}")
    public void deleteMessage(@PathParam("id") int id) {
        try{
            MessageDao.INSTANCE.deleteMessage(id);

        } catch (Exception e) {
            throw new NotFoundException("Message '" + id + "' not found!");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/messages/{user1}/{user2}")
    public ResourceCollection<Message> getMessages(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
            @PathParam("user1") String user1,
            @PathParam("user2") String user2
    ){
        int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
        int pn = pageNumber > 0 ? pageNumber : 1;
        var resources = MessageDao.INSTANCE.getMessages(ps, pn, sortBy, user1, user2).toArray(new Message[0]);
        var total = MessageDao.INSTANCE.getTotalMessages();

        return new ResourceCollection<>(resources, ps, pn, total);
    }
}
