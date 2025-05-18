import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT CID, CNAME, CEMAIL, CPASSWORD, PHONENO, CADDRESS, REGISTRATIONDATE, DISCOUNTVOUCHER FROM CUSTOMER";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("CID"));
                customer.setName(rs.getString("CNAME"));
                customer.setEmail(rs.getString("CEMAIL"));
                customer.setPassword(rs.getString("CPASSWORD"));
                customer.setphone(rs.getString("PHONENO"));
                customer.setAddress(rs.getString("CADDRESS"));
                customer.setDate(rs.getString("REGISTRATIONDATE"));
                customer.setDiscountVoucher(rs.getString("DISCOUNTVOUCHER"));
                customers.add(customer);
            }
        }

        return customers;
    }

    public List<Customer> searchCustomers(String name) throws SQLException {
    List<Customer> customers = new ArrayList<>();
    String sql = """
        SELECT CID, CNAME, CEMAIL, PHONENO, CADDRESS
        FROM CUSTOMER
        WHERE CNAME LIKE ?
    """;

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + name + "%");

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("CID");
                String customerName = rs.getString("CNAME");
                String email = rs.getString("CEMAIL");
                String phone = rs.getString("PHONENO");
                String address = rs.getString("CADDRESS");

                customers.add(new Customer(customerName, email, phone, address,""));
            }
        } 
    }
    return customers;
}


    public void insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO CUSTOMER (CNAME, CEMAIL, CPASSWORD, PHONENO, CADDRESS, REGISTRATIONDATE) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.setString(4, customer.getPhoneNo());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getRegistrationDate());
            stmt.executeUpdate();
        }
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE CUSTOMER SET CNAME = ?, CEMAIL = ?, CPASSWORD = ?, PHONENO = ?, CADDRESS = ?, DISCOUNTVOUCHER = ? WHERE CID = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.setString(4, customer.getPhoneNo());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getDiscountVoucher());
            stmt.setInt(7, customer.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM CUSTOMER WHERE CID = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }
    public void updateCustomerProfile(Customer customer) throws SQLException {
        String sql = "UPDATE CUSTOMER SET CNAME = ?, CEMAIL = ?, PHONENO = ?, CADDRESS = ? WHERE CID = ?";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhoneNo());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getId());

            stmt.executeUpdate();
        }
    }
}
