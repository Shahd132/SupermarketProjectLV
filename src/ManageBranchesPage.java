import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ManageBranchesPage extends JFrame {
    private BranchDAO branchDAO;
    private JTable branchTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public ManageBranchesPage() {
        branchDAO = new BranchDAO();

        setTitle("Manage Branches");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 255, 245));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(34, 139, 34));
        titlePanel.setPreferredSize(new Dimension(700, 50));
        JLabel titleLabel = new JLabel("Manage Branches", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(245, 255, 245));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setHorizontalAlignment(JTextField.CENTER);
        searchField.setForeground(Color.GRAY);
        searchField.setText("Search by name");
        searchField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(34, 139, 34)));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search by name")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search by name");
                }
            }
        });

        JButton searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> searchBranches());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = {"ID", "Name", "Phone", "Admin ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        branchTable = new JTable(tableModel);
        branchTable.setRowHeight(25);
        branchTable.setFont(new Font("Arial", Font.PLAIN, 14));
        branchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = branchTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(34, 139, 34));
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(branchTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 255, 245));

        JButton addButton = createStyledButton("Add Branch");
        JButton editButton = createStyledButton("Edit Branch");
        JButton deleteButton = createStyledButton("Delete Branch");
        JButton backButton = createBackButton("Back");

        addButton.addActionListener(e -> openBranchForm(null));
        editButton.addActionListener(e -> editSelectedBranch());
        deleteButton.addActionListener(e -> deleteSelectedBranch());
        backButton.addActionListener(e -> {new AdminDashboard(); dispose(); });
        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshBranchTable();

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(34, 139, 34));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
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

    public void refreshBranchTable() {
        try {
            List<Branch> branches = branchDAO.getAllBranches();
            populateTable(branches);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading branches: " + ex.getMessage());
        }
    }

    private void populateTable(List<Branch> branches) {
        tableModel.setRowCount(0);
        for (Branch b : branches) {
            tableModel.addRow(new Object[]{b.getBranchId(), b.getName(), b.getPhone(), b.getAdminId()});
        }
    }

    private void searchBranches() {
        String searchText = searchField.getText().trim();
        if (searchText.equals("Search by name") || searchText.isEmpty()) {
            refreshBranchTable();
            return;
        }

        try {
            List<Branch> branches = branchDAO.searchBranches(searchText);
            populateTable(branches);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching branches: " + ex.getMessage());
        }
    }

    private void openBranchForm(Branch branch) {
        new BranchForm(this, branch, branchDAO);
    }

    private void editSelectedBranch() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a branch to edit.");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String phone = (String) tableModel.getValueAt(selectedRow, 2);
        int adminId = (int) tableModel.getValueAt(selectedRow, 3);

        Branch branch = new Branch(id, name, phone, adminId);
        openBranchForm(branch);
    }

    private void deleteSelectedBranch() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a branch to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this branch?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int branchId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            branchDAO.deleteBranch(branchId);
            refreshBranchTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting branch: " + ex.getMessage());
        }
    }
    
}


