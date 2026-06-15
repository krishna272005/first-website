import javax.swing.*;
import java.awt.*;

public class ContactPage extends JFrame {

    public ContactPage() {

        setTitle("Contact Gram Panchayat Office");
        setSize(500,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        JLabel title = new JLabel("Gram Panchayat Office Details");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel phone = new JLabel("📞 Phone: +91 98765 43210");
        JLabel email = new JLabel("📧 Email: mahagaon.gp@gmail.com");
        JLabel address = new JLabel("📍 Address: Gram Panchayat Office, Mahagaon, Maharashtra");
        JLabel hours = new JLabel("🕒 Office Hours: 10:00 AM - 5:00 PM (Mon-Fri)");

        phone.setAlignmentX(Component.CENTER_ALIGNMENT);
        email.setAlignmentX(Component.CENTER_ALIGNMENT);
        address.setAlignmentX(Component.CENTER_ALIGNMENT);
        hours.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(phone);
        panel.add(Box.createVerticalStrut(15));
        panel.add(email);
        panel.add(Box.createVerticalStrut(15));
        panel.add(address);
        panel.add(Box.createVerticalStrut(15));
        panel.add(hours);

        add(panel);

        setVisible(true);
    }
}
