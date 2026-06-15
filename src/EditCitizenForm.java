import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditCitizenForm extends JFrame {

    JTextField nameField, addressField, mobileField, usernameField;
    int citizenId;

    public EditCitizenForm(int id) {

        this.citizenId = id;

        setTitle("Edit Citizen");
        setSize(400,350);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240,248,255));

        JLabel title = new JLabel("Edit Citizen Details");
        title.setFont(new Font("Arial",Font.BOLD,16));
        title.setBounds(100,10,250,30);
        add(title);

        addLabel("Name:",30,60);
        nameField = addTextField(150,60);

        addLabel("Address:",30,100);
        addressField = addTextField(150,100);

        addLabel("Mobile:",30,140);
        mobileField = addTextField(150,140);

        addLabel("Username:",30,180);
        usernameField = addTextField(150,180);

        JButton updateBtn = new JButton("Update");
        updateBtn.setBounds(80,230,120,35);
        add(updateBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(220,230,120,35);
        add(cancelBtn);

        loadCitizenData();

        updateBtn.addActionListener(e -> updateCitizen());

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadCitizenData() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement("SELECT * FROM citizen WHERE id=?");
            ps.setInt(1,citizenId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                nameField.setText(rs.getString("name"));
                addressField.setText(rs.getString("address"));
                mobileField.setText(rs.getString("mobile"));
                usernameField.setText(rs.getString("username"));
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateCitizen() {

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(
                            "UPDATE citizen SET name=?, address=?, mobile=?, username=? WHERE id=?");

            ps.setString(1,nameField.getText());
            ps.setString(2,addressField.getText());
            ps.setString(3,mobileField.getText());
            ps.setString(4,usernameField.getText());
            ps.setInt(5,citizenId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Updated Successfully!");
            dispose();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addLabel(String text,int x,int y){
        JLabel label = new JLabel(text);
        label.setBounds(x,y,100,30);
        add(label);
    }

    private JTextField addTextField(int x,int y){
        JTextField tf = new JTextField();
        tf.setBounds(x,y,200,30);
        add(tf);
        return tf;
    }
}
