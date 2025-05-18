import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignInPage extends JFrame 
{
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton logiBtn;
    private JButton goToSignUpBtn;
    private AuthenticationManager authManager;

    public SignInPage() 
    {
        authManager = new AuthenticationManager();

        this.setTitle("Supermarket Sign In");
        this.setSize(700, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(0, 128, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false); 
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        contentPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        contentPanel.setPreferredSize(new Dimension(400, 300));
        contentPanel.setMinimumSize(new Dimension(400, 100));
        

        centerWrapper.add(contentPanel, gbc);

        setLayout(new BorderLayout()); 
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 128, 0));

        JLabel titleLabel = new JLabel("Supermarket Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0)); 
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);



        emailField = new JTextField("Enter your email");
        setupPlaceholderBehavior(emailField, "Enter your email");

        passwordField = new JPasswordField("Password");
        setupPasswordPlaceholderBehavior(passwordField, "Password");

        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        emailField.setMaximumSize(new Dimension(400, 40));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setMaximumSize(new Dimension(400, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        
        logiBtn = new JButton("Login");
        logiBtn.setFont(new Font("Arial", Font.BOLD, 16));
        logiBtn.setBackground(new Color(34, 139, 34));
        logiBtn.setForeground(Color.WHITE);
        logiBtn .setFocusPainted(false);
        logiBtn .setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logiBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        goToSignUpBtn = new JButton("Go to Sign Up");
        goToSignUpBtn.setFont(new Font("Arial", Font.BOLD, 16));
        goToSignUpBtn.setBackground(Color.WHITE);
        goToSignUpBtn.setForeground(new Color(34, 139, 34));
        goToSignUpBtn.setFocusPainted(false);
        goToSignUpBtn .setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        goToSignUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(emailField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(logiBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(goToSignUpBtn);

        centerWrapper.add(contentPanel);
        add(centerWrapper, BorderLayout.CENTER);

        logiBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (password.equals("Password")||email.equals("Enter your email"))
            {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            } 
            if(!isValidEmail(email))
            {
                JOptionPane.showMessageDialog(this, "Enter a valid email");
                return;
            }
            if(!authManager.emailExists(email)&&!authManager.adminemailExists(email))
            {
                JOptionPane.showMessageDialog(this, "This account not found.");
                return;
            }

            boolean success = authManager.authenticateAdmin(email, password) ||authManager.authenticateCustomer(email,password);
            if(!success)
            {
                JOptionPane.showMessageDialog(null, "login failed.");
                return;
            }
            else
            {
                
                if(authManager.determineRole(email, password)=="admin")
                {
                    new AdminDashboard();
                    dispose();
                }
                else
                {
                    Customer customer = authManager.getCustomerByEmail(email);
                    new BranchSelector(customer);
                    dispose();

                }
                
            }
           
        });

        goToSignUpBtn.addActionListener(e -> {
        new SignUpPage();
        dispose();});

        setVisible(true);
        SwingUtilities.invokeLater(() -> titleLabel.requestFocusInWindow());
    }

    private void setupPlaceholderBehavior(JTextField field, String placeholder) 
    {
        field.setForeground(Color.LIGHT_GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) 
            {
                if (field.getText().isEmpty()) 
                {
                    field.setText(placeholder);
                    field.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
    }

    private void setupPasswordPlaceholderBehavior(JPasswordField field, String placeholder) 
    {
        field.setForeground(Color.LIGHT_GRAY);
        field.setText(placeholder);
        field.setEchoChar((char) 0);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) 
            {
                if (new String(field.getPassword()).equals(placeholder)) 
                {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('\u2022'); // bullet char
                }
            }

            public void focusLost(FocusEvent e) 
            {
                if (field.getPassword().length == 0) 
                {
                    field.setText(placeholder);
                    field.setForeground(Color.LIGHT_GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }
    private boolean isValidEmail(String email) 
    {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}




