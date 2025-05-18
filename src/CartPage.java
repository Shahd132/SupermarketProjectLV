
import javax.swing.*;
import java.awt.*;

public class CartPage extends JFrame {
    public CartPage(Cart cart) {
        setTitle("Shopping Cart");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea cartArea = new JTextArea();
        for (CartItem item : cart.getItems()) {
            cartArea.append(item.getProduct().getName() + " x " + item.getQuantity() + " = " + item.getTotalPrice() + " EGP\n");
        }
        cartArea.append("\nTotal: " + cart.getTotal() + " EGP");
        cartArea.setEditable(false);

        add(new JScrollPane(cartArea), BorderLayout.CENTER);

        JButton placeOrderBtn = new JButton("Place Order");
placeOrderBtn.setFont(new Font("Arial", Font.BOLD, 16));
placeOrderBtn.setBackground(new Color(34, 139, 34));
placeOrderBtn.setForeground(Color.WHITE);

placeOrderBtn.addActionListener(e -> {
    try {
        int customerId = 1; 
        new OrderDAO().placeOrder(customerId, cart);
        JOptionPane.showMessageDialog(this, "✅ Order placed successfully!");
        cart.clear();
        dispose();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, " Failed to place order: " + ex.getMessage());
    }
});

JPanel bottomPanel = new JPanel();
bottomPanel.add(placeOrderBtn);
add(bottomPanel, BorderLayout.SOUTH);

    }
}
