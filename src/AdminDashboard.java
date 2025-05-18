import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame 
{

    public AdminDashboard() 
    {
        this.setTitle("Admin Dashboard");
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 255, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 0));
        JLabel heading = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(heading);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 255, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createDashboardButton("Manage Products", e -> {dispose(); new ManageProductsPage();}), gbc);
        gbc.gridy++;
        panel.add(createDashboardButton("Manage Categories", e -> {dispose(); new ManageCategoriesPage();}), gbc);
        gbc.gridy++;
        panel.add(createDashboardButton("Manage Customers", e -> {dispose(); new ManageCustomersPage();}), gbc);
        gbc.gridy++;
        panel.add(createDashboardButton("Manage Branches", e -> {dispose(); new ManageBranchesPage();}), gbc);
        gbc.gridy++;
        panel.add(createDashboardButton("Manage Stocks", e -> {dispose(); new ManageStocksPage();}), gbc);
        gbc.gridy++;
        panel.add(createDashboardButton("Products Need Stocking", e -> {dispose(); new LowStockPage();}), gbc);
        gbc.gridy++;
        panel.add(createDashboardButton("Report", e -> { dispose();new ReportApp().setVisible(true);}), gbc);
        gbc.gridy++;
        panel.add(createBackButton("Logout", e -> {
            dispose();
            new SignInPage();
        }), gbc);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        this.add(mainPanel);
        this.setVisible(true);
    }

   private JButton createDashboardButton(String text, ActionListener listener)
   {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(250, 40));
        button.addActionListener(listener);
        return button;
   }

    private JButton createBackButton(String text, ActionListener listener) 
    {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(34, 139, 34));
        button.setFocusPainted(false);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(250, 40));
        button.addActionListener(listener);
        return button;
    }

}

