import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class StockForm extends JDialog {

    private JTextField branchIdField, productIdField, quantityField, minRequiredField;
    private JButton saveButton, cancelButton;
    private ManageStocksPage parent;
    private StockDAO stockDAO;
    private Stock stock;

    public StockForm(ManageStocksPage parent, Stock stock, StockDAO stockDAO) {
        super(parent, "Stock Form", true);
        this.parent = parent;
        this.stock = stock;
        this.stockDAO = stockDAO;

        setSize(700, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0, 100, 0));
        setLayout(new GridBagLayout());

        branchIdField = createStyledTextField("Enter Branch ID");
        productIdField = createStyledTextField("Enter Product ID");
        quantityField = createStyledTextField("Enter Quantity");
        minRequiredField = createStyledTextField("Enter Minimum Required");

        saveButton = createStyledButton(stock == null ? "Add Stock" : "Save Changes");
        cancelButton = createCancelButton("Cancel");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> {
            new ManageStocksPage();
            dispose();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addCentered(branchIdField, gbc, 0);
        addCentered(productIdField, gbc, 1);
        addCentered(quantityField, gbc, 2);
        addCentered(minRequiredField, gbc, 3);
        addCentered(saveButton, gbc, 4);
        addCentered(cancelButton, gbc, 5);

        if (stock != null) {
            branchIdField.setText(String.valueOf(stock.getBranchId()));
            productIdField.setText(String.valueOf(stock.getProductId()));
            quantityField.setText(String.valueOf(stock.getQuantity()));
            minRequiredField.setText(String.valueOf(stock.getMinRequired()));
        }

        setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private void onSave() {
        try {
            int branchId = Integer.parseInt(branchIdField.getText().trim());
            int productId = Integer.parseInt(productIdField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int minRequired = Integer.parseInt(minRequiredField.getText().trim());
            Date now = new Date();

            if (stock == null) {
                Stock newStock = new Stock(0, branchId, productId, quantity, minRequired, now);
                stockDAO.insertStock(newStock);
            } else {
                stock.setBranchId(branchId);
                stock.setProductId(productId);
                stock.setQuantity(quantity);
                stock.setMinRequired(minRequired);
                stock.setLastUpdated(now);
                stockDAO.updateStock(stock);
            }

            parent.refreshStockTable();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving stock: " + ex.getMessage());
        }
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(30);
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(34, 139, 34)));
        field.setBackground(new Color(245, 255, 245));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(300, 40));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private JButton createCancelButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(34, 139, 34));
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private void addCentered(Component comp, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        add(comp, gbc);
    }
}
