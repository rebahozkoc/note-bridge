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

/**
 * This class is used to handle the message routes.
 */
@Path("/message")
public class MessageRoute {

    /**
     * This method is used to get all contacts of a user.
     *
     * @param sortBy     The field to sort by
     * @param pageSize   The page size
     * @param pageNumber The page number
     * @param id         The id of the user
     * @return The response with the contacts
     */
    @GET
    @Path("/contact/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContacts(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
            @PathParam("id") String id
    ) {
        try {
            int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
            int pn = pageNumber > 0 ? pageNumber : 1;
            var resources = MessageDao.INSTANCE.getContacts(ps, pn, sortBy, id).toArray(new BaseUser[0]);
            var total = MessageDao.INSTANCE.getTotalMessages();

            return Response.ok().entity(new ResourceCollection<>(resources, ps, pn, total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to create a message history between two users.
     *
     * @return The response with the message history
     */
    @POST
    @Path("/newhistory/{user1}/{user2}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newMessageHistory(@PathParam("user1") String user1, @PathParam("user2") String user2) {
        try {
            MessageHistory mh = new MessageHistory();
            mh.setUser1(user1);
            mh.setUser2(user2);
            return Response.ok().entity(MessageDao.INSTANCE.create(mh)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to add a message to a message history.
     *
     * @return The response with the added message
     */
    @POST
    @Path("/newmessage/{user}/{contact}/{message}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMessage(@PathParam("user") String user, @PathParam("contact") String contact, @PathParam("message") String message) {
        try {
            Message m = new Message();
            m.setUser_id(Integer.valueOf(user));
            m.setContent(URLDecoder.decode(message, StandardCharsets.UTF_8));
            return Response.ok().entity(MessageDao.INSTANCE.createNewMessage(contact, m)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to delete a message history.
     *
     * @return The response with the result
     */
    @DELETE
    @Path("/deletehistory{id}/{user}")
    public Response deleteMessageHistory(@PathParam("id") Integer id, @PathParam("user") Integer user) {
        try {
            MessageDao.INSTANCE.delete(id, user);
            return Response.ok(Response.Status.OK).entity("MessageHistory with id " + id + " is deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to delete a message.
     *
     * @return The response with the result
     */
    @DELETE
    @Path("/deletemessage/{user_id}/{timestamp}/{content}")
    public Response deleteMessage(@PathParam("user_id") int id, @PathParam("timestamp") String timestamp, @PathParam("content") String content) {
        try {
            content = URLDecoder.decode(content, StandardCharsets.UTF_8);
            Message m = new Message();
            m.setId(id);
            m.setContent(content);
            MessageDao.INSTANCE.deleteMessage(m, timestamp);
            return Response.ok(Response.Status.OK).entity("Message with id " + id + " was deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to read messages.
     * isRead field in message is set to true.
     *
     * @return The response with the result
     */
    @PUT
    @Path("/readmessages/{id}")
    public Response readMessages(@PathParam("id") int id) {
        try {
            MessageDao.INSTANCE.readMessages(id);
            return Response.ok(Response.Status.OK).entity("Read message with id " + id).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to return the number of unread messages.
     *
     * @return The response with the number of unread messages
     */
    @GET
    @Path("/count/{user}/{contact}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countUnreadMessages(@PathParam("user") int user, @PathParam("contact") int contact) {
        try {
            return Response.status(Response.Status.OK).entity(MessageDao.INSTANCE.countUnreadMessages(user, contact)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to get all messages between two users.
     *
     * @return The response with the messages
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/messages/{user1}/{user2}")
    public Response getMessages(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") int pageNumber,
            @PathParam("user1") String user1,
            @PathParam("user2") String user2
    ) {
        try {
            int ps = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
            int pn = pageNumber > 0 ? pageNumber : 1;
            var resources = MessageDao.INSTANCE.getMessages(ps, pn, sortBy, user1, user2).toArray(new Message[0]);
            var total = MessageDao.INSTANCE.getTotalMessages();

            return Response.ok().entity(new ResourceCollection<>(resources, ps, pn, total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
