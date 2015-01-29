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
public class BrowseByMovieGenre extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BrowseByMovieGenre() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String genre = request.getParameter("genre");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		if (genre == null) {
			out.println("No genre selected.");
		} else {
			try {
				List<Movie> movies = getMoviesWithGenre(genre);

				request.setAttribute("movies", movies);

				RequestDispatcher view = request
						.getRequestDispatcher("/MovieListPage");
				view.forward(request, response);
			} catch (Exception e) {
				out.println("An error occurred retrieving movies.");
				e.printStackTrace();
			}
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

	private List<Movie> getMoviesWithGenre(String genre) throws SQLException,
			NamingException {
		// the following few lines are for connection pooling
		// Obtain our environment naming context
		Context initCtx = new InitialContext();

		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		// Look up our data source
		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

		Connection dbcon = ds.getConnection();

		// Declare our statement
		String query = "SELECT m.* "
				+ "FROM movies AS m, genres AS g, genres_in_movies AS gim "
				+ "WHERE g.name = ? AND m.id = gim.movie_id AND gim.genre_id = g.id "
				+ "ORDER BY m.title;";
		PreparedStatement preparedStmt = dbcon.prepareStatement(query);

		preparedStmt.setString(1, genre); // Perform the query
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
		}

		rs.close();
		preparedStmt.close();
		dbcon.close();

		return movies;
	}
}
