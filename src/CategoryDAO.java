import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException 
    {
        return DriverManager.getConnection(DB_URL);
    }
    public List<Category> searchCategories(String name) throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORY WHERE CATEGNAME LIKE ?";
        try (Connection conn = connect();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("CATEGORYID"),
                        rs.getString("CATEGNAME"),
                        rs.getString("DESCRIPTION")
                ));
            }
        }
        return list;
    }
    // Get all categories
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT CATEGORYID, CATEGNAME, DESCRIPTION FROM CATEGORY";
        try (Connection conn = connect();Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("CATEGORYID"),
                    rs.getString("CATEGNAME"),
                    rs.getString("DESCRIPTION")
                ));
            }
        }
        return categories;
    }

    // Insert new category
    public void insertCategory(Category category) throws SQLException {
        String sql = "INSERT INTO CATEGORY (CATEGNAME, DESCRIPTION) VALUES (?, ?)";
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.executeUpdate();
        }
    }

    // Update existing category
    public void updateCategory(Category category) throws SQLException {
        String sql = "UPDATE CATEGORY SET CATEGNAME = ?, DESCRIPTION = ? WHERE CATEGORYID = ?";
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());
            ps.executeUpdate();
        }
    }

    // Delete category by ID
    public void deleteCategory(int categoryId) throws SQLException {
        String sql = "DELETE FROM CATEGORY WHERE CATEGORYID = ?";
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.executeUpdate();
        }
    }

    // Get a category by ID
    public Category getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT CATEGORYID, CATEGNAME, DESCRIPTION FROM CATEGORY WHERE CATEGORYID = ?";
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                        rs.getInt("CATEGORYID"),
                        rs.getString("CATEGNAME"),
                        rs.getString("DESCRIPTION")
                    );
                }
            }
        }
        return null; // Not found
    }
}

