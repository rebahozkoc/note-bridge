package ut.twente.notebridge.routes;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.model.ResourceCollection;

@Path("/message")
public class MessageRoute {

    @GET
    @Path("/contact/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContacts(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
            @PathParam("id") String id
    ){
        try{
            int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
            int pn = pageNumber > 0 ? pageNumber : 1;
            var resources = MessageDao.INSTANCE.getContacts(ps, pn, sortBy, id).toArray(new BaseUser[0]);
            var total = MessageDao.INSTANCE.getTotalMessages();

            return Response.ok().entity(new ResourceCollection<>(resources, ps, pn, total)).build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/newhistory/{user1}/{user2}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessageHistory newMessageHistory(@PathParam("user1") String user1, @PathParam("user2") String user2) {
        MessageHistory mh=new MessageHistory();
        mh.setUser1(user1);
        mh.setUser2(user2);
        return MessageDao.INSTANCE.create(mh);
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
    @Path("/deletemessage/{user_id}/{timestamp}/{content}")
    public void deleteMessage(@PathParam("user_id") int id, @PathParam("timestamp") String timestamp,@PathParam("content") String content) {
        try{
            content=URLDecoder.decode(content, StandardCharsets.UTF_8);
            MessageDao.INSTANCE.deleteMessage(id,timestamp,content);

        } catch (Exception e) {
            throw new NotFoundException("Message '" + id + "' not found!");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/messages/{user1}/{user2}")
    public Response getMessages(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
            @PathParam("user1") String user1,
            @PathParam("user2") String user2
    ){
        try{
            int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
            int pn = pageNumber > 0 ? pageNumber : 1;
            var resources = MessageDao.INSTANCE.getMessages(ps, pn, sortBy, user1, user2).toArray(new Message[0]);
            var total = MessageDao.INSTANCE.getTotalMessages();

            return Response.ok().entity(new ResourceCollection<>(resources, ps, pn, total)).build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
