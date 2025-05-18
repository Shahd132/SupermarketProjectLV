import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ManageCustomersPage extends JFrame {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private CustomerDAO customerDAO;

    public ManageCustomersPage() {
        this.setTitle("Manage Customers");
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(245, 255, 245));

        customerDAO = new CustomerDAO();

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 128, 0));
        JLabel titleLabel = new JLabel("Manage Customers", SwingConstants.CENTER);
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
        searchButton.addActionListener(e -> searchCustomers());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        this.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = {"ID", "Name", "Email", "Phone", "Address", "Reg Date", "Voucher"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(28);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 16));
        customerTable.setBackground(new Color(245, 255, 245));

        JTableHeader header = customerTable.getTableHeader();
        header.setBackground(new Color(0, 128, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        this.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 255, 245));
        JButton addButton = createStyledButton("Add Customer");
        JButton editButton = createStyledButton("Edit Customer");
        JButton deleteButton = createStyledButton("Delete Customer");
        JButton backButton = createBackButton("Back");

        addButton.addActionListener(e -> { new CustomerForm(this, null, customerDAO); });
        editButton.addActionListener(e -> editSelectedCustomer());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        backButton.addActionListener(e -> { new AdminDashboard(); dispose(); });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        loadAllCustomers();
        this.setVisible(true);
    }

    private void searchCustomers() {
        String name = searchField.getText().trim();
        if (name.equals("Search here")) name = "";
        try {
            List<Customer> customers = customerDAO.searchCustomers(name);
            refreshTable(customers);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
        }
    }

    public void loadAllCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            refreshTable(customers);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage());
        }
    }

    private void refreshTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), c.getPhoneNo(), c.getAddress(), c.getRegistrationDate(), c.getDiscountVoucher()});
        }
    }

    private void editSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String email = (String) tableModel.getValueAt(row, 2);
        String phone = (String) tableModel.getValueAt(row, 3);
        String address = (String) tableModel.getValueAt(row, 4);
        String regDate = (String) tableModel.getValueAt(row, 5);
        String voucher = (String) tableModel.getValueAt(row, 6);

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setphone(phone);
        customer.setDiscountVoucher(voucher);
        customer.setPassword(""); // You may handle password editing separately

        new CustomerForm(this, customer, customerDAO);
    }

    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerDAO.deleteCustomer(id);
                loadAllCustomers();
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
        return button;
    }

    public void refreshCustomerTable() {
        loadAllCustomers();
    }
}
