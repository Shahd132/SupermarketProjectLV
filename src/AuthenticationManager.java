import java.sql.*;

public class AuthenticationManager 
{

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
    private Connection connect() throws SQLException 
    {
        return DriverManager.getConnection(DB_URL);
    }

    public boolean authenticateAdmin(String email, String password) 
    {
        String sql = "SELECT * FROM Admin WHERE AEmail = ? AND APassword = ?"; 

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticateCustomer(String email, String password) 
    {
        String sql = "SELECT * FROM Customer WHERE CEmail = ? AND CPassword = ?"; 

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerCustomer(Customer customer) 
    {
        String sql = "INSERT INTO Customer (CName, CEmail, PhoneNo, CAddress, CPassword, RegistrationDate, DiscountVoucher) VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhoneNo());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getPassword()); 
            stmt.setString(6, customer.getRegistrationDate());
            stmt.setString(7, customer.getDiscountVoucher());
            return stmt.executeUpdate() > 0; 
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean emailExists(String email) 
    {
        String sql = "SELECT 1 FROM Customer WHERE CEmail = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean adminemailExists(String email) 
    {
        String sql = "SELECT 1 FROM Admin WHERE AEmail = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            System.out.println("email found");
            return rs.next(); 
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean phoneExists(String phoneNo) 
    {
        String sql = "SELECT 1 FROM Customer WHERE PhoneNo = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, phoneNo);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    public String determineRole(String email, String password) 
    {
        if (authenticateAdmin(email, password)) 
        {
            return "admin"; 
        } else if (authenticateCustomer(email, password)) 
        {
            return "customer"; 
        }
        return "unknown"; 
    }
    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM Customer WHERE CEmail = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("CID"); 
                String name = rs.getString("CName");
                String phoneNo = rs.getString("PhoneNo");
                String address = rs.getString("CAddress");
                String password = rs.getString("CPassword");
                String registrationDate = rs.getString("RegistrationDate");
                String discountVoucher = rs.getString("DiscountVoucher");

                Customer customer = new Customer(name, email, phoneNo, address, password);
                customer.setId(id);  // Set the ID here!
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
