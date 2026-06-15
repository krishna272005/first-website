import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame() {

        setTitle("Gram Panchayat Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        Color buttonColor = new Color(255, 102, 0);

        // ========= Background =========
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(236, 242, 248));
        add(background);

        // ========= Login Card =========
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 550));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        background.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // ========= Logo =========
        ImageIcon logoIcon = new ImageIcon("src/logo.png");
        Image scaled = logoIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(logoLabel, gbc);

        // ========= Title =========
        JLabel title = new JLabel("Gram Panchayat");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy++;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Management System");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy++;
        card.add(subtitle, gbc);

        // ========= Username =========
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        card.add(new JLabel("Username"), gbc);

        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 35));
        card.add(usernameField, gbc);

        // ========= Password =========
        gbc.gridy++;
        card.add(new JLabel("Password"), gbc);

        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 35));
        card.add(passwordField, gbc);

        // ========= Role =========
        gbc.gridy++;
        card.add(new JLabel("Role"), gbc);

        gbc.gridy++;
        roleBox = new JComboBox<>(new String[]{"Admin", "Citizen"});
        card.add(roleBox, gbc);

        // ========= Login Button =========
        gbc.gridy++;
        gbc.insets = new Insets(20, 30, 10, 30);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(buttonColor);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 15));
        loginBtn.setPreferredSize(new Dimension(300, 40));

        card.add(loginBtn, gbc);

        loginBtn.addActionListener((ActionEvent e) -> loginUser());

        // ========= REGISTER BUTTON (CORRECTLY ADDED) =========
        gbc.gridy++;
        gbc.insets = new Insets(10, 30, 20, 30);

        JButton registerBtn = new JButton("New Citizen? Register Here");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(0, 102, 204));
        registerBtn.setFocusPainted(false);

        card.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            dispose();
            new CitizenRegistrationRequestForm();
        });
        // ========= Back To Home Button =========
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 20, 20);

        JButton backBtn = new JButton("← Back To Home");
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(120, 120, 120));
        backBtn.setFont(new Font("Arial", Font.PLAIN, 13));

        card.add(backBtn, gbc);

        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });


        setVisible(true);
    }

    // ================= LOGIN LOGIC =================
    private void loginUser() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String table = role.equals("Admin") ? "admin" : "citizen";

            PreparedStatement ps =
                    con.prepareStatement("SELECT * FROM " + table +
                            " WHERE username=? AND password=?");

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                SessionManager.login(username, role);
                dispose();

                if (role.equals("Admin")) {
                    new Dashboard(username);
                } else {
                    new CitizenDashboard(username);
                }

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Credentials!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database Error!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
