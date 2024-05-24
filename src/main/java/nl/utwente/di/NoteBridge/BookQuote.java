package nl.utwente.di.NoteBridge;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

/** Example of a Servlet that gets an ISBN number and returns the book price
 */

public class BookQuote extends HttpServlet {
 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Quoter quoter;
	
    public void init() throws ServletException {
    	quoter = new Quoter();
    }	
	
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    String title = "Celsius to Fahrenheit translator";
    
    // Done with string concatenation only for the demo
    // Not expected to be done like this in the project
    out.println("<!DOCTYPE HTML>\n" +
                "<HTML>\n" +
                "<HEAD><TITLE>" + title + "</TITLE>" +
                "<LINK REL=STYLESHEET HREF=\"styles.css\">" +
                		"</HEAD>\n" +
                "<BODY BGCOLOR=\"#FDF5E6\">\n" +
                "<H1>" + title + "</H1>\n" +              
                "  <P>Enter temperature in C: " +
                   request.getParameter("isbn") + "\n" +
                "  <P>Temperature in Fahrenheit: " +
                   quoter.getBookPrice(request.getParameter("isbn")) +
                "</BODY></HTML>");
  }
}
