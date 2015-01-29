
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

public class MovieListPage extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getServletInfo()
    {
       return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
    	doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
    	HttpSession mySession = request.getSession();

    	// Is the user logged in? Otherwise, go to the login page.
    	User thisUser = (User)mySession.getAttribute("user_object");
    	if (thisUser == null)
    	{
        	RequestDispatcher view = request.getRequestDispatcher("index.html");
        	view.forward(request, response);
        	return;
    	}
    	
      
        response.setContentType("text/html");
        
        try
        {
        	Context initCtx = new InitialContext();

	      	Context envCtx = (Context) initCtx.lookup("java:comp/env");
	      	// Look up our data source
	      	DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

	      	Connection dbcon = ds.getConnection();
            
	      	PrintWriter out = response.getWriter();
            out.println("<HTML><HEAD><TITLE>FabFlix - MovieList</TITLE>"
            		+ "<script src='/Fabflix/sorttable.js' type='text/javascript'></script>"
            		+ "<script src='/Fabflix/paging.js' type='text/javascript'></script>"
            		+ "<link rel='StyleSheet' href='../pagingStyle.css'>"
            		+ "</HEAD>"); 
            out.println("<BODY BGCOLOR=\"#FDF5E6\"><H1>Search Results</H1>");
            out.println("<p><a href=\"/Fabflix/ShoppingCart\">Shopping Cart</a></p>");
	        out.println("<p><a href=\"javascript:history.back()\">Back</a></p>");
            out.println("<p><a href=\"/Fabflix/main.html\">Home</a></p>");

            addResultsPerPageSelector(out, 25);
            Object srObj = request.getAttribute("movies");
            if (srObj != null)
            {
            	ArrayList<Movie> searchResults = (ArrayList<Movie>)srObj;
            	createResultsTable(dbcon, searchResults, out); 
            } 
            else
            {
            	System.out.println("No searchResults data given!");
            }

            createPaginator(out);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

	private void addResultsPerPageSelector(PrintWriter out, int resultsPerPage) {
		String option10 = "<option value='10'>10</option>";
		String option25 = "<option value='25'>25</option>";
		String option50 = "<option value='50'>50</option>";
		String option100 = "<option value='100'>100</option>";

		switch (resultsPerPage)
		{
		case 10:
			option10 = "<option value='10' SELECTED>10</option>";
			break;
		case 25:
			option25 = "<option value='25' SELECTED>25</option>";
			break;
		case 50:
			option50 = "<option value='50' SELECTED>50</option>";
			break;
		case 100:
			option100 = "<option value='100' SELECTED>100</option>";
			break;
		}

		out.println("<div>Results per page: <select id='resultsperpage'>" +
				option10 + option25 + option50 + option100 +
		"</select>" +
		"</div>");
	}

	private void createResultsTable(Connection dbcon, ArrayList<Movie> results,
			PrintWriter out) throws SQLException {
		out.println("<TABLE class='sortable' id='results' border>");
		out.println("<tr>" +
					"<th>id</th>"
					+ "<th>title</th>"
					+ "<th>year</th>"
					+ "<th>director</th>"
					+ "<th>genres</th>"
					+ "<th>stars</th>");

		  // Iterate through each row of rs

		for (int i = 0; i < results.size(); i++)
		{
		      String m_ID = results.get(i).getId();
		      String m_TL = results.get(i).getTitle();
		      String m_BU = results.get(i).getBannerUrl();
		      String m_YR = results.get(i).getYear();
		      String m_DR = results.get(i).getDirector(); 

		      String m_GR = getGenresInMovieString(dbcon, m_ID);
		      String m_ST = getStarsInMovieString(dbcon, m_ID);
		      
		      if (!m_BU.equals("n/a") || !m_BU.equals(""))
		      {
		    	  m_TL = "<a href='/Fabflix/MovieInfo?movieid=" + m_ID + "'>" + m_TL + "</a>";
		      }
		      
		      out.println("<tr>" +
		                  "<td>" + m_ID + "</td>" +
		                  "<td>" + m_TL + "</td>" +
		                  "<td>" + m_YR + "</td>" +
		                  "<td>" + m_DR + "</td>" +
		                  "<td>" + m_GR + "</td>" +
		                  "<td>" + m_ST + "</td>" +
		                  "</tr>");
		}

		  out.println("</TABLE>");
	}
    
    private String getGenresInMovieString(Connection dbcon, String m_ID)
    		throws SQLException
    {
		String query = "SELECT name FROM (genres g JOIN genres_in_movies gm "
		  		+ "ON g.id = gm.genre_id) WHERE gm.movie_id=" + m_ID;
		  
		  Statement statement = dbcon.createStatement();
		  ResultSet rs = statement.executeQuery(query);
		  
		  StringBuilder sb = new StringBuilder();
		  boolean genresFound = false;
		  while(rs.next())
		  {
			  genresFound = true;
			  String s_NA = rs.getString("name");
			  sb.append(s_NA + ", ");
		  }
		  
		  if (genresFound)
			  sb.delete(sb.length() - 2, sb.length());
		  
		  rs.close();
		  
		  return sb.toString();
    }

	private String getStarsInMovieString(Connection dbcon, String m_ID)
			throws SQLException
	{
		String query = "SELECT first_name, last_name, photo_url, s.id FROM (movies m JOIN stars_in_movies sm "
		  		+ "NATURAL JOIN stars s "
		  		+ "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE m.id=" + m_ID;
		  
		  Statement statement = dbcon.createStatement();
		  ResultSet rs = statement.executeQuery(query);
		  
		  StringBuilder sb = new StringBuilder();
		  boolean starsFound = false;
		  while(rs.next())
		  {
			  starsFound = true;
			  String s_NA = rs.getString("first_name") + " " + rs.getString("last_name");
			  String s_ID = rs.getString("s.id");
			  sb.append("<a href='/Fabflix/StarInfo?starid=" + s_ID  + "'>" + s_NA + "</a>, ");
			  
		  }
		  
		  if (starsFound)
			  sb.delete(sb.length() - 2, sb.length());
		  
		  rs.close();
		  
		  return sb.toString();
	}

    private void createPaginator(PrintWriter out)
    		throws SQLException
    {
    	out.println("<div id='pageNavPosition'></div>");
    	out.println("<script type='text/javascript' src='/Fabflix/pagingUser.js'></script>");
    }
}