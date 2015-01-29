
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

public class LoginPage extends HttpServlet
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
        response.setContentType("text/html");
        
        String username, password;
        username = request.getParameter("username");
        password = request.getParameter("password");
        
   		HttpSession mySession = request.getSession();
        
        try
        {
        	  Context initCtx = new InitialContext();

	      		Context envCtx = (Context) initCtx.lookup("java:comp/env");
	      		// Look up our data source
	      		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

	      		Connection dbcon = ds.getConnection();
        	Statement statement = dbcon.createStatement();
        	
        	String query = "SELECT * FROM customers WHERE email='" + username + "'";
        	
        	ResultSet rs = statement.executeQuery(query);
        	
        	User thisUser = new User();
        	
        	while (rs.next())
        	{
        		thisUser.id = rs.getString("id");
        		thisUser.firstName = rs.getString("first_name");
        		thisUser.lastName = rs.getString("last_name");
        		thisUser.email = rs.getString("email");
        		thisUser.password = rs.getString("password");
        		break;
        	}
        	
        	if (password.equals(thisUser.password))
        	{
        		System.out.println("LOGIN SUCCESSFUL");
        		mySession.setAttribute("user_object", thisUser);

        		// Used to test movie list.
        		generateTestSearchResults(request, dbcon);

        		// This is how you get to the movie list. Probably change this to go to the main page.
        		RequestDispatcher view = request.getRequestDispatcher("/main.html");
        		view.forward(request, response);
        	}
        	else
        	{
        		System.out.println("Invalid login.");
        		if ("".equals(thisUser.password))
        		{
        			mySession.setAttribute("errorMessage", "Your username is not found.");
        		}
        		else
        		{
        			mySession.setAttribute("errorMessage", "The password is wrong.");
        		}
        		
        		doGet(request, response);
        	}
        	
        	rs.close();
        	statement.close();
        	dbcon.close();
        }
        catch (SQLException ex)
        {
        	ex.printStackTrace();
//        	while (ex != null)
//        	{
//        		
//        	}
        } catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void generateTestSearchResults(HttpServletRequest request,
			Connection dbcon) throws SQLException {
		Statement statement2 = dbcon.createStatement();
		
		String query2 = "SELECT * FROM movies";
		ResultSet rs2 = statement2.executeQuery(query2);
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		while (rs2.next())
		{
			Movie curMovie = new Movie();
			curMovie.setId(rs2.getString("id"));
			curMovie.setTitle(rs2.getString("title"));
			curMovie.setBannerUrl(rs2.getString("banner_url"));
			curMovie.setYear(rs2.getString("year"));
			curMovie.setDirector(rs2.getString("director"));
			movieList.add(curMovie);
		}

			request.setAttribute("search_results", movieList);
	}

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
    	HttpSession mySession = request.getSession();
    	User thisUser = (User)mySession.getAttribute("user_object");
    	String errorMessage = (String)mySession.getAttribute("errorMessage");
    	// If we're already logged in, we don't need to be at the login page.
    	if (thisUser != null)
    	{
        	// Used to test movie list.
    		try
    		{
    			  Context initCtx = new InitialContext();

  	      		Context envCtx = (Context) initCtx.lookup("java:comp/env");
  	      		// Look up our data source
  	      		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

  	      		Connection dbcon = ds.getConnection();
    			generateTestSearchResults(request, dbcon);
        	
    			dbcon.close();
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}

       	 	// TODO: This is how you get to the movie list. Probably change this to go to the main page.
       		RequestDispatcher view = request.getRequestDispatcher("/movieList.html");
       		view.forward(request, response);

    		return;
    	}
    	else if (errorMessage != null)
    	{
    		mySession.setAttribute("errorMessage", null);

            RequestDispatcher view = request.getRequestDispatcher("/loginError.html");
            view.forward(request, response);
    	}
    	else
    	{
    		RequestDispatcher view = request.getRequestDispatcher("/index.html");
    		view.forward(request, response);
    	}
    } 
}