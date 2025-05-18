import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class CustomerDashboard extends JFrame {
    private Customer customer;
    private JTable productTable;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private int branchId;
    private JTextField searchField;
    private JButton searchButton;
    private JButton viewCartBtn; 
    private DefaultTableModel productTableModel;
    private ArrayList<OrderItem> cartItems = new ArrayList<>();


    public CustomerDashboard(Customer customer, int branchId) {
        this.customer = customer;
        this.branchId = branchId;
        setTitle("Customer Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productDAO = new ProductDAO();
        customerDAO = new CustomerDAO();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 255, 245));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 102, 0));

        JLabel title = new JLabel("Customer Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(title, BorderLayout.CENTER);

        JButton editProfileBtn = new JButton("Edit Profile");
        editProfileBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        editProfileBtn.setBackground(Color.WHITE);
        editProfileBtn.setForeground(new Color(0, 102, 0));
        editProfileBtn.setFocusPainted(false);
        editProfileBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        editProfileBtn.addActionListener(e -> openProfileEditor());
        titlePanel.add(editProfileBtn, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 255, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        addSearchPanel(centerPanel);
        addProductTable(centerPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 255, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));


        JButton viewproductBtn = createDashboardButton("View Product", e -> onViewProduct());
        viewCartBtn = createDashboardButton("View Cart (0)", e -> showCart());
        JButton orderBtn = createDashboardButton("Place Order", e -> onPlaceOrder());


        // Create the back button with different styling
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 18));
        backBtn.setForeground(new Color(0, 102, 0));
        backBtn.setBackground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 0), 2));
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> {
            this.dispose();
            new BranchSelector(customer).setVisible(true);
        });

        // Add all buttons to the panel
        buttonPanel.add(viewproductBtn);
        buttonPanel.add(viewCartBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(backBtn);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        loadProductsForBranch();
    }

    private void addSearchPanel(JPanel parent) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 255, 245));

        searchField = new JTextField("Search products...", 20);
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 0), 2));

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search products...");
                }
            }
        });

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setBackground(new Color(0, 102, 0));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        searchButton.addActionListener(e -> searchProducts());

        searchField.addActionListener(e -> searchProducts());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        parent.add(searchPanel, BorderLayout.NORTH);
    }

    private void addProductTable(JPanel parent) {
        String[] columnNames = {"ID", "Name", "Category", "Price"};
        productTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setRowHeight(25);
        productTable.setBackground(new Color(245, 255, 245));

        JTableHeader header = productTable.getTableHeader();
        header.setBackground(new Color(0, 102, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(new Color(245, 255, 245));
        parent.add(scrollPane, BorderLayout.CENTER);
    }

    private void searchProducts() {
        String query = searchField.getText().trim();
        if (query.equals("Search products...")) query = "";
        try {
            List<Product> products = productDAO.searchProductsByBranch(query, branchId);
            refreshProductTable(products);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
        }
    }

    private void loadProductsForBranch() {
        try {
            List<Product> products = productDAO.getProductsByBranch(branchId);
            refreshProductTable(products);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load branch products: " + e.getMessage());
        }
    }

    private void refreshProductTable(List<Product> products) {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            productTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategoryName(), p.getPrice()
            });
        }
    }

    private JButton createDashboardButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(200, 40));
        button.addActionListener(listener);
        return button;
    }

    private void onViewProduct() {
        try {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to view.");
                return;
            }

            // Get values safely with proper type conversion
            int productId = Integer.parseInt(productTable.getValueAt(selectedRow, 0).toString());
            String name = productTable.getValueAt(selectedRow, 1).toString();
            String category = productTable.getValueAt(selectedRow, 2).toString();
            double price = Double.parseDouble(productTable.getValueAt(selectedRow, 3).toString());

            // Record view in database
            int customerId = customer.getId();
            incrementView(productId, customerId);

            // Create product details panel (without quantity)
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Name: " + name));
            panel.add(new JLabel("Category: " + category));
            panel.add(new JLabel("Price: $" + price));

            // Add to cart controls with default quantity (1-100)
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            JButton addToCartBtn = new JButton("Add to Cart");
            addToCartBtn.addActionListener(e -> {
                int qty = (int) quantitySpinner.getValue();
                cartItems.add(new OrderItem(productId, name, price, qty));
                updateCartButton();
                JOptionPane.showMessageDialog(this, "Added to cart!");
            });

            JPanel controlsPanel = new JPanel();
            controlsPanel.add(new JLabel("Quantity:"));
            controlsPanel.add(quantitySpinner);
            controlsPanel.add(addToCartBtn);
            panel.add(controlsPanel);

            // Show product details
            JOptionPane.showMessageDialog(this, panel, "Product Details", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error viewing product: " + e.getMessage());
        }
    }

    private void onPlaceOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Please add products first.");
            return;
        }
        showCart();
    }

    private void incrementView(int productId, int customerId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true")) {
            String checkSql = "SELECT TIMES_VIEWED FROM VIEWS WHERE PRODUCTID = ? AND CID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, productId);
                checkStmt.setInt(2, customerId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int timesViewed = rs.getInt("TIMES_VIEWED") + 1;
                    String updateSql = "UPDATE VIEWS SET TIMES_VIEWED = ? WHERE PRODUCTID = ? AND CID = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, timesViewed);
                        updateStmt.setInt(2, productId);
                        updateStmt.setInt(3, customerId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO VIEWS (PRODUCTID, CID, TIMES_VIEWED) VALUES (?, ?, 1)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, productId);
                        insertStmt.setInt(2, customerId);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error recording view: " + e.getMessage());
        }
    }


    private void showCart() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }

        final double[] totalHolder = {0};
        for (OrderItem item : cartItems) {
            totalHolder[0] += item.getQuantity() * item.getPrice();
        }

        JDialog cartDialog = new JDialog(this, "Your Cart", true);
        cartDialog.setSize(500, 400);
        cartDialog.setLayout(new BorderLayout());
        cartDialog.setLocationRelativeTo(this);

        String[] columnNames = {"Product", "Quantity", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (OrderItem item : cartItems) {
            double subtotal = item.getQuantity() * item.getPrice();
            model.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                String.format("$%.2f", item.getPrice()),
                String.format("$%.2f", subtotal)
            });
        }

        JTable cartTable = new JTable(model);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cartTable.setRowHeight(25);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("Total: " + String.format("$%.2f", totalHolder[0])));

        JPanel buttonPanel = new JPanel();
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.addActionListener(e -> {
            cartDialog.dispose();
            showCheckoutDialog(totalHolder[0]);
        });

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                cartItems.remove(selectedRow);
                updateCartButton();
                cartDialog.dispose();
                if (!cartItems.isEmpty()) {
                    showCart();
                } else {
                    JOptionPane.showMessageDialog(this, "Your cart is now empty.");
                }
            }
        });

        buttonPanel.add(removeBtn);
        buttonPanel.add(checkoutBtn);

        cartDialog.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartDialog.add(totalPanel, BorderLayout.NORTH);
        cartDialog.add(buttonPanel, BorderLayout.SOUTH);
        cartDialog.setVisible(true);
    }

    private void showCheckoutDialog(double total) {
        JDialog checkoutDialog = new JDialog(this, "Checkout", true);
        checkoutDialog.setSize(400, 300);
        checkoutDialog.setLayout(new GridBagLayout());
        checkoutDialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPanel.add(new JLabel("Payment Method:"));
        ButtonGroup paymentGroup = new ButtonGroup();
        JRadioButton cashRadio = new JRadioButton("Cash");
        JRadioButton cardRadio = new JRadioButton("Credit Card");
        paymentGroup.add(cashRadio);
        paymentGroup.add(cardRadio);
        cashRadio.setSelected(true);
        paymentPanel.add(cashRadio);
        paymentPanel.add(cardRadio);

        JTextArea addressArea = new JTextArea(customer.getAddress(), 3, 20);
        addressArea.setEditable(false);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        JLabel totalLabel = new JLabel("Total: " + String.format("$%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton confirmBtn = new JButton("Confirm Order");
        confirmBtn.addActionListener(e -> {
            String paymentMethod = cashRadio.isSelected() ? "Cash" : "Credit Card";
            placeOrder(paymentMethod, total);
            checkoutDialog.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> checkoutDialog.dispose());

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        checkoutDialog.add(paymentPanel, gbc);

        gbc.gridy++;
        checkoutDialog.add(new JLabel("Delivery Address:"), gbc);
        
        gbc.gridy++;
        checkoutDialog.add(new JScrollPane(addressArea), gbc);
        
        gbc.gridy++;
        checkoutDialog.add(totalLabel, gbc);
        
        gbc.gridy++;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);
        checkoutDialog.add(btnPanel, gbc);

        checkoutDialog.setVisible(true);
    }

    private void placeOrder(String paymentMethod, double total) {
        try {
            Order order = new Order();
            order.setCustomerId(customer.getId());
            order.setPaymentMethod(paymentMethod);
            order.setStatus("Pending");
            order.setTotalPrice(total);

            OrderDAO orderDAO = new OrderDAO();
            int orderId = orderDAO.insertOrder(order);
            
            if (orderId > 0) {
                for (OrderItem item : cartItems) {
                    item.setOrderId(orderId);
                    orderDAO.insertOrderItem(item);
                }

                showReceipt(orderId, paymentMethod, total);
                cartItems.clear();
                updateCartButton();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create order.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error placing order: " + ex.getMessage());
        }
    }

    private void showReceipt(int orderId, String paymentMethod, double total) {
        JDialog receiptDialog = new JDialog(this, "Order Receipt", true);
        receiptDialog.setSize(500, 500);
        receiptDialog.setLayout(new BorderLayout());
        receiptDialog.setLocationRelativeTo(this);

        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        receiptPanel.setBackground(Color.WHITE);

        JLabel header = new JLabel("ORDER RECEIPT", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(header);

        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        addReceiptLine(receiptPanel, "Order ID:", String.valueOf(orderId));
        addReceiptLine(receiptPanel, "Customer:", customer.getName());
        addReceiptLine(receiptPanel, "Date:", java.time.LocalDate.now().toString());
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addReceiptLine(receiptPanel, "Payment Method:", paymentMethod);
        addReceiptLine(receiptPanel, "Status:", "Pending");
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel itemsHeader = new JLabel("ITEMS ORDERED:");
        itemsHeader.setFont(new Font("Arial", Font.BOLD, 14));
        itemsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        receiptPanel.add(itemsHeader);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] columns = {"Product", "Qty", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (OrderItem item : cartItems) {
            model.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                String.format("$%.2f", item.getPrice()),
                String.format("$%.2f", item.getQuantity() * item.getPrice())
            });
        }

        JTable itemsTable = new JTable(model);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        itemsTable.setRowHeight(20);

        receiptPanel.add(new JScrollPane(itemsTable));
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel totalLabel = new JLabel("TOTAL: " + String.format("$%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(totalLabel);

        JButton closeBtn = new JButton("Close");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> receiptDialog.dispose());

        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        receiptPanel.add(closeBtn);

        receiptDialog.add(receiptPanel, BorderLayout.CENTER);
        receiptDialog.setVisible(true);
    }

    private void addReceiptLine(JPanel panel, String label, String value) {
        JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linePanel.setBackground(Color.WHITE);
        linePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Arial", Font.BOLD, 12));
        labelLbl.setPreferredSize(new Dimension(120, 15));
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        
        linePanel.add(labelLbl);
        linePanel.add(valueLbl);
        panel.add(linePanel);
    }

    private void updateCartButton() {
        viewCartBtn.setText("View Cart (" + cartItems.size() + ")");
    }

    private JTable loadProductTable() {
        String[] columnNames = {"ID", "Name", "Category", "Price"};
        Object[][] data = {};

        try {
            List<Product> products = productDAO.getAllProducts();
            data = new Object[products.size()][5];
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                data[i][0] = p.getId();
                data[i][1] = p.getName();
                data[i][2] = p.getCategoryName();
                data[i][3] = p.getPrice();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        return table;
    }



    private void openProfileEditor() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(customer.getName(), 20);
        JTextField emailField = new JTextField(customer.getEmail(), 20);
        JTextField phoneField = new JTextField(customer.getPhoneNo(), 20);
        JTextField addressField = new JTextField(customer.getAddress(), 20);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete Profile");

        saveButton.addActionListener(e -> {
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            customer.setAddress(addressField.getText());

            try {
                customerDAO.updateCustomerProfile(customer);
                JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating profile: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete your profile?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    customerDAO.deleteCustomer(customer.getId());
                    JOptionPane.showMessageDialog(dialog, "Profile deleted successfully!");
                    dialog.dispose();
                    this.dispose(); // Close dashboard
                    new SignInPage().setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error deleting profile: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private Connection connect() throws SQLException {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
        return DriverManager.getConnection(url);
    }
}
