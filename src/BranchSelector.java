import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchSelector extends JFrame {
    private JComboBox<String> branchComboBox;
    private JButton confirmButton;
    private List<Integer> branchIds = new ArrayList<>();
    private Customer customer;

    public BranchSelector(Customer customer) {
        this.customer = customer;
        setTitle("Select Branch");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set matching background color (e.g., soft bluish-gray)
        Color backgroundColor = new Color(220, 230, 241);  // Match CustomerDashboard
        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(20, 20));

        JLabel label = new JLabel("Select a branch to continue:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(new Color(33, 37, 41)); // Dark text for readability
        add(label, BorderLayout.NORTH);

        branchComboBox = new JComboBox<>();
        loadBranches();
        branchComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        branchComboBox.setPreferredSize(new Dimension(250, 30));

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(branchComboBox);
        add(centerPanel, BorderLayout.CENTER);

        confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        confirmButton.setBackground(new Color(0, 123, 255)); // Bootstrap primary blue
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(120, 40));
        confirmButton.addActionListener(e -> openDashboard());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadBranches() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true");
             PreparedStatement stmt = conn.prepareStatement("SELECT BRANCHID, BNAME FROM BRANCH");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("BRANCHID");
                String name = rs.getString("BNAME");
                branchComboBox.addItem(name);
                branchIds.add(id);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load branches: " + e.getMessage());
        }
    }

    private void openDashboard() {
        int selectedIndex = branchComboBox.getSelectedIndex();
        if (selectedIndex >= 0) {
            int selectedBranchId = branchIds.get(selectedIndex);
            dispose(); // Close branch selector
            new CustomerDashboard(customer, selectedBranchId);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a branch.");
        }
    }
}
