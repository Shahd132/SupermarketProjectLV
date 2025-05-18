import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class ReportApp extends JFrame {

    public ReportApp() {
        super("Supermarket Reports");
        setBounds(100, 100, 700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 0));
        headerPanel.setPreferredSize(new Dimension(700, 70));

        JLabel titleLabel = new JLabel("Report and Analytics");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 255, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;

        JButton btnMostBoughtProduct = createStyledButton("Most Bought Product");
        btnMostBoughtProduct.addActionListener(e -> showMostBoughtProduct());
        gbc.gridy = 0;
        panel.add(btnMostBoughtProduct, gbc);

        JButton btnProductNoCustomers = createStyledButton("Products Not Bought (Select Month)");
        btnProductNoCustomers.addActionListener(e -> showProductNoCustomers());
        gbc.gridy = 1;
        panel.add(btnProductNoCustomers, gbc);

        JButton btnInactiveCustomers = createStyledButton("Customers Inactive for 1 Year");
        btnInactiveCustomers.addActionListener(e -> showInactiveCustomers());
        gbc.gridy = 2;
        panel.add(btnInactiveCustomers, gbc);

        JButton btnTopCustomerThisMonth = createStyledButton("Top Customer This Month");
        btnTopCustomerThisMonth.addActionListener(e -> showTopCustomerThisMonth());
        gbc.gridy = 3;
        panel.add(btnTopCustomerThisMonth, gbc);

        JButton btnCompareSales = createStyledButton("Electronics vs Food Sales");
        btnCompareSales.addActionListener(e -> compareCategorySales());
        gbc.gridy = 4;
        panel.add(btnCompareSales, gbc);

        JButton btnProductCustomerStats = createStyledButton("Product Info & Customer Count");
        btnProductCustomerStats.addActionListener(e -> showProductCustomerStats());
        gbc.gridy = 5;
        panel.add(btnProductCustomerStats, gbc);
        JButton btnFrequentCustomers = createStyledButton("Frequent Customers");
        btnFrequentCustomers.addActionListener(e -> showFrequentCustomers());
        gbc.gridy = 6;
        panel.add(btnFrequentCustomers, gbc);

        JButton btnBackToDashboard = createStyledButton("Back to Admin Dashboard");
        btnBackToDashboard.addActionListener(e -> {
            dispose();
            new AdminDashboard().setVisible(true);
        });
        gbc.gridy = 7;
        panel.add(btnBackToDashboard, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(400, 55));
        button.setBackground(new Color(0, 102, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void createReportWindow(String title, ResultSet rs) throws SQLException {
        JFrame reportFrame = new JFrame(title);
        reportFrame.setSize(800, 700);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setLayout(new BorderLayout());

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = meta.getColumnLabel(i);
        }

        java.util.List<Object[]> data = new java.util.ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            data.add(row);
        }

        Object[][] rowData = new Object[data.size()][columnCount];
        for (int i = 0; i < data.size(); i++) {
            rowData[i] = data.get(i);
        }

        JTable table = new JTable(rowData, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(60);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnCount; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 102, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 18));

        JScrollPane scrollPane = new JScrollPane(table);
        reportFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 255, 250));

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setBackground(new Color(0, 102, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> reportFrame.dispose());

        bottomPanel.add(backButton);
        reportFrame.add(bottomPanel, BorderLayout.SOUTH);
        reportFrame.setVisible(true);
    }

    private void executeReportQuery(String query, String reportTitle) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;instanceName=MSSQLSERVER1;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true;");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            createReportWindow(reportTitle, rs);

        } catch (SQLException e) {
            showErrorDialog(e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, "Database Error:\n" + message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // a
    private void showMostBoughtProduct() {
        String query = "SELECT TOP 1 " +
                "p.PNAME AS [Product Name], " +
                "COUNT(DISTINCT o.CID) AS [Number of Customers] " +
                "FROM ORDERITEM oi " +
                "JOIN PRODUCT p ON oi.PRODUCTID = p.PRODUCTID " +
                "JOIN [ORDER] o ON oi.ORDERID = o.ORDERID " +
                "GROUP BY p.PNAME " +
                "ORDER BY [Number of Customers] DESC";

        executeReportQuery(query, "Most Bought Product (By Customers)");
    }

    // b
    private void showProductNoCustomers() {
        String monthInput = JOptionPane.showInputDialog(this, "Enter Month (1-12):");
        String yearInput = JOptionPane.showInputDialog(this, "Enter Year (e.g., 2025):");

        try {
            int month = Integer.parseInt(monthInput);
            int year = Integer.parseInt(yearInput);

            if (month < 1 || month > 12) {
                showErrorDialog("Invalid month entered. Please enter a number from 1 to 12.");
                return;
            }

            String query = "SELECT p.PNAME AS [Unbought Product Name] FROM PRODUCT p WHERE p.PRODUCTID NOT IN ( " +
                    "SELECT DISTINCT oi.PRODUCTID FROM ORDERITEM oi JOIN [ORDER] o ON oi.ORDERID = o.ORDERID " +
                    "WHERE YEAR(o.ORDERDATE) = " + year + " AND MONTH(o.ORDERDATE) = " + month + ")";

            executeReportQuery(query, "Products Not Bought in " + year + "/" + month);

        } catch (NumberFormatException e) {
            showErrorDialog("Invalid input. Please enter valid numbers.");
        }
    }

    // c
    private void showInactiveCustomers() {
        String query = "SELECT c.CName AS [Inactive Customer Names] FROM CUSTOMER c WHERE c.CID NOT IN ( " +
                "SELECT DISTINCT o.CID FROM [ORDER] o WHERE o.ORDERDATE >= DATEADD(YEAR, -1, GETDATE()))";
        executeReportQuery(query, "Customers Inactive for 1 Year");
    }

    // d
    private void showTopCustomerThisMonth() {
        String query = "SELECT TOP 1 c.CName AS [Customer Name], " +
                "SUM(oi.Quantity_Ordered * p.PRICE) AS [Total Purchase (EGP)] " +
                "FROM CUSTOMER c " +
                "JOIN [ORDER] o ON c.CID = o.CID " +
                "JOIN ORDERITEM oi ON o.ORDERID = oi.ORDERID " +
                "JOIN PRODUCT p ON oi.PRODUCTID = p.PRODUCTID " +
                "WHERE MONTH(o.ORDERDATE) = MONTH(GETDATE()) AND YEAR(o.ORDERDATE) = YEAR(GETDATE()) " +
                "GROUP BY c.CName " +
                "ORDER BY [Total Purchase (EGP)] DESC";
        executeReportQuery(query, "Top Customer This Month");
    }

    // e
    private void compareCategorySales() {
        String query = "SELECT c.CATEGNAME AS [Category Name], " +
                "SUM(oi.Quantity_Ordered * p.PRICE) AS [Total Sales (EGP)] " +
                "FROM PRODUCT p " +
                "JOIN ORDERITEM oi ON p.PRODUCTID = oi.PRODUCTID " +
                "JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID " +
                "GROUP BY c.CATEGNAME";
        executeReportQuery(query, "Sales by Product Category (Electronics vs Food)");
    }

   private void showFrequentCustomers() {
    String query = "SELECT " +
            "c.CID AS [Customer ID], " +
            "c.CName AS [Customer Name], " +
            "COUNT(o.ORDERID) AS [Number of Orders], " +
            "ISNULL(c.[DiscountVoucher], '') AS [Discount Voucher] " +
            "FROM CUSTOMER c " +
            "JOIN [ORDER] o ON c.CID = o.CID " +
            "GROUP BY c.CID, c.CName, c.[DiscountVoucher] " +
            "HAVING COUNT(o.ORDERID) >= 3 " +
            "ORDER BY [Number of Orders] DESC";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;instanceName=MSSQLSERVER1;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true;");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        JFrame frame = new JFrame("Frequent Customers (3+ Orders)");
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = meta.getColumnLabel(i);
        }

        java.util.List<Object[]> data = new java.util.ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            data.add(row);
        }

        Object[][] rowData = new Object[data.size()][columnCount];
        for (int i = 0; i < data.size(); i++) {
            rowData[i] = data.get(i);
        }

        JTable table = new JTable(rowData, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(50);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnCount; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 102, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 18));

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 255, 250));

        JButton discountButton = new JButton("Set Discount Voucher");
        discountButton.setPreferredSize(new Dimension(200, 40));
        discountButton.setBackground(new Color(0, 102, 0));
        discountButton.setForeground(Color.WHITE);
        discountButton.setFont(new Font("Arial", Font.BOLD, 16));
        discountButton.setFocusPainted(false);

        discountButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a customer row first.");
                return;
            }

            String cid = table.getValueAt(selectedRow, 0).toString();
            String cname = table.getValueAt(selectedRow, 1).toString();
            String discount = JOptionPane.showInputDialog(frame, "Enter discount code for " + cname + ":");

            if (discount != null && !discount.isEmpty()) {
                try (Connection updateConn = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:1433;instanceName=MSSQLSERVER1;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true;");
                     PreparedStatement updateStmt = updateConn.prepareStatement(
                             "UPDATE CUSTOMER SET [DiscountVoucher] = ? WHERE CID = ?")) {

                    updateStmt.setString(1, discount);
                    updateStmt.setInt(2, Integer.parseInt(cid));

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(frame, "Discount code updated for " + cname);
                        table.setValueAt(discount, selectedRow, 3);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update discount.");
                    }

                } catch (Exception ex) {
                    showErrorDialog(ex.getMessage());
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setBackground(new Color(0, 102, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> frame.dispose());

        bottomPanel.add(discountButton);
        bottomPanel.add(backButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

    } catch (SQLException e) {
        showErrorDialog(e.getMessage());
    }
}

    // f
    private void showProductCustomerStats() {
        String query = "SELECT " +
                "p.PRODUCTID AS [Product ID], " +
                "p.PNAME AS [Product Name], " +
                "p.PRICE AS [Price (EGP)], " +
                "c.Categname AS [Category Name], " +
                "COUNT(DISTINCT o.CID) AS [Number of Unique Customers] " +
                "FROM PRODUCT p " +
                "LEFT JOIN CATEGORY c ON p.CategoryId = c.CategoryId " +
                "LEFT JOIN ORDERITEM oi ON p.PRODUCTID = oi.PRODUCTID " +
                "LEFT JOIN [ORDER] o ON oi.ORDERID = o.ORDERID " +
                "GROUP BY p.PRODUCTID, p.PNAME, p.PRICE, c.Categname";
        executeReportQuery(query, "Product Customer Statistics");

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ReportApp window = new ReportApp();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
