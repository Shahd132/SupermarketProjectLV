import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class BranchForm extends JDialog {

    private JTextField nameField, phoneField, adminIdField;
    private JButton saveButton, cancelButton;
    private ManageBranchesPage parent;
    private BranchDAO branchDAO;
    private Branch branchToEdit;

    public BranchForm(ManageBranchesPage parent, Branch branch, BranchDAO dao) {
        super(parent, "Branch Form", true);
        this.parent = parent;
        this.branchToEdit = branch;
        this.branchDAO = dao;

        setSize(700, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0, 100, 0));
        setLayout(new GridBagLayout());

        nameField = createStyledTextField("Enter branch name");
        phoneField = createStyledTextField("Enter phone");
        adminIdField = createStyledTextField("Enter admin ID");

        saveButton = createStyledButton(branch == null ? "Add Branch" : "Save Changes");
        cancelButton = createCancelButton("Cancel");

        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> { new ManageBranchesPage(); dispose(); });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addCentered(nameField, gbc, 0);
        addCentered(phoneField, gbc, 1);
        addCentered(adminIdField, gbc, 2);
        addCentered(saveButton, gbc, 3);
        addCentered(cancelButton, gbc, 4);

        if (branch != null) {
            nameField.setText(branch.getName());
            phoneField.setText(branch.getPhone());
            adminIdField.setText(String.valueOf(branch.getAdminId()));
        }

        setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        int adminId;
        if(!isValidEgyptianPhone(phone))
        {
             JOptionPane.showMessageDialog(this, "please enter a valid phone");
            return;
        }

        try {
            adminId = Integer.parseInt(adminIdField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Admin ID format.");
            return;
        }

        try {
            if (branchToEdit == null) {
                Branch newBranch = new Branch(0, name, phone, adminId);
                branchDAO.insertBranch(newBranch);
            } else {
                branchToEdit.setName(name);
                branchToEdit.setPhone(phone);
                branchToEdit.setAdminId(adminId);
                branchDAO.updateBranch(branchToEdit);
            }
            parent.refreshBranchTable();
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving branch: " + ex.getMessage());
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

    public boolean isValidEgyptianPhone(String phone) {
        return phone.matches("^(010|011|012|015)\\d{8}$");
    }
}



