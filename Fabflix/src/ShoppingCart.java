

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ShoppingCart
 */
public class ShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */ 
    public ShoppingCart() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Cart shoppingCart;
        shoppingCart = (Cart) session.getAttribute("cart");
        if(shoppingCart == null){
          shoppingCart = new Cart();
          session.setAttribute("cart", shoppingCart);
        }
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>result</title>");            
        out.println("</head>");
        out.println("<body ALIGN=\"CENTER\" BGCOLOR=\"#FDF5E6\">");
        out.println("<h1>Shopping Cart</h1>");
        if(shoppingCart.getCartItems().isEmpty() && request.getParameter("title") == null)
        {
        	out.println("<p>There is nothing in your cart.</p>");
        }
        
        else{
        
        	if(request.getParameter("title") != null && request.getParameter("price") != null){
                String name = request.getParameter("title");
                Integer price = Integer.parseInt(request.getParameter("price"));
                shoppingCart.addToCart(name, price);
                session.setAttribute("cart", shoppingCart);
                }
        	session.setAttribute("cart", shoppingCart);
        try {
            /* TODO output your page here. You may use following sample code. */
           
         //   out.println("<form action='index.html'>Add One More Movie   <input type='submit' value='Add'></form>");
            out.println("<hr>");
            out.println("<h2>Cart</h2>");
            HashMap<String, Integer> items = shoppingCart.getCartItems();
            out.println("<table ALIGN=\"CENTER\" border='1px'>");
             
            out.println("<tr><td>Movie Title </td> <td>Price</td> <td>Quantity </td> <td>Increase</td> <td>Decrease</td> <td>Remove</td></tr>");
            int sum = 0;
            for(String key: items.keySet()){
                out.println("<form action='DeleteItem'><input type='hidden' name='name' value='"+key+"'>"+
                            "<tr><td>"+key+" - </td><td>"+"$"+items.get(key)+"</td><td>" + items.get(key)/15 +"</td><td>"+
                             "<input type = 'number' name = 'quant' value = '1' > <input type = 'submit' value = 'Increase' name= 'action'></td>"
                             + "<td><input type = 'submit' value = 'Decrease' name='action'> </td>"
                             + "<td><input type='submit' value='Remove' name='action'></td></tr></form>");
                sum += items.get(key);
            }
            out.println("<tr><td></td><td></td><td></td><td></td> <td>Total Price</td> <td>$"+sum+".00</td> </tr>");
             
            
            
             
        }catch(java.lang.Exception ex)
        {
            out.println("<HTML>" +
                        "<HEAD><TITLE>" +
                        "MovieDB: Error" +
                        "</TITLE></HEAD>\n<BODY>" +
                        "<P>SQL error in doGet: " +
                        ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
    }
        out.println("<p><a href=\"/Fabflix/main.html\">Home</a></p>"); 
        out.println("<p><a href=\"javascript:history.back()\">Back</a></p>");
        out.println("<table>");
        if(!(shoppingCart.getCartItems().isEmpty() && request.getParameter("title") == null))
        	out.println("<form action='Checkout.html'><input type='submit' value='Checkout'></form>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

}
