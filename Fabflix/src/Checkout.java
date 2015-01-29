

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class Checkout
 */
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        out.println("<HTML><HEAD><TITLE>Checkout</TITLE></HEAD>");
        out.println("<BODY ALIGN = \"CENTER\" BGCOLOR=\"#FDF5E6\"><H1>Checkout</H1>");
        try
        {
        	  Context initCtx = new InitialContext();

	      		Context envCtx = (Context) initCtx.lookup("java:comp/env");
	      		// Look up our data source
	      		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

	      		Connection dbcon = ds.getConnection();
             // Declare our statement
             Statement statement = dbcon.createStatement();
             
            
             String b1 = request.getParameter("boxOne");
             String b2 = request.getParameter("boxTwo");
             String b3 = request.getParameter("boxThree");
             String b4 = request.getParameter("boxFour");
             
             String first_name = request.getParameter("firstname");
             String last_name = request.getParameter("lastname");
             String expirDate = request.getParameter("year") + "-" + request.getParameter("month") + "-" + request.getParameter("day");
             String ccNumber = b1;//= b1+"%"+b2+"%"+b  3+"%"+b4;
              
             if(!b2.equals(""))
             {
            	ccNumber = ccNumber +"%"+b2; 
            	if(!b3.equals(""))
            	{
            		ccNumber = ccNumber+"%"+b3;
            		
            		if(!b4.equals(""))
            		{
            			ccNumber = ccNumber + "%" +b4;
            		}
            	}
             }
             
             String query = "SELECT * FROM creditcards WHERE id LIKE ? AND first_name = ? AND last_name = ? AND expiration = ?";
             PreparedStatement prepState = dbcon.prepareStatement(query);
             prepState.setString(1, ccNumber);
             prepState.setString(2, first_name);
             prepState.setString(3, last_name);
             prepState.setString(4, expirDate);
             
             ResultSet rs = prepState.executeQuery();
             if(!rs.next()){
            	 out.println("The information provided doesn't match our records. Please try again.");
            	 out.println("<p><a href=\"javascript:history.back()\">Back</a></p>");
             }
            else{ 
            	 out.println("Congratulations on your purchase!");
            	 out.println("<p><a href=\"/Fabflix/main.html\">Home</a></p>");
            	 //<p><a href="/Fabflix/main.html">Home</a></p>
            	 HttpSession session = request.getSession();
            	 session.setAttribute("cart", null);
            }
             rs.close();
             prepState.close();
             statement.close();
             dbcon.close();
        }
        catch (SQLException ex) {
            while (ex != null) {
                System.out.println ("SQL Exception:  " + ex.getMessage ());
                ex = ex.getNextException ();
            }  // end while
        }  // end catch SQLException

    catch(java.lang.Exception ex)
        {
            out.println("<HTML>" +
                        "<HEAD><TITLE>" +
                        "MovieDB: Error" +
                        "</TITLE></HEAD>\n<BODY>" +
                        "<P>SQL error in doGet: " +
                        ex.getMessage() + "</P></BODY></HTML>");
            return; 
        }
     out.close();
	}

}
