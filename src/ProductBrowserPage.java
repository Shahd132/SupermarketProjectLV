
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ProductBrowserPage extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private Cart cart = new Cart();

    public ProductBrowserPage() {
        setTitle("Browse Products");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Quantity"}, 0);
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        JButton addToCartBtn = new JButton("Add to Cart");
        JButton viewCartBtn = new JButton("View Cart");

        addToCartBtn.addActionListener(e -> addToCart());
        viewCartBtn.addActionListener(e -> new CartPage(cart).setVisible(true));

        JPanel panel = new JPanel();
        panel.add(addToCartBtn);
        panel.add(viewCartBtn);

        add(panel, BorderLayout.SOUTH);

        loadProducts();
    }

    private void loadProducts() {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT p.PRODUCTID, p.PNAME, c.CATEGNAME, p.PRICE, p.QUANTITY FROM PRODUCT p JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID WHERE p.QUANTITY > 0");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("PRODUCTID"));
                row.add(rs.getString("PNAME"));
                row.add(rs.getString("CATEGNAME"));
                row.add(rs.getDouble("PRICE"));
                row.add(rs.getInt("QUANTITY"));
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.");
        }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            String category = (String) tableModel.getValueAt(row, 2);
            double price = (double) tableModel.getValueAt(row, 3);

            Product product = new Product(id, name, category, price);
            cart.addItem(product, 1);
            JOptionPane.showMessageDialog(this, "Added to cart.");
        }
    }

    public static void main(String[] args) {
        new ProductBrowserPage().setVisible(true);
    }
}
