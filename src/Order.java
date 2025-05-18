public class Order {

    private int orderId;
    private int customerId;
    private String paymentMethod;
    private String status;
    private double totalPrice;

    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }

}
