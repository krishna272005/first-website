import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminCitizenRequests extends JFrame {

    JTable table;
    DefaultTableModel model;

    public AdminCitizenRequests() {

        setTitle("Citizen Registration Requests");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Username");
        model.addColumn("Status");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton approveBtn = new JButton("Approve");
        add(approveBtn, BorderLayout.SOUTH);

        loadData();

        approveBtn.addActionListener(e -> approveCitizen());

        setVisible(true);
    }

    private void loadData(){
        try{
            model.setRowCount(0);
            Connection con = DBConnection.getConnection();
            ResultSet rs =
                    con.createStatement().executeQuery(
                            "SELECT * FROM citizen_requests WHERE status='Pending'");

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("status")
                });
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void approveCitizen(){

        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select request first!");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        try{
            Connection con = DBConnection.getConnection();

            ResultSet rs =
                    con.createStatement().executeQuery(
                            "SELECT * FROM citizen_requests WHERE id="+id);

            if(rs.next()){

                PreparedStatement ps =
                        con.prepareStatement(
                                "INSERT INTO citizen(name,address,mobile,username,password) VALUES(?,?,?,?,?)");

                ps.setString(1, rs.getString("name"));
                ps.setString(2, rs.getString("address"));
                ps.setString(3, rs.getString("mobile"));
                ps.setString(4, rs.getString("username"));
                ps.setString(5, rs.getString("password"));
                ps.executeUpdate();

                con.createStatement().executeUpdate(
                        "UPDATE citizen_requests SET status='Approved' WHERE id="+id);

                JOptionPane.showMessageDialog(this,"Citizen Approved!");
                loadData();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
