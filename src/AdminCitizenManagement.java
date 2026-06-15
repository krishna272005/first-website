import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminCitizenManagement extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField searchField;

    public AdminCitizenManagement() {

        if(!SessionManager.isLoggedIn() ||
                !SessionManager.getRole().equals("Admin")) {

            JOptionPane.showMessageDialog(this,"Unauthorized Access!");
            dispose();
            new LoginFrame();
            return;
        }

        setTitle("Citizen Management - Admin");
        setSize(900,550);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240,248,255));

        JLabel title = new JLabel("Citizen Management Panel");
        title.setFont(new Font("Arial",Font.BOLD,22));
        title.setBounds(300,20,400,30);
        add(title);

        // Search Field
        searchField = new JTextField();
        searchField.setBounds(200,70,300,30);
        add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(520,70,100,30);
        add(searchBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(640,70,100,30);
        add(refreshBtn);

        // Table
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Address");
        model.addColumn("Mobile");
        model.addColumn("Username");

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50,120,800,300);
        add(scroll);

        // Buttons
        JButton editBtn = new JButton("Edit");
        editBtn.setBounds(150,450,120,35);
        add(editBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(300,450,120,35);
        add(deleteBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(450,450,120,35);
        add(backBtn);

        // Load data
        loadData();

        // Actions
        searchBtn.addActionListener(e -> searchCitizen());
        refreshBtn.addActionListener(e -> loadData());
        deleteBtn.addActionListener(e -> deleteCitizen());
        editBtn.addActionListener(e -> editCitizen());

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(SessionManager.getUsername());
        });

        setVisible(true);
    }

    // ================= LOAD ALL CITIZENS =================
    private void loadData() {
        try {
            model.setRowCount(0);

            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement("SELECT * FROM citizen");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("mobile"),
                        rs.getString("username")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= SEARCH =================
    private void searchCitizen() {
        try {
            model.setRowCount(0);

            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(
                            "SELECT * FROM citizen WHERE username LIKE ?");
            ps.setString(1, "%" + searchField.getText() + "%");

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("mobile"),
                        rs.getString("username")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= DELETE =================
    private void deleteCitizen() {

        int row = table.getSelectedRow();

        if(row == -1) {
            JOptionPane.showMessageDialog(this,"Select citizen first!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to delete?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm != JOptionPane.YES_OPTION)
            return;

        int id = (int) model.getValueAt(row,0);

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(
                            "DELETE FROM citizen WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Deleted Successfully!");
            loadData();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= EDIT =================
    private void editCitizen() {

        int row = table.getSelectedRow();

        if(row == -1) {
            JOptionPane.showMessageDialog(this,"Select citizen first!");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        new EditCitizenForm(id);

        // Auto refresh after edit window closes
        loadData();
    }
}
