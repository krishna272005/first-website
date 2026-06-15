import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class AdminPostNotice extends JFrame {

    JTextField titleField, expiryField;
    JTextArea messageArea;
    JButton postBtn;

    public AdminPostNotice() {

        setTitle("Post Notice");
        setSize(400,400);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Notice Title:");
        titleLabel.setBounds(30,30,100,25);
        add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(140,30,200,25);
        add(titleField);

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setBounds(30,80,100,25);
        add(messageLabel);

        messageArea = new JTextArea();
        JScrollPane pane = new JScrollPane(messageArea);
        pane.setBounds(140,80,200,100);
        add(pane);

        JLabel expiryLabel = new JLabel("Expiry Date:");
        expiryLabel.setBounds(30,200,100,25);
        add(expiryLabel);

        expiryField = new JTextField();
        expiryField.setBounds(140,200,200,25);
        expiryField.setToolTipText("YYYY-MM-DD");
        add(expiryField);

        postBtn = new JButton("Post Notice");
        postBtn.setBounds(140,250,150,30);
        add(postBtn);

        postBtn.addActionListener(e -> postNotice());

        setVisible(true);
    }

    void postNotice() {
        try {

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO notices(title,message,post_date,expiry_date) VALUES(?,?,CURDATE(),?)";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1,titleField.getText());
            pst.setString(2,messageArea.getText());
            pst.setString(3,expiryField.getText());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,"Notice Posted Successfully");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}