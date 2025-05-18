public class Stock {
    private int stockId;
    private int branchId;
    private int productId;
    private int quantity;
    private int minRequired;
    private java.util.Date lastUpdated;

    public Stock(int stockId, int branchId, int productId, int quantity, int minRequired, java.util.Date lastUpdated) {
        this.stockId = stockId;
        this.branchId = branchId;
        this.productId = productId;
        this.quantity = quantity;
        this.minRequired = minRequired;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getStockId() { return stockId; }
    public int getBranchId() { return branchId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public int getMinRequired() { return minRequired; }
    public java.util.Date getLastUpdated() { return lastUpdated; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public void setBranchId(int branchId) { this.branchId = branchId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setMinRequired(int minRequired) { this.minRequired = minRequired; }
    public void setLastUpdated(java.util.Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
