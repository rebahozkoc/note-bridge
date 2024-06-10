package ut.twente.notebridge.routes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthRoute{

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("username") String username,
	                      @FormParam("password") String password,
	                      @Context HttpServletRequest request,
	                      @Context HttpServletResponse response) {

		// Simple authentication logic (replace with your own)
		if ("user".equals(username) && "password".equals(password)) {
			HttpSession session = request.getSession(true);
			session.setAttribute("username", username);

			NewCookie cookie = new NewCookie.Builder("JSESSIONID")
					.value(session.getId())
					.path("/notebridge/")
					.secure(true)
					.sameSite(NewCookie.SameSite.LAX)// Set the value of the cookie
					.httpOnly(true) // Optional, adds the HttpOnly attribute
					.maxAge(3600)   // Optional, sets the max age of the cookie in seconds
					.build();

			System.out.println(session.getAttribute("username"));
			System.out.println(session.getId());
			return Response.ok("Login successful").cookie(cookie).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
		}
	}


	@GET
	@Path("/loggedintest")
	public Response welcome(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false); // Get existing session, do not create new
		System.out.println(session.getAttribute("username"));
		if (session != null && session.getAttribute("username") != null) {
			String username = (String) session.getAttribute("username");
			return Response.ok("Welcome, " + username).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Please log in").build();
		}
	}
}