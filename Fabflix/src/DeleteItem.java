

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DeleteItem
 */
public class DeleteItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String name = request.getParameter("name");
        HttpSession session = request.getSession();
        Cart shoppingCart;
        shoppingCart = (Cart) session.getAttribute("cart");
        String action = request.getParameter("action");
        if(action.equals("Remove"))
        	shoppingCart.deleteFromCart(name);
        else if(action.equals("Decrease"))
        	shoppingCart.decFromCart(name);
        else if(action.equals("Increase"))
        {
        	String quantity = request.getParameter("quant");
        	shoppingCart.increaseQuantity(name, 15, quantity);
        }
        session.setAttribute("cart", shoppingCart);
        shoppingCart = (Cart) session.getAttribute("cart");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Delete Item</title>");            
            out.println("</head>");
            out.println("<body ALIGN=\"CENTER\" BGCOLOR=\"#FDF5E6\">");
            out.println("<p> Successfully updated cart!</p>");
            HashMap<String, Integer> items = shoppingCart.getCartItems();
            out.println("<table ALIGN=\"CENTER\" border='1px'>");
            
            out.println("<p> Items Still In Your Cart:");
            for(String key: items.keySet()){
                out.println("<tr><td>"+key+" - </td><td>"+"$"+items.get(key)+"</td><td>" + items.get(key)/15 +"</td></tr></form>");
            }
            
            out.println("<p><a href=\"/Fabflix/main.html\">Home</a></p>");
            out.println("<p><a href=\"/Fabflix/ShoppingCart\">Back to Shopping Cart</a></p>");
            out.println("<table>"); 
            out.println("</body>");
            out.println("</html>");
        }
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
         out.close();
    }
	

}
