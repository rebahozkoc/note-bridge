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
import ut.twente.notebridge.utils.Security;

@Path("/auth")
public class AuthRoute{

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("email") String email,
	                      @FormParam("password") String password,
	                      @Context HttpServletRequest request,
	                      @Context HttpServletResponse response) {

		// Simple authentication logic (replace with your own)
		if (Security.checkCredentials(email, password)) {
			HttpSession session = request.getSession(true);
			session.setAttribute("email", email);
			session.setAttribute("userId", 1);
			session.setAttribute("role", "person");
			NewCookie cookie = new NewCookie.Builder("JSESSIONID")
					.value(session.getId())
					.path("/notebridge/")
					.secure(true)
					.sameSite(NewCookie.SameSite.LAX)// Set the value of the cookie
					.httpOnly(true) // Optional, adds the HttpOnly attribute
					.maxAge(3600)   // Optional, sets the max age of the cookie in seconds
					.build();

			System.out.println(session.getAttribute("email"));
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
		if (session.getAttribute("username") != null) {
			String username = (String) session.getAttribute("username");
			return Response.ok("Welcome, " + username).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Please log in").build();
		}
	}

	@GET
	@Path("/status")
	public Response status(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false); // Get existing session, do not create new
		if (session != null && session.getAttribute("email") != null) {
			String email = (String) session.getAttribute("email");
			int userId = (int) session.getAttribute("userId");
			String role = (String) session.getAttribute("role");
			return Response.ok("User is logged in: " + email + ", User ID: " + userId + ", Role" + role).build();

		} else {
			return Response.ok("Not logged in").build();
		}
	}
}