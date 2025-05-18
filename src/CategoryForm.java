import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class CategoryForm extends JDialog {

    private JTextField nameField, descriptionField;
    private JButton saveButton, cancelButton;
    private ManageCategoriesPage parent;
    private CategoryDAO categoryDAO;
    private Category categoryToEdit;

    public CategoryForm(ManageCategoriesPage parent, Category category, CategoryDAO dao) {
        super(parent, "Category Form", true);
        this.parent = parent;
        this.categoryToEdit = category;
        this.categoryDAO = dao;

        setSize(700, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0, 100, 0));
        setLayout(new GridBagLayout());

        nameField = createStyledTextField("Enter category name");
        descriptionField = createStyledTextField("Enter description");

        saveButton = createStyledButton(category == null ? "Add Category" : "Save Changes");
        cancelButton = createCancelButton("Cancel");

        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> {
            new ManageCategoriesPage();
            dispose();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addCentered(nameField, gbc, 0);
        addCentered(descriptionField, gbc, 1);
        addCentered(saveButton, gbc, 2);
        addCentered(cancelButton, gbc, 3);

        if (category != null) {
            nameField.setText(category.getName());
            descriptionField.setText(category.getDescription());
        }

        setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty() || name.equals("Enter category name")) {
            JOptionPane.showMessageDialog(this, "Category name is required.");
            return;
        }

        try {
            if (categoryToEdit == null) {
                Category newCategory = new Category(0, name, description);
                categoryDAO.insertCategory(newCategory);
            } else {
                categoryToEdit.setName(name);
                categoryToEdit.setDescription(description);
                categoryDAO.updateCategory(categoryToEdit);
            }
            parent.refreshCategories();
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving category: " + ex.getMessage());
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

