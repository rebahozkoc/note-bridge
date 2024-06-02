package cards;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


public class forum extends HttpServlet {


    private static final long serialVersionUID = 1L;

    private cards card;

    public void init() throws ServletException {
        card = new cards();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        String text = request.getParameter("text");  //Retrieve data
        PrintWriter out = response.getWriter();
        out.println("Hello, you've typed" + text);

    }

}
