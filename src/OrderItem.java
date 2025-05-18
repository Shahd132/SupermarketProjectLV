public class OrderItem {
    private int orderId;
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    
    public OrderItem(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
    
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}