import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CitizenRegistrationRequestForm extends JFrame {

    JTextField nameField, addressField, mobileField, userField;
    JPasswordField passField;

    public CitizenRegistrationRequestForm() {

        setTitle("Citizen Registration Request");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(240,248,255));

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400,450));
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        add(panel);

        JLabel title = new JLabel("Citizen Registration Request");
        title.setFont(new Font("Arial",Font.BOLD,18));
        title.setBounds(60,20,300,30);
        panel.add(title);

        addLabel(panel,"Full Name",80);
        nameField = addField(panel,110);

        addLabel(panel,"Address",140);
        addressField = addField(panel,170);

        addLabel(panel,"Mobile",200);
        mobileField = addField(panel,230);

        addLabel(panel,"Username",260);
        userField = addField(panel,290);

        addLabel(panel,"Password",320);
        passField = new JPasswordField();
        passField.setBounds(50,350,300,30);
        panel.add(passField);

        JButton submitBtn = new JButton("Submit Request");
        submitBtn.setBounds(100,390,200,35);
        panel.add(submitBtn);

        submitBtn.addActionListener(e -> submitRequest());

        setVisible(true);
    }

    private void submitRequest() {

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(
                            "INSERT INTO citizen_requests(name,address,mobile,username,password) VALUES(?,?,?,?,?)");

            ps.setString(1, nameField.getText());
            ps.setString(2, addressField.getText());
            ps.setString(3, mobileField.getText());
            ps.setString(4, userField.getText());
            ps.setString(5, new String(passField.getPassword()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Registration Request Sent!\nWait for Admin Approval.");

            dispose();
            new LoginFrame();

        } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Username already exists!");
        }
    }

    private void addLabel(JPanel p, String text, int y){
        JLabel l = new JLabel(text);
        l.setBounds(50,y,200,20);
        p.add(l);
    }

    private JTextField addField(JPanel p, int y){
        JTextField f = new JTextField();
        f.setBounds(50,y,300,30);
        p.add(f);
        return f;
    }
}
