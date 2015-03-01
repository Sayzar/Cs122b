import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

/**
 * Servlet implementation class MovieInfo
 */
public class MovieDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public String getServletInfo() {
		return "Servlet connects to MySQL database and displays result of a SELECT";
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html"); // Response mime type

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		out.println("<div>");

		out.println("<table><tr>");
		try {
			// Class.forName("org.gjt.mm.mysql.Driver");
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
			
			while (movieRS.next()) {
				String m_Dir = movieRS.getString("director");
				String m_Title = movieRS.getString("title");
				movie = m_Title;
				String m_Year = movieRS.getString("year");
				String m_Photo = movieRS.getString("banner_url");
				String m_Trailer = movieRS.getString("trailer_url");

				// SELECT * FROM (movies m JOIN stars_in_movies sm
				// NATURAL JOIN stars s ON m.id = sm.movie_id AND sm.star_id =
				// s.id) WHERE first_name=?

				out.println("<td>");
				out.println("<img src= \"" + m_Photo + "\" alt=\"" + m_Title + " Banner\" " + "width =\"64\"> <br/>");
				out.println("</td>");
				out.println("<td>");
				out.println("<strong> Movie Title: </strong>" + m_Title + "<br/>"
						+ "<strong> Director: </strong>" + m_Dir + "<br/>" 
						+ "<strong> Year: </strong>" + m_Year + "<br/>"
						+ "<strong> Trailer Link: </strong> <a href = \"" + m_Trailer + "\"> Link </a>");
				out.println("</td>");
			}
			
			String genrequery = "SELECT name FROM (genres g JOIN genres_in_movies gm " + "ON g.id = gm.genre_id) WHERE gm.movie_id= ?";
			PreparedStatement genrePrep = dbcon.prepareStatement(genrequery);
			genrePrep.setString(1, movieId);
			ResultSet genreRs = genrePrep.executeQuery();
			
			out.println("<td>");
			out.println("<strong>Genres</strong> <br/>");

			out.println("<table border='1' style='border-collapse:collapse;'>");
			while (genreRs.next()) {
				String g_nm = genreRs.getString("name");
				out.println("<tr><td>" + g_nm + "</td></tr>");
			}
			out.println("</table>");
			out.println("<br/>");

			String starsQuery = "SELECT first_name, last_name, s.id FROM (movies m JOIN stars_in_movies sm " + "NATURAL JOIN stars s "
					+ "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE m.id=?";
			PreparedStatement starsPs = dbcon.prepareStatement(starsQuery);
			starsPs.setString(1, movieId);
			ResultSet starsRS = starsPs.executeQuery();

			out.println("<strong>Stars In Movie</strong> <br/>");
			out.println("<TABLE border='1'  style='border-collapse:collapse;'>");
			out.println("<tr><td> ID </td> <td> First Name</td> <td> Last Name </td> <td> Star Page</td> </tr>");
			while (starsRS.next()) {
				String s_first = starsRS.getString("first_name");
				String s_last = starsRS.getString("last_name");
				String s_id = starsRS.getString("s.id");
				out.println("<tr>" + "<td>" + s_id + "</td>" + "<td>" + s_first + "</td>" + "<td>" + s_last + "</td>"
						+ "<td><a href = \"/Fabflix/StarInfo?starid=" + s_id + "\"> Link </a> </td>" + " </tr>");
			}
			out.println("</table>");
			out.println("</td>");
			out.println("</tr></table>");
			out.println("</div>");
			starsRS.close();
			movieRS.close();
			statement.close();
			dbcon.close();

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception:  " + ex.getMessage());
				ex = ex.getNextException();
			} // end while
		} // end catch SQLException

		catch (java.lang.Exception ex) {
			out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>" + "<P>SQL error in doGet: " + ex.getMessage()
					+ "</P></BODY></HTML>");
			return;
		}

		out.close();
	}

}
