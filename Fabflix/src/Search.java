import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class Browsing
 */
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Search() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pTitle = request.getParameter("title");
		if (pTitle.equals("")) {
			pTitle = null;
		}

		String pYear = request.getParameter("year");
		if (pYear.equals("")) {
			pYear = null;
		}

		String pDirector = request.getParameter("director");
		if (pDirector.equals("")) {
			pDirector = null;
		}

		String pStarName = request.getParameter("star_name");
		if (pStarName.equals("")) {
			pStarName = null;
		}

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			List<Movie> movies = getMoviesMatchingSearch(pTitle, pYear,
					pDirector, pStarName);

			request.setAttribute("movies", movies);	
			request.getRequestDispatcher("/MovieListPage").include(request, response);
		//	HttpSession session = request.getSession();
		
		//	view.forward(request, response);

		} catch (Exception e) {
			//out.println("An error occurred retrieving movies.");
			e.printStackTrace();
		}

		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private List<Movie> getMoviesMatchingSearch(String title, String year,
			String director, String starName) throws SQLException,
			NamingException {
		// the following few lines are for connection pooling
		// Obtain our environment naming context
		Context initCtx = new InitialContext();

		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		// Look up our data source
		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

		Connection dbcon = ds.getConnection();

		// Declare our statement
		String query = "SELECT DISTINCT m.* "
				+ "FROM movies AS m JOIN stars_in_movies AS sim JOIN stars AS s "
				+ "ON m.id = sim.movie_id AND s.id = sim.star_id "
				+ "WHERE (? IS NULL OR m.title LIKE ?) "
				+ "AND (? IS NULL OR m.year = ?) "
				+ "AND (? IS NULL OR m.director LIKE ?) "
				+ "AND ((? IS NULL AND ? IS NULL AND ? IS NULL) OR ((? IS NOT NULL AND ? IS NOT NULL) AND (s.first_name = ? AND s.last_name = ?)) OR (? IS NOT NULL AND (s.first_name = ? OR s.last_name = ?))) "
				+ "ORDER BY m.title;";
		PreparedStatement preparedStmt = dbcon.prepareStatement(query);

		preparedStmt.setString(1, title);
		preparedStmt.setString(2, '%' + title + '%');
		preparedStmt.setString(3, year);
		preparedStmt.setString(4, year);
		preparedStmt.setString(5, director);
		preparedStmt.setString(6, '%' + director + '%');

		String firstName = null;
		String lastName = null;
		String name = null;
		if (starName != null) {
			String[] nameParts = starName.split("\\s+");
			if (nameParts.length == 1) {
				name = nameParts[0];
			} else {
				firstName = nameParts[0];
				lastName = nameParts[1];
			}
		}

		preparedStmt.setString(7, firstName);
		preparedStmt.setString(8, lastName);
		preparedStmt.setString(9, name);
		preparedStmt.setString(10, firstName);
		preparedStmt.setString(11, lastName);
		preparedStmt.setString(12, firstName);
		preparedStmt.setString(13, lastName);
		preparedStmt.setString(14, name);
		preparedStmt.setString(15, name);
		preparedStmt.setString(16, name);

		// Perform the query
		preparedStmt.execute();
		ResultSet rs = preparedStmt.getResultSet();

		List<Movie> movies = new ArrayList<Movie>();
		while (rs.next()) {
			String mId = rs.getString("id");
			String mTitle = rs.getString("title");
			String mYear = rs.getString("year");
			String mDirector = rs.getString("director");
			String mBannerUrl = rs.getString("banner_url");
			String mTrailerUrl = rs.getString("trailer_url");

			Movie movie = new Movie();
			movie.setId(mId);
			movie.setTitle(mTitle);
			movie.setYear(mYear);
			movie.setDirector(mDirector);
			movie.setBannerUrl(mBannerUrl);
			movie.setTrailerUrl(mTrailerUrl);

			movies.add(movie);
			//	System.out.println("Added!");
		}
		
		
		rs.close();
		preparedStmt.close();
		dbcon.close();

		return movies;
	}
}
