

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
import javax.sql.DataSource;

//import java.sql.*;

/**
 * Servlet implementation class StarInfo
 */
public class StarInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	        response.setContentType("text/html");    // Response mime type

	        // Output stream to STDOUT
	        PrintWriter out = response.getWriter();
	        
	        out.println("<HTML><HEAD><TITLE>Star Info</TITLE></HEAD>");
	        out.println("<BODY ALIGN = \"CENTER\" BGCOLOR=\"#FDF5E6\"><H1>Star Information:</H1>");

	        try
	           {
	          
	            Context initCtx = new InitialContext();

	      		Context envCtx = (Context) initCtx.lookup("java:comp/env");
	      		// Look up our data source
	      		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

	      		Connection dbcon = ds.getConnection();

	              // Declare our statement
	              Statement statement = dbcon.createStatement();

	              String starId = request.getParameter("starid");
	              String query = "SELECT * from stars WHERE id = ?";
	              PreparedStatement ps = dbcon.prepareStatement(query);
	              ps.setString(1, starId);
	              ResultSet rs = ps.executeQuery();
	              // Perform the query
            
	              // Iterate through each row of rs
	             while (rs.next())
	              {
	                  String s_FN = rs.getString("first_name");
	                  String s_LN = rs.getString("last_name");
	                  String s_DOB = rs.getString("dob");
	                  String s_Photo = rs.getString("photo_url");
	                  
	                  out.println("<img src= \""+s_Photo+"\" alt=\"" +s_FN  + " " + s_LN +" Banner\" "
		                  		+ "width =\"128\" heigth=\"128\"> <br></br>" );
	                  out.println("<tr>" +
	                              "<td><p> <strong> Name: </strong>" + s_FN  + " " + s_LN +  "</td>" +
	                              "<td><p> <strong> Date of Birth: </strong>" + s_DOB +"</td>" +
	                              "</tr>");
	              }
	             
	             	  String moviesQuery = "SELECT  m.id, title, director, year FROM (movies m JOIN stars_in_movies sm "
		              		+ "JOIN stars s "
		              		+ "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE s.id=? AND sm.star_id = ?"; 
		              PreparedStatement moviesPs = dbcon.prepareStatement(moviesQuery);
		              moviesPs.setString(1, starId);
		              moviesPs.setString(2, starId);
		              ResultSet moviesRS = moviesPs.executeQuery();
		             
		              out.println("<p> <strong> Stars in: </strong> </p>");
		              out.println("<TABLE ALIGN = \"CENTER\" border>");
		              out.println("<tr><td> ID </td><td>Movie Title</td> <td> Year</td> <td>Director</td> <td> Movie Link</td></tr>");
		              while(moviesRS.next())
		              { 
		            	  String m_Title = moviesRS.getString("m.title");
		            	  String m_Year = moviesRS.getString("m.year");
		            	  String m_director = moviesRS.getString("m.director");
		            	  String m_id = moviesRS.getString("m.id");
		            	  out.println("<tr ALIGN = \"CENTER\">" +
		            	  "<td>" + m_id + "</td>" +
		            	  "<td>" + m_Title + " </td><td>" + 	  
		            	  m_Year+ "</td><td>"+ m_director +  "</td>"
		            	  		+ "<td> <a href = \"/Fabflix/MovieInfo?movieid="+ m_id +"\"> Link </a></td> </tr>");  
		            	  
		              }
		             out.println("</TABLE>");
		          
		          moviesRS.close();
	              rs.close();
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
	        out.println("<p><a href=\"/Fabflix/ShoppingCart\">Shopping Cart</a></p>");
	        out.println("<p><a href=\"javascript:history.back()\">Back</a></p>");
        	out.println("<p><a href=\"/Fabflix/main.html\">Home</a></p>");

	        out.close();
	}

}
