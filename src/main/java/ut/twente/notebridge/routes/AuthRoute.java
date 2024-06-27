package ut.twente.notebridge.routes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.utils.Security;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to handle the authentication routes.
 */
@Path("/auth")
public class AuthRoute {

    /**
     * This method is used to handle the login route.
     * It checks the credentials and logs the user in.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @param request  The request
     * @return The response
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password,
                          @Context HttpServletRequest request) {

        // Simple authentication logic (replace with your own)
        try {
            if (Security.checkCredentials(email, password.toCharArray())) {
                BaseUser baseUser = BaseUserDao.INSTANCE.getUserByEmail(email);
                HttpSession session = request.getSession(true);
                session.setAttribute("username", baseUser.getUsername());
                session.setAttribute("email", email);
                session.setAttribute("userId", baseUser.getId());
                if (BaseUserDao.INSTANCE.isPerson(baseUser.getId())) {
                    session.setAttribute("role", "person");
                } else {
                    session.setAttribute("role", "sponsor");
                }
                NewCookie cookie = new NewCookie.Builder("JSESSIONID")
                        .value(session.getId())
                        .path("/notebridge/")
                        .secure(true)
                        .sameSite(NewCookie.SameSite.LAX)// Set the value of the cookie
                        .httpOnly(true) // Optional, adds the HttpOnly attribute
                        .maxAge(3600)   // Optional, sets the max age of the cookie in seconds
                        .build();


                return Response.ok("Login successful").cookie(cookie).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
            }
        } catch (Exception e) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("Server not available").build();
        }
    }

    /**
     * This method is used to handle the logout route.
     * It logs the user out, invalidating the session.
     *
     * @param request The request
     * @return The response
     */
    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Get existing session, do not create new
        if (session != null) {
            session.invalidate();
        }
        return Response.ok("Logged out").build();
    }

    /**
     * This method mainly exposes operational information about the running application.
     *
     * @return The response
     */
    @GET
    @Path("/health")
    public Response welcome() {
        return Response.ok("Welcome").build();
    }

    /**
     * This method checks if the user is logged in and returns the user's information.
     *
     * @param request The request
     * @return The response
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Get existing session, do not create new
        if (session != null && session.getAttribute("email") != null) {

            String username = (String) session.getAttribute("username");
            String email = (String) session.getAttribute("email");
            int userId = (int) session.getAttribute("userId");
            String role = (String) session.getAttribute("role");
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("email", email);
            responseMap.put("userId", userId);
            responseMap.put("role", role);
            responseMap.put("username", username);

            return Response.ok().entity(responseMap).build();

        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("not-logged-in").build();
        }
    }
}