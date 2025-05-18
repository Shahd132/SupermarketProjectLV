public class Product 
{
    private int ProductId;
    private String Name;
    private String Category;
    private double Price;
    private int CategoryId;
    private int Quantity;

    public Product(int id, String name, String category, double price) 
    {
        this.ProductId = id;
        this.Name = name;
        this.Category = category;
        this.Price = price;
        
    }

    public int getId() 
    {
        return ProductId;
    }

    public void setId(int productId) 
    {
        this.ProductId = productId;
    }

    public String getName() 
    {
        return Name;
    }

    public void setName(String name) 
    {
        this.Name = name;
    }

    public String getCategoryName() 
    {
        return Category;
    }

    public void setCategoryName(String category) 
    {
        this.Category = category;
    }

    public double getPrice() 
    {
        return Price;
    }

    public void setPrice(double price) 
    {
        this.Price = price;
    }
    public void setCategoryId(int categoryid)
    {
        this.CategoryId=categoryid;
    }
    public int getCategoryId()
    {
        return CategoryId;
    }
    public int getQuantity()
    {
        return Quantity;
    }
    public void setQuantity(int quantity)
    {
        this.Quantity=quantity;
    }
}

