import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ManageProductsPage extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ProductDAO productDAO;

    public ManageProductsPage() 
    {
        this.setTitle("Manage Products");
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(245, 255, 245));

        productDAO = new ProductDAO();

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 128, 0));//dark green
        JLabel titleLabel = new JLabel("Manage Products", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        this.add(titlePanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 255, 245));
        searchField = new JTextField("Search here", 20);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search here")) 
                {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) 
            {
                if (searchField.getText().isEmpty()) 
                {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search here");
                }
            }
        });

        searchField.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBackground(Color.WHITE);

        JButton searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> searchProducts());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        this.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = {"ID", "Name", "Category", "Price"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        productTable.setRowHeight(28);
        productTable.setFont(new Font("Arial", Font.PLAIN, 16));
        productTable.setBackground(new Color(245, 255, 245));

        JTableHeader header = productTable.getTableHeader();
        header.setBackground(new Color(0, 128, 0)); 
        header.setForeground(Color.WHITE);          
        header.setFont(new Font("Arial", Font.BOLD, 16));

        this.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 255, 245));
        JButton addButton = createStyledButton("Add Product");
        JButton editButton = createStyledButton("Edit Product");
        JButton deleteButton = createStyledButton("Delete Product");
        JButton backButton = createBackButton("Back");

        addButton.addActionListener(e -> {new ProductForm(this, null, productDAO); });
        editButton.addActionListener(e -> editSelectedProduct());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        backButton.addActionListener(e -> {new AdminDashboard(); dispose();});

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        loadAllProducts();
        this.setVisible(true);
    }

    private void searchProducts() 
    {
        String name = searchField.getText().trim();
        if (name.equals("Search here")) name = "";
        try 
        {
            List<Product> products = productDAO.searchProducts(name);
            refreshTable(products);
        } 
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
        }
    }

    private void loadAllProducts() 
    {
        try 
        {
            List<Product> products = productDAO.getAllProducts();
            refreshTable(products);
        } 
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage());
        }
    }

    private void refreshTable(List<Product> products) 
    {
        tableModel.setRowCount(0);
        for (Product p : products) 
        {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategoryName(), p.getPrice(), p.getQuantity()});
        }
    }

    private void editSelectedProduct() 
    {
        int row = productTable.getSelectedRow();
        if (row == -1) 
        {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String category = (String) tableModel.getValueAt(row, 2);
        double price = (double) tableModel.getValueAt(row, 3);
        

        Product product = new Product(id, name, category, price);
        new ProductForm(this, product, productDAO);
    }

    private void deleteSelectedProduct() 
    {
        int row = productTable.getSelectedRow();
        if (row == -1) 
        {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) 
        {
            try 
            {
                productDAO.deleteProduct(id);
                loadAllProducts();
            } 
            catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
            }
        }
    }

    private JButton createStyledButton(String text) 
    {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 128, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return button;
    }

    private JButton createBackButton(String text) 
    {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0, 128, 0));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
        return button;
    }

    public void refreshProductTable() 
    {
        loadAllProducts();
    }
}

