import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO 
{

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException 
    {
        return DriverManager.getConnection(DB_URL);
    }

    public List<Product> getAllProducts() throws SQLException 
    {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.PRODUCTID, p.PNAME, p.PRICE, p.QUANTITY, c.CATEGNAME, c.CATEGORYID
            FROM PRODUCT p
            JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("PRODUCTID");
                String name = rs.getString("PNAME");
                double price = rs.getDouble("PRICE");
                int quantity = rs.getInt("QUANTITY");
                int categoryId = rs.getInt("CATEGORYID");
                String categoryName = rs.getString("CATEGNAME");

                products.add(new Product(id, name, categoryName, price));
            }
        }

        return products;
    }

    public List<Product> searchProducts(String name) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.PRODUCTID, p.PNAME, p.PRICE, p.QUANTITY, c.CATEGNAME, c.CATEGORYID
            FROM PRODUCT p
            JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID
            WHERE p.PNAME LIKE ?
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("PRODUCTID");
                    String pname = rs.getString("PNAME");
                    double price = rs.getDouble("PRICE");
                    int quantity = rs.getInt("QUANTITY");
                    int categoryId = rs.getInt("CATEGORYID");
                    String categoryName = rs.getString("CATEGNAME");

                    products.add(new Product(id, pname, categoryName, price));
                }
            }
        }
        return products;
    }
    private int getCategoryIdByName(String categoryName) throws SQLException 
    {
    String sql = "SELECT CATEGORYID FROM CATEGORY WHERE CATEGNAME = ?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, categoryName);
        try (ResultSet rs = stmt.executeQuery()) 
        {
            if (rs.next()) 
            {
                return rs.getInt("CATEGORYID");
            } 
            else 
            {
                throw new SQLException("Category not found: " + categoryName);
            }
        }
    }
}


    public void insertProduct(Product product) throws SQLException 
    {
    int categoryId = getCategoryIdByName(product.getCategoryName()); 

    String sql = "INSERT INTO PRODUCT (PNAME, CATEGORYID, PRICE, QUANTITY) VALUES (?, ?, ?, ?)";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, product.getName());
        stmt.setInt(2, categoryId);
        stmt.setDouble(3, product.getPrice());
        stmt.setInt(4, product.getQuantity());
        stmt.executeUpdate();
    }
}


    public void updateProduct(Product product) throws SQLException 
    {
    int categoryId = getCategoryIdByName(product.getCategoryName()); 

    String sql = "UPDATE PRODUCT SET PNAME = ?, CATEGORYID = ?, PRICE = ?, QUANTITY = ? WHERE PRODUCTID = ?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, product.getName());
        stmt.setInt(2, categoryId);
        stmt.setDouble(3, product.getPrice());
        stmt.setInt(4, product.getQuantity());
        stmt.setInt(5, product.getId());
        stmt.executeUpdate();
        }
   }


    public void deleteProduct(int productId) throws SQLException
     {
        String sql = "DELETE FROM PRODUCT WHERE PRODUCTID = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    public List<Product> getProductsByCategory(int categoryId) throws SQLException {
    List<Product> products = new ArrayList<>();
    String sql = "SELECT P.PRODUCTID, P.PNAME, C.CATEGNAME, P.PRICE, P.QUANTITY, P.CATEGORYID " +
                 "FROM PRODUCT P JOIN CATEGORY C ON P.CATEGORYID = C.CATEGORYID " +
                 "WHERE P.CATEGORYID = ?";
    try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, categoryId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("PRODUCTID"),
                    rs.getString("PNAME"),
                    rs.getString("CATEGNAME"), // category name
                    rs.getDouble("PRICE")
                   
                );
                p.setCategoryId(rs.getInt("CATEGORYID")); // explicitly set categoryId too
                products.add(p);
            }
        }
    }
    return products;
}

public List<Product> getProductsByBranch(int branchId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.PRODUCTID, p.PNAME, c.CATEGNAME, p.PRICE, s.QUANTITY
            FROM PRODUCT p
            JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID
            JOIN STOCK s ON p.PRODUCTID = s.PRODUCTID
            WHERE s.BRANCHID = ?
        """;

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("PRODUCTID"),
                    rs.getString("PNAME"),
                    rs.getString("CATEGNAME"),
                    rs.getDouble("PRICE")
                );
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> searchProductsByBranch(String name, int branchId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.PRODUCTID, p.PNAME, p.PRICE, s.QUANTITY, c.CATEGNAME, c.CATEGORYID
            FROM PRODUCT p
            JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID
            JOIN STOCK s ON p.PRODUCTID = s.PRODUCTID
            WHERE s.BRANCHID = ? AND p.PNAME LIKE ?
        """;

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setString(2, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("PRODUCTID");
                    String pname = rs.getString("PNAME");
                    double price = rs.getDouble("PRICE");
                    int quantity = rs.getInt("QUANTITY");
                    String categoryName = rs.getString("CATEGNAME");

                    products.add(new Product(id, pname, categoryName, price));
                }
            }
        }
        return products;
    }


}


