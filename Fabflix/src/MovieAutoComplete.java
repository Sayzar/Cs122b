import org.json.JSONArray;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Cesar Ramirez on 2/28/2015.
 */
public class MovieAutoComplete extends HttpServlet{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        JSONArray jarray = null;

        String term = request.getParameter("term");
        try{
            ArrayList<String> suggestions = getSuggestions(term);
            jarray = new JSONArray(suggestions);

            out.println(jarray.toString());
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getSuggestions(String term) throws SQLException, NamingException
    {
        Context initCtx = new InitialContext();

        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        // Look up our data source
        DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

        Connection dbcon = ds.getConnection();

        ArrayList<String> suggestions = new ArrayList<String>();
        String query = "SELECT title FROM movies WHERE (title LIKE ? AND title LIKE ?) OR title = ? ORDER BY title";
       //replace whitespace
        StringTokenizer tokenizer = new StringTokenizer(term);
        String firstToken = tokenizer.nextToken();
        term.replaceAll("\\s","%");

        PreparedStatement prepStmnt = dbcon.prepareStatement(query);
        prepStmnt.setString(1, firstToken+'%');
        prepStmnt.setString(2, term+'%');
        prepStmnt.setString(3, term);

        ResultSet rs = prepStmnt.executeQuery();

        while(rs.next())
        {
            suggestions.add(rs.getString("title"));
        }

        return suggestions;
    }

}
