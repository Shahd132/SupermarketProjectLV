import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ManageCategoriesPage extends JFrame {

    private JTable categoryTable;
    private JTable productTable;
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel productTableModel;

    private JTextField searchField;
    private CategoryDAO categoryDAO;
    private ProductDAO productDAO;

    public ManageCategoriesPage() {
        this.setTitle("Manage Categories");
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(245, 255, 245));

        categoryDAO = new CategoryDAO();
        productDAO = new ProductDAO();

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 128, 0));
        JLabel titleLabel = new JLabel("Manage Categories", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        this.add(titlePanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 255, 245));
        searchField = new JTextField("Search here", 20);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search here")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search here");
                }
            }
        });

        searchField.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBackground(Color.WHITE);

        JButton searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> searchCategories());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        this.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Category Table
        String[] categoryColumns = {"ID", "Name", "Description"};
        categoryTableModel = new DefaultTableModel(categoryColumns, 0);
        categoryTable = new JTable(categoryTableModel);
        categoryTable.setRowHeight(28);
        categoryTable.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryTable.setBackground(new Color(245, 255, 245));
        JTableHeader catHeader = categoryTable.getTableHeader();
        catHeader.setBackground(new Color(0, 128, 0));
        catHeader.setForeground(Color.WHITE);
        catHeader.setFont(new Font("Arial", Font.BOLD, 16));

        // Product Table
        String[] productColumns = {"ID", "Name", "Category", "Price", "Quantity"};
        productTableModel = new DefaultTableModel(productColumns, 0);
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(28);
        productTable.setFont(new Font("Arial", Font.PLAIN, 16));
        productTable.setBackground(new Color(245, 255, 245));
        JTableHeader prodHeader = productTable.getTableHeader();
        prodHeader.setBackground(new Color(0, 128, 0));
        prodHeader.setForeground(Color.WHITE);
        prodHeader.setFont(new Font("Arial", Font.BOLD, 16));

        // Split pane to hold category table on top, product table below
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(categoryTable), new JScrollPane(productTable));
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        this.add(splitPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 255, 245));
        JButton addButton = createStyledButton("Add Category");
        JButton editButton = createStyledButton("Edit Category");
        JButton deleteButton = createStyledButton("Delete Category");
        JButton backButton = createBackButton("Back");

        addButton.addActionListener(e -> openCategoryForm(null));
        editButton.addActionListener(e -> editSelectedCategory());
        deleteButton.addActionListener(e -> deleteSelectedCategory());
        backButton.addActionListener(e -> {
            // You can replace this with your admin dashboard opening logic
            new AdminDashboard();
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadProductsForSelectedCategory();
            }
        });

        loadAllCategories();
        this.setVisible(true);
    }

    private void searchCategories() {
        String name = searchField.getText().trim();
        if (name.equals("Search here")) name = "";
        try {
            List<Category> categories = categoryDAO.searchCategories(name);
            refreshCategoryTable(categories);
            productTableModel.setRowCount(0); // clear products table on search
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
        }
    }

    private void loadAllCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            refreshCategoryTable(categories);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        }
    }

    private void refreshCategoryTable(List<Category> categories) {
        categoryTableModel.setRowCount(0);
        for (Category c : categories) {
            categoryTableModel.addRow(new Object[]{c.getCategoryId(), c.getName(), c.getDescription()});
        }
    }

    private void loadProductsForSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) 
        {
            productTableModel.setRowCount(0);
            return;
        }
        int categoryId = (int) categoryTableModel.getValueAt(row, 0);
        try {
            List<Product> products = productDAO.getProductsByCategory(categoryId);
            productTableModel.setRowCount(0);
            for (Product p : products) {
                productTableModel.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategoryName(),
                        p.getPrice(),
                        p.getQuantity()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }

    private void openCategoryForm(Category category) {
        // Assume you create a CategoryForm class similar to CustomerForm
        new CategoryForm(this, category, categoryDAO);
    }

    private void editSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to edit.");
            return;
        }
        int id = (int) categoryTableModel.getValueAt(row, 0);
        String name = (String) categoryTableModel.getValueAt(row, 1);
        String desc = (String) categoryTableModel.getValueAt(row, 2);

        Category category = new Category(id, name, desc);
        openCategoryForm(category);
    }

    private void deleteSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.");
            return;
        }
        int id = (int) categoryTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this category?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryDAO.deleteCategory(id);
                loadAllCategories();
                productTableModel.setRowCount(0);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 128, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return button;
    }

    private JButton createBackButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0, 128, 0));
        button.setFocusPainted(false);
button.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
button.setPreferredSize(new Dimension(100, 40));
return button;
}
// Call this method to refresh the categories table after adding/editing in CategoryForm
public void refreshCategories() {
    loadAllCategories();
    productTableModel.setRowCount(0);
}

// Main method for quick testing:
}
