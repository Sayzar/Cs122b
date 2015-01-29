<%@page
	import="java.sql.*,
 javax.sql.*,
 java.io.IOException,
 javax.servlet.http.*,
 javax.servlet.*,
 javax.naming.*,
 java.util.*"%>
<html>
<head>
</head>
<body>
	<h3>Browse By Movie Genre</h3>
	<p><a href="/Fabflix/ShoppingCart">Shopping Cart</a></p>
	<p><a href="javascript:history.back()">Back</a></p>
	<p><a href="/Fabflix/main.html">Home</a></p>
	<div style="display: table-cell;">
		<%
			try {
				// the following few lines are for connection pooling
				// Obtain our environment naming context
				Context initCtx = new InitialContext();
 
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				// Look up our data source
				DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

				Connection dbcon = ds.getConnection();

				// Declare our statement
				Statement statement = dbcon.createStatement();
				String query = "SELECT genres.name FROM genres ORDER BY genres.name;";

				// Perform the query
				ResultSet rs = statement.executeQuery(query);

				out.println("<ul>");

				// Iterate through each row of rs
				while (rs.next()) {
					String genreName = rs.getString("name");
					
					out.println("<li>");
					out.println("<a href='/Fabflix/BrowseByMovieGenre?genre="
							+ genreName + "'>" + genreName + "</a>");
					out.println("</li>");
				}

				out.println("</ul>");

				rs.close();
				statement.close();
				dbcon.close();
			} catch (Exception e) {
				out.println("Unable to display genres.");
				e.printStackTrace();
			}
		%>
	</div>
</body>
</html>

