import javax.swing.*;
import java.sql.*;

public class CitizenComplaintForm extends JFrame {

    String username;
    JTextArea complaintArea;

    public CitizenComplaintForm(String username) {
        this.username = username;

        setTitle("Post Complaint");
        setSize(400,350);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel l1 = new JLabel("Enter Complaint:");
        l1.setBounds(30,30,150,30);
        add(l1);

        complaintArea = new JTextArea();
        complaintArea.setBounds(30,70,320,120);
        add(complaintArea);

        JButton postBtn = new JButton("Post");
        postBtn.setBounds(80,220,100,30);
        add(postBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(200,220,100,30);
        add(backBtn);

        postBtn.addActionListener(e -> postComplaint());

        backBtn.addActionListener(e -> {
            dispose();
            new CitizenDashboard(username);
        });

        setVisible(true);
    }

    private void postComplaint() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO complaint(citizen_name,complaint) VALUES(?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, complaintArea.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Complaint Submitted");

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
