<%@page
	import="java.sql.*,
 javax.sql.*,
 java.io.IOException,
 javax.servlet.http.*,
 javax.servlet.*,
 javax.naming.*"%>
<html>
<head>
<title>Browse - Fabflix</title>
</head>
<body BGCOLOR="#FDF5E6">
	<h3>Browse Movies</h3>
	<% String option = request.getParameter("option"); %>
	<% if(option != null && option.equals("title")){ %>
		<jsp:include page="BrowseByMovieTitle.jsp" />
	<% } else if(option != null && option.equals("genre")){ %>
		<jsp:include page="BrowseByMovieGenre.jsp" />
	<% } else { %>
		<a href="/Fabflix/Browsing.jsp?option=title">Browse By Title</a>
		<a href="/Fabflix/Browsing.jsp?option=genre">Browse By Genre</a>	
		<br> </br>
		
	<p><a href="/Fabflix/ShoppingCart">Shopping Cart</a></p>
	<p><a href="javascript:history.back()">Back</a></p>
	<p><a href="/Fabflix/main.html">Home</a></p>
	<% } %>
</body>
</html>

