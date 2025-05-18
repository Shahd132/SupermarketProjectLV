import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

public class CustomerForm extends JDialog {

    private JTextField nameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private boolean isPasswordPlaceholderActive = true;

    private JButton saveButton, cancelButton;
    private ManageCustomersPage parent;
    private CustomerDAO customerDAO;
    private Customer customerToEdit;
    private AuthenticationManager authManager;

    public CustomerForm(ManageCustomersPage parent, Customer customer, CustomerDAO dao) {
        super(parent, "Customer Form", true);
        this.parent = parent;
        this.customerToEdit = customer;
        this.customerDAO = dao;
        this.authManager=new AuthenticationManager();

        setSize(700, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0, 100, 0));
        setLayout(new GridBagLayout());

        nameField = createStyledTextField("Enter name");
        emailField = createStyledTextField("Enter email");
        phoneField = createStyledTextField("Enter phone");
        addressField = createStyledTextField("Enter address");
        passwordField = createStyledPasswordField("Enter password");

        saveButton = createStyledButton(customer == null ? "Add Customer" : "Save Changes");
        cancelButton = createCancelButton("Cancel");

        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> {
            new ManageCustomersPage();
            dispose();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addCentered(nameField, gbc, 0);
        addCentered(emailField, gbc, 1);
        addCentered(phoneField, gbc, 2);
        addCentered(addressField, gbc, 3);
        addCentered(passwordField, gbc, 4);
        addCentered(saveButton, gbc, 5);
        addCentered(cancelButton, gbc, 6);

        if (customer != null) {
            nameField.setText(customer.getName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhoneNo());
            addressField.setText(customer.getAddress());

            passwordField.setEchoChar((char) 0);
            passwordField.setText("Leave empty to keep password");
            passwordField.setForeground(Color.GRAY);
            isPasswordPlaceholderActive = true;
        }

        setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            if (customerToEdit == null) {
                if (password.isEmpty() || isPasswordPlaceholderActive) {
                    JOptionPane.showMessageDialog(this, "Please enter a password for the new customer.");
                    return;
                }
                if(authManager.emailExists(email)||authManager.adminemailExists(email))
            {
                JOptionPane.showMessageDialog(this, "This email already used.");
                return;
            }
            if(authManager.phoneExists(phone))
            {
                JOptionPane.showMessageDialog(this, "This phone number already used.");
                return;
            }
            if(!isValidEmail(email))
            {
                JOptionPane.showMessageDialog(this, "Please enter a valid email.");
                return;
            }
            if(!isValidEgyptianPhone(phone))
            {
                JOptionPane.showMessageDialog(this, "Please enter a valid phone number.");
                return;
            }
            if(!isValidPassword(password))
            {
                JOptionPane.showMessageDialog(this, "Please enter more powerful password");
                return;
            }
                Customer newCustomer = new Customer(name, email, phone, address, password);
                customerDAO.insertCustomer(newCustomer);
            } else {
                customerToEdit.setName(name);
                customerToEdit.setEmail(email);
                customerToEdit.setphone(phone);
                customerToEdit.setAddress(address);
                if (!password.isEmpty() && !isPasswordPlaceholderActive) {
                    customerToEdit.setPassword(password); // Only update password if provided
                }
                customerDAO.updateCustomer(customerToEdit);
            }

            parent.loadAllCustomers();
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving customer: " + ex.getMessage());
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

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(30);
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(34, 139, 34)));
        field.setBackground(new Color(245, 255, 245));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(300, 40));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (isPasswordPlaceholderActive) {
                    field.setText("");
                    field.setEchoChar('\u2022');
                    field.setForeground(Color.BLACK);
                    isPasswordPlaceholderActive = false;
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    isPasswordPlaceholderActive = true;
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
    private boolean isValidEmail(String email) 
    {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    public boolean isValidEgyptianPhone(String phone) {
        return phone.matches("^(010|011|012|015)\\d{8}$");
    }
    public boolean isValidPassword(String password)
    {
        return password.length()>=7;
    }

}



