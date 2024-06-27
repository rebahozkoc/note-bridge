package ut.twente.notebridge.routes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to handle the sponsor routes.
 */
@Path("/sponsors")
public class SponsorRoute {
    /**
     * This method is used to create a sponsor.
     *
     * @param sponsor The sponsor to be created
     * @return The response with the created sponsor
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSponsor(Sponsor sponsor) {
        System.out.println("PersonRoute.createSponsor is called");
        try {
            if (Security.checkPasswordValidity(sponsor.getPassword())) {
                BaseUser baseUser = BaseUserDao.INSTANCE.create(sponsor);
                sponsor.setBaseUser(baseUser);
                return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.create(sponsor)).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Password is not valid").build();
            }
        } catch (Exception e) {
            System.out.println("Error while creating sponsor user");
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to get a sponsor with a given id.
     *
     * @param id The id of the sponsor
     * @return The response with the sponsor
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSponsor(@PathParam("id") int id) {
        try {
            return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.getSponsor(id)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to update a sponsor.
     *
     * @param id      The id of the sponsor to be updated
     * @param sponsor The sponsor to be updated
     * @return The response with the updated sponsor
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSponsor(@PathParam("id") Integer id, Sponsor sponsor, @Context HttpServletRequest request) {

        HttpSession userSession = request.getSession(false);
        if (!Security.isAuthorized(userSession, "sponsor") || (int) userSession.getAttribute("userId") != id) {
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
            }
        }
        Sponsor existingSponsor = SponsorDao.INSTANCE.getSponsor(id);
        if (sponsor.getUsername() != null) {
            existingSponsor.setUsername(sponsor.getUsername());
        }
        if (sponsor.getEmail() != null) {
            existingSponsor.setEmail(sponsor.getEmail());
        }
        if (sponsor.getPhoneNumber() != null) {
            existingSponsor.setPhoneNumber(sponsor.getPhoneNumber());
        }
        if (sponsor.getCompanyName() != null) {
            existingSponsor.setCompanyName(sponsor.getCompanyName());
        }
        if (sponsor.getWebsiteURL() != null) {
            existingSponsor.setWebsiteURL(sponsor.getWebsiteURL());
        }
        if (sponsor.getDescription() != null) {
            existingSponsor.setDescription(sponsor.getDescription());
        }


        try {
            return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.update(existingSponsor)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to delete a sponsor.
     *
     * @param id The id of the sponsor to be deleted
     * @return The response with the result
     */
    @DELETE
    @Path("/{id}")
    public Response deleteSponsor(@PathParam("id") int id, @Context HttpServletRequest request) {
        HttpSession userSession = request.getSession(false);
        if (!Security.isAuthorized(userSession, "sponsor") || (int) userSession.getAttribute("userId") != id) {
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
            }
        }
        try {
            SponsorDao.INSTANCE.delete(id);
            BaseUserDao.INSTANCE.delete(id);
            userSession.invalidate();
            return Response.status(Response.Status.OK).entity("Sponsor deleted").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to update the profile picture of a sponsor.
     *
     * @param id                  The id of the sponsor
     * @param uploadedInputStream The input stream of the image
     * @param fileDetail          The file details
     * @return The response with the updated sponsor
     */
    @PUT
    @Path("{id}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putImage(
            @PathParam("id") Integer id,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        try {
            Sponsor sponsor = SponsorDao.INSTANCE.getSponsor(id);
            System.out.println("SponsorRoute.putImage is called");
            BaseUser baseUser = BaseUserDao.INSTANCE.setProfilePicture(id, uploadedInputStream, fileDetail.getFileName());
            sponsor.setBaseUser(baseUser);
            SponsorDao.INSTANCE.update(sponsor);
            return Response.status(Response.Status.OK).entity(sponsor).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /**
     * This method is used to get the profile picture of a sponsor.
     *
     * @param id The id of the sponsor
     * @return The response with the image
     */
    @GET
    @Path("{id}/image")
    public Response getImage(@PathParam("id") Integer id) {
        Sponsor sponsor = SponsorDao.INSTANCE.getSponsor(id);
        String fileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + sponsor.getPicture();
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
     * This method is used to sponsor a post.
     *
     * @param id      The id of the sponsor
     * @param post    The post to be sponsored
     * @param request The request
     * @return The response with the sponsored post
     */
    @PUT
    @Path("{id}/post")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sponsorPost(@PathParam("id") Integer id, Post post, @Context HttpServletRequest request) {
        System.out.println("SponsorRoute.sponsorPost is called");
        HttpSession userSession = request.getSession(false);
        if (!Security.isAuthorized(userSession, "sponsor")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authorized").build();
        }

        try {
            post.setId(id);
            post.setSponsoredBy((int) userSession.getAttribute("userId"));
            return Response.status(Response.Status.OK).entity(SponsorDao.INSTANCE.sponsorPost(post)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
