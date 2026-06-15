import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CitizenForm extends JFrame {

    JTextField nameField, addressField, mobileField, userField;
    JPasswordField passField;

    public CitizenForm() {

        // ===== SECURITY CHECK =====
        if(!SessionManager.isLoggedIn() ||
                !SessionManager.getRole().equals("Admin")) {

            JOptionPane.showMessageDialog(this,"Unauthorized Access!");
            dispose();
            new LoginFrame();
            return;
        }

        setTitle("Citizen Registration (Admin)");
        setSize(450,420);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240,248,255));

        JLabel title = new JLabel("Register New Citizen");
        title.setFont(new Font("Arial",Font.BOLD,18));
        title.setBounds(120,10,250,30);
        add(title);

        addLabel("Name:",30,50);
        nameField = addTextField(150,50);

        addLabel("Address:",30,90);
        addressField = addTextField(150,90);

        addLabel("Mobile (10 digits):",30,130);
        mobileField = addTextField(150,130);

        addLabel("Username:",30,170);
        userField = addTextField(150,170);

        addLabel("Password:",30,210);
        passField = new JPasswordField();
        passField.setBounds(150,210,200,30);
        add(passField);

        JButton saveBtn = new JButton("Register Citizen");
        saveBtn.setBounds(80,270,150,35);
        add(saveBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(240,270,120,35);
        add(backBtn);

        saveBtn.addActionListener(e -> saveCitizen());

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(SessionManager.getUsername());
        });

        setVisible(true);
    }

    // ===== SAVE METHOD =====
    private void saveCitizen() {

        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String mobile = mobileField.getText().trim();
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        // ===== VALIDATIONS =====
        if(name.isEmpty() || address.isEmpty() ||
                mobile.isEmpty() || username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this,"All fields are required!");
            return;
        }

        if(!mobile.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this,"Mobile must be exactly 10 digits!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            // ===== CHECK DUPLICATE USERNAME =====
            PreparedStatement checkPs = con.prepareStatement(
                    "SELECT * FROM citizen WHERE username=?"
            );
            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();

            if(rs.next()) {
                JOptionPane.showMessageDialog(this,"Username already exists!");
                return;
            }

            // ===== INSERT NEW CITIZEN =====
            String query = "INSERT INTO citizen(name,address,mobile,username,password) VALUES(?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, mobile);
            ps.setString(4, username);
            ps.setString(5, password);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Citizen Registered Successfully!");

            clearFields();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ===== HELPER METHODS =====
    private void addLabel(String text,int x,int y){
        JLabel label = new JLabel(text);
        label.setBounds(x,y,150,30);
        add(label);
    }

    private JTextField addTextField(int x,int y){
        JTextField tf = new JTextField();
        tf.setBounds(x,y,200,30);
        add(tf);
        return tf;
    }

    private void clearFields(){
        nameField.setText("");
        addressField.setText("");
        mobileField.setText("");
        userField.setText("");
        passField.setText("");
    }
}
