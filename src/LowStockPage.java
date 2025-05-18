import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class LowStockPage extends JFrame 
{

    private JTable table;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    public LowStockPage() {
        setTitle("Low Stock Alert");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 255, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 0));
        JLabel heading = new JLabel("Products That Need Restocking", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(heading);

        String[] columnNames = {"Product ID", "Product Name", "Price", "Quantity", "Min Required", "Branch"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);

       table.getTableHeader().setBackground(new Color(0, 102, 0));  
       table.getTableHeader().setForeground(Color.WHITE);
       table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
       table.getTableHeader().setOpaque(true);

JScrollPane scrollPane = new JScrollPane(table);
scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // Back button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 255, 245));
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(34, 139, 34));
        backButton.setFocusPainted(false);
        backButton.setBorder(null);
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });
        bottomPanel.add(backButton);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadLowStockData();
        setVisible(true);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void loadLowStockData() {
        String query = """
            SELECT 
                P.PRODUCTID,
                P.PNAME,
                P.PRICE,
                S.QUANTITY,
                S.MINREQUIRED,
                B.BNAME
            FROM STOCK S
            JOIN PRODUCT P ON S.PRODUCTID = P.PRODUCTID
            JOIN BRANCH B ON S.BRANCHID = B.BRANCHID
            WHERE S.QUANTITY < S.MINREQUIRED
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("PRODUCTID"));
                row.add(rs.getString("PNAME"));
                row.add(rs.getDouble("PRICE"));
                row.add(rs.getInt("QUANTITY"));
                row.add(rs.getInt("MINREQUIRED"));
                row.add(rs.getString("BNAME"));
                tableModel.addRow(row);
            }

        } 
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading low stock data:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}



