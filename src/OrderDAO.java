import java.sql.*;

import java.sql.SQLException;



public class OrderDAO {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException {

        return DriverManager.getConnection(DB_URL);

    }



    public int insertOrder(Order order) throws SQLException {

        int cid = order.getCustomerId();

        System.out.println("Inserting order for customer ID: " + cid);

        if (cid <= 0) {

            throw new IllegalArgumentException("Invalid customer ID: " + cid);

        }



        String sql = "INSERT INTO [ORDER] (CID, ORDERDATE, PAYMENTMETHOD, ORDERSTATUS, BUILDING_NO, STREETNAME, AREANAME) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (

            Connection conn = connect();

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

        ){

            stmt.setInt(1, cid);

            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // Use current timestamp

            stmt.setString(3, order.getPaymentMethod());

            stmt.setString(4, order.getStatus());

            stmt.setInt(5, 1); 

            stmt.setString(6, "Main St"); 

            stmt.setString(7, "Downtown"); 

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {

                    int generatedId = rs.getInt(1);

                    System.out.println("Inserted order ID: " + generatedId);

                    return generatedId;

                }

            }

        }

        return -1; 

    }



    public void insertOrderItem(OrderItem item) throws SQLException {

        String sql = "INSERT INTO ORDERITEM (ORDERID, PRODUCTID, QUANTITY_ORDERED) VALUES (?, ?, ?)";

        try (

            Connection conn = connect();

            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, item.getOrderId());

            stmt.setInt(2, item.getProductId());

            stmt.setInt(3, item.getQuantity());

            stmt.executeUpdate();



            System.out.println("Inserted order item: OrderID=" + item.getOrderId() + ", ProductID=" + item.getProductId() +", Quantity=" + item.getQuantity());



        }

    }

}
