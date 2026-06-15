import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SchemeApplicationForm extends JFrame {

    public SchemeApplicationForm(String username, String schemeType) {

        setTitle("Apply for " + schemeType);
        setSize(500,500);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("Apply for " + schemeType);
        title.setBounds(150,20,300,30);
        add(title);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50,80,100,30);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(180,80,200,30);
        add(nameField);

        JLabel mobileLabel = new JLabel("Mobile:");
        mobileLabel.setBounds(50,130,100,30);
        add(mobileLabel);

        JTextField mobileField = new JTextField();
        mobileField.setBounds(180,130,200,30);
        add(mobileField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50,180,100,30);
        add(addressLabel);

        JTextField addressField = new JTextField();
        addressField.setBounds(180,180,200,30);
        add(addressField);

        JLabel incomeLabel = new JLabel("Income:");
        incomeLabel.setBounds(50,230,100,30);
        add(incomeLabel);

        JTextField incomeField = new JTextField();
        incomeField.setBounds(180,230,200,30);
        add(incomeField);

        JButton submitBtn = new JButton("Submit Application");
        submitBtn.setBounds(150,300,200,40);
        add(submitBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(150,360,200,40);
        add(backBtn);

        submitBtn.addActionListener(e -> {

            try {
                Connection con = DBConnection.getConnection();

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO scheme_applications (citizen_username, scheme_type, full_name, mobile, address, income) VALUES (?,?,?,?,?,?)"
                );

                ps.setString(1, username);
                ps.setString(2, schemeType);
                ps.setString(3, nameField.getText());
                ps.setString(4, mobileField.getText());
                ps.setString(5, addressField.getText());
                ps.setDouble(6, Double.parseDouble(incomeField.getText()));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Application Submitted Successfully!");

                dispose();
                new CitizenDashboard(username);

            } catch(Exception ex){
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new SchemeSelectionPage(username);
        });

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
