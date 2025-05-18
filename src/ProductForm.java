import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

public class ProductForm extends JDialog {

    private JTextField nameField, categoryField, priceField;
    private JButton saveButton, cancelButton;
    private ManageProductsPage parent;
    private ProductDAO productDAO;
    private Product productToEdit;

    public ProductForm(ManageProductsPage parent, Product product, ProductDAO dao) 
    {
        super(parent, "Product Form", true);
        this.parent = parent;
        this.productToEdit = product;
        this.productDAO = dao;

        setSize(700, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0, 100, 0));
        setLayout(new GridBagLayout());

        nameField = createStyledTextField("Enter product name");
        categoryField = createStyledTextField("Enter category");
        priceField = createStyledTextField("Enter price");

        saveButton = createStyledButton(product == null ? "Add Product" : "Save Changes");
        cancelButton = createCancelButton("Cancel");

        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> {new ManageProductsPage(); dispose();});

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addCentered(nameField, gbc, 0);
        addCentered(categoryField, gbc, 1);
        addCentered(priceField, gbc, 2);
        
        addCentered(saveButton, gbc, 4);
        addCentered(cancelButton, gbc, 5);

        if (product != null) 
        {
            nameField.setText(product.getName());
            categoryField.setText(product.getCategoryName());
            priceField.setText(String.valueOf(product.getPrice()));
            
        }

        setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();

    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        double price;
        

        try {
            price = Double.parseDouble(priceField.getText().trim());
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity format.");
            return;
        }

        try {
            if (productToEdit == null) {
                Product newProduct = new Product(0, name, category, price);
                productDAO.insertProduct(newProduct);
            } else {
                productToEdit.setName(name);
                productToEdit.setCategoryName(category);
                productToEdit.setPrice(price);
                
                productDAO.updateProduct(productToEdit);
            }
            parent.refreshProductTable();
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage());
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




