package ut.twente.notebridge.routes;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.CommentDao;
import ut.twente.notebridge.model.Comment;

@Path("/comments")
public class CommentRoute {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment createComment(Comment comment) {
        return CommentDao.INSTANCE.create(comment);
    }

}
