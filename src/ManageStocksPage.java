import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ManageStocksPage extends JFrame {
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private StockDAO stockDAO;

    public ManageStocksPage() {
        stockDAO = new StockDAO();
        setTitle("Manage Stocks");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 255, 245));

        JLabel titleLabel = new JLabel("Manage Stocks", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(700, 50));
        titlePanel.setBackground(new Color(34, 139, 34));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Branch ID", "Product ID", "Quantity", "Min Required", "Last Updated"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        stockTable = new JTable(tableModel);
        stockTable.setRowHeight(25);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        stockTable.getTableHeader().setBackground(new Color(34, 139, 34));
        stockTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 255, 245));

        JButton addButton = createStyledButton("Add Stock");
        JButton editButton = createStyledButton("Edit Stock");
        JButton deleteButton = createStyledButton("Delete Stock");
        JButton backButton = createBackButton("Back");

        addButton.addActionListener(e -> openStockForm(null));
        editButton.addActionListener(e -> editSelectedStock());
        deleteButton.addActionListener(e -> deleteSelectedStock());
        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard(); // Update if your dashboard class has a different name
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshStockTable();
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

    public void refreshStockTable() {
        try {
            List<Stock> stocks = stockDAO.getAllStocks();
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Stock s : stocks) {
                tableModel.addRow(new Object[]{
                    s.getStockId(),
                    s.getBranchId(),
                    s.getProductId(),
                    s.getQuantity(),
                    s.getMinRequired(),
                    s.getLastUpdated() != null ? sdf.format(s.getLastUpdated()) : ""
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading stock: " + ex.getMessage());
        }
    }

    private void openStockForm(Stock stock) {
        new StockForm(this, stock, stockDAO);
    }

    private void editSelectedStock() {
        int row = stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock to edit.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int branchId = (int) tableModel.getValueAt(row, 1);
        int productId = (int) tableModel.getValueAt(row, 2);
        int quantity = (int) tableModel.getValueAt(row, 3);
        int minReq = (int) tableModel.getValueAt(row, 4);
        String dateStr = (String) tableModel.getValueAt(row, 5);

        java.util.Date lastUpdated = null;
        try {
            lastUpdated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (Exception ignored) {}

        openStockForm(new Stock(id, branchId, productId, quantity, minReq, lastUpdated));
    }

    private void deleteSelectedStock() {
        int row = stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock to delete.");
            return;
        }
        int stockid = (int) tableModel.getValueAt(row, 0);
        int productid = (int) tableModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this stock?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                stockDAO.deleteStock(stockid,productid);
                refreshStockTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting stock: " + ex.getMessage());
            }
        }
    }
}
