import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AdminComplaintView extends JFrame {

    JTable table;
    DefaultTableModel model;

    public AdminComplaintView() {

        setTitle("Admin - Complaint Management");
        setSize(750,400);
        setLayout(null);
        setLocationRelativeTo(null);

        // ✅ IMPORTANT
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Citizen");
        model.addColumn("Complaint");
        model.addColumn("Status");

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(30,30,680,200);
        add(scroll);

        JButton resolveBtn = new JButton("Mark as Resolved");
        resolveBtn.setBounds(150,260,160,30);
        add(resolveBtn);

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setBounds(330,260,120,30);
        add(rejectBtn);

        JButton homeBtn = new JButton("Home");
        homeBtn.setBounds(300,320,120,30);
        add(homeBtn);

        homeBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(480,260,120,30);
        add(backBtn);

        loadComplaints();

        resolveBtn.addActionListener(e -> updateStatus("Resolved"));
        rejectBtn.addActionListener(e -> updateStatus("Rejected"));

        backBtn.addActionListener(e -> {
            dispose();              // close this window
            new Dashboard(SessionManager.getUsername());
            // reopen admin dashboard
        });

        setVisible(true);
    }

    private void loadComplaints() {
        try {
            model.setRowCount(0);

            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM complaint";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("citizen_name"),
                        rs.getString("complaint"),
                        rs.getString("status")
                });
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(String status) {

        int selectedRow = table.getSelectedRow();

        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Select complaint first!");
            return;
        }

        int id = (int) model.getValueAt(selectedRow,0);

        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE complaint SET status=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Status Updated!");
            loadComplaints();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
