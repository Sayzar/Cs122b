

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.sql.*;

/**
 * Servlet implementation class MovieInfo
 */
public class MovieInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public String getServletInfo()
    {
       return "Servlet connects to MySQL database and displays result of a SELECT";
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	        response.setContentType("text/html");    // Response mime type

	        // Output stream to STDOUT
	        PrintWriter out = response.getWriter();
	        
	        out.println("<HTML><HEAD><TITLE>Movie Informationt</TITLE></HEAD>");
	        out.println("<BODY ALIGN=\"CENTER\" BGCOLOR=\"#FDF5E6\"><H1 ALIGN=\"CENTER\" >Information:</H1>");
	        
	        try
	           {
	              //Class.forName("org.gjt.mm.mysql.Driver");
	        		Context initCtx = new InitialContext();

		      		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		      		// Look up our data source
		      		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

		      		Connection dbcon = ds.getConnection();
	              // Declare our statement
	              Statement statement = dbcon.createStatement();
	       
	              String movieId = request.getParameter("movieid");
	              String query = "SELECT * from movies WHERE id = ?";
	              PreparedStatement ps = dbcon.prepareStatement(query);
	              ps.setString(1, movieId);
	              // Perform the query
	              ResultSet movieRS = ps.executeQuery(); 

	              String movie = "";
	              while (movieRS.next())
	              {
	                  String m_Dir = movieRS.getString("director");
	                 String m_Title = movieRS.getString("title");
	                  movie = m_Title;
	                  String m_Year = movieRS.getString("year");
	                  String m_Photo = movieRS.getString("banner_url");
	                  String m_Trailer = movieRS.getString("trailer_url");
	                  
	                  //SELECT * FROM (movies m JOIN stars_in_movies sm 
	                  //NATURAL JOIN stars s ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE first_name=? 
	                  
	                  out.println("<img src= \""+m_Photo+"\" alt=\"" +m_Title+" Banner\" "
	                  		+ "width =\"128\" heigth=\"128\"> <br></br>" );
	                  out.println("<tr ALIGN=\"CENTER\">" +
	                              "<td> <p> <strong> Movie Title: </strong>" + m_Title + " </p> </td>" +
	                              "<td> <p> <strong> Director: </strong>" + m_Dir + " </p> </td>" +
	                              "<td> <p> <strong> Year: </strong>" + m_Year + "</p></td>" +
	                              "<td><p> <strong> Trailer Link: </strong> <a href = \""+ m_Trailer +"\"> Link </a> </p> </td>" + 
	                              "</tr>");
	              }	
	           
	              out.println("<br></br> <TABLE ALIGN=\"CENTER\" border>"
	              		+ "<form action =\"ShoppingCart\">"
	              		+ "<th>Movie Name</th> <th> Price</th> <th> Add to Cart </th>"
	              		+ "<tr><td>"+movie+"</td> <td>$15</td> <td><input type =\"hidden\" name=\"title\" value=\""+movie+"\">"
	              				+ "<input type = \"hidden\" name = \"price\" value = \"15\">"
	              				+ "<input type = \"submit\" value = \"Add to cart\"></td></TABLE>");
	              
	              String genrequery = "SELECT name FROM (genres g JOIN genres_in_movies gm "
	      		  		+ "ON g.id = gm.genre_id) WHERE gm.movie_id= ?";
	              PreparedStatement genrePrep = dbcon.prepareStatement(genrequery);
	              genrePrep.setString(1, movieId);
	              ResultSet genreRs = genrePrep.executeQuery();
	              
	              out.println("<p><strong> Genre : </strong><p>");
	          
	              while(genreRs.next())
	              {
	            	  String g_nm = genreRs.getString("name");
	            	  out.println("<tr>"+g_nm+"</tr>");
	              }
	              out.println("<br></br>");
	              
	              
	              String starsQuery = "SELECT first_name, last_name, s.id FROM (movies m JOIN stars_in_movies sm "
	              		+ "NATURAL JOIN stars s "
	              		+ "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE m.id=?"; 
	              PreparedStatement starsPs = dbcon.prepareStatement(starsQuery);
	              starsPs.setString(1, movieId);
	              ResultSet starsRS = starsPs.executeQuery();
	            
	              out.println("<strong>Stars In Movie</strong> <br></br>");
	              out.println("<TABLE ALIGN = \"CENTER\" border>");
	              out.println("<tr><td> ID </td> <td> First Name</td> <td> Last Name </td> <td> Star Page</td> </tr>");
	              while(starsRS.next())
	              {
	            	  String s_first = starsRS.getString("first_name");
	            	  String s_last = starsRS.getString("last_name");
	            	  String s_id = starsRS.getString("s.id");
	            	  out.println("<tr>" +
	            	  "<td>" + s_id + "</td>"+
	            	  	 "<td>" + s_first+ "</td>"
	            	  	 +"<td>" + s_last + "</td>"
	            	  	 +"<td><a href = \"/Fabflix/StarInfo?starid="+ s_id +"\"> Link </a> </td>"
	            	  	 		+ " </tr>");  	
	              }
	              out.println("</TABLE>");
	              starsRS.close();
	              movieRS.close();
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
