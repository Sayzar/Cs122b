import java.util.HashMap;


public class Cart {
    HashMap<String, Integer> cartItems;
    public Cart(){
     cartItems = new HashMap<String, Integer>();
      
    }
    public HashMap<String, Integer> getCartItems(){
        return cartItems;
    }
    public void addToCart(String itemId, int price){
       if( cartItems.put(itemId, price) != null){
    	   int newPrice = price + cartItems.get(itemId);
    	   cartItems.put(itemId, newPrice);
       }
    }
    public void increaseQuantity(String itemId, int price, String quantity)
    {
    	int quant = Integer.parseInt(quantity);
    	cartItems.put(itemId,price*quant);
    }
    
    public void deleteFromCart(String itemId)
    {
    	cartItems.remove(itemId);
    }
    
    public void decFromCart(String itemId){
    	int price = cartItems.get(itemId);
    	if(price == 15)
    		cartItems.remove(itemId);
    	else
    		cartItems.put(itemId, price-15);
    }
}