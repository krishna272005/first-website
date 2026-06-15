import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CitizenLoginFrame extends JFrame {

    JTextField userField;
    JPasswordField passField;

    public CitizenLoginFrame() {

        setTitle("Citizen Login - Gram Panchayat");
        setSize(400,300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel title = new JLabel("Citizen Login");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(130,20,200,30);
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50,80,100,30);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(150,80,150,30);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50,130,100,30);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(150,130,150,30);
        add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(80,190,100,30);
        add(loginBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(200,190,100,30);
        add(backBtn);

        // Login Button Action
        loginBtn.addActionListener(e -> loginCitizen());

        // Back Button Action
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private void loginCitizen() {

        String username = userField.getText();
        String password = new String(passField.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please fill all fields!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM citizen WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                JOptionPane.showMessageDialog(this,"Login Successful!");

                dispose(); // close login page
                new CitizenDashboard(username); // open dashboard

            } else {
                JOptionPane.showMessageDialog(this,"Invalid Username or Password!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Database Error!");
        }
    }
}
