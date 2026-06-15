import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminSchemeApproval extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextArea remarksArea;

    public AdminSchemeApproval() {

        setTitle("Scheme Applications - Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color mainColor = new Color(236, 242, 248);

        // ================= HEADER =================
        JLabel title = new JLabel("  Scheme Applications Management", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,25,20,0));
        add(title, BorderLayout.NORTH);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Citizen");
        model.addColumn("Scheme");
        model.addColumn("Name");
        model.addColumn("Mobile");
        model.addColumn("Income");
        model.addColumn("Status");

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM PANEL =================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(mainColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15,40,25,40));

        // ===== Remarks Section (Moved Up & Styled) =====
        JPanel remarksPanel = new JPanel(new BorderLayout());
        remarksPanel.setBackground(mainColor);

        JLabel remarksLabel = new JLabel("Admin Remarks:");
        remarksLabel.setFont(new Font("Arial", Font.BOLD, 15));

        remarksArea = new JTextArea(3, 30);
        remarksArea.setFont(new Font("Arial", Font.PLAIN, 14));
        remarksArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JScrollPane remarksScroll = new JScrollPane(remarksArea);

        remarksPanel.add(remarksLabel, BorderLayout.NORTH);
        remarksPanel.add(remarksScroll, BorderLayout.CENTER);

        bottomPanel.add(remarksPanel, BorderLayout.CENTER);

        // ===== Button Panel (Better Alignment) =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(mainColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));

        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");
        JButton backBtn = new JButton("Back");

        styleButton(approveBtn, new Color(0,150,0));
        styleButton(rejectBtn, new Color(200,0,0));
        styleButton(refreshBtn, new Color(0,102,204));
        styleButton(backBtn, new Color(120,120,120));

        buttonPanel.add(approveBtn);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(rejectBtn);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(backBtn);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ================= LOAD DATA =================
        loadData();

        // ================= BUTTON ACTIONS =================
        approveBtn.addActionListener(e -> updateStatus("Approved"));
        rejectBtn.addActionListener(e -> updateStatus("Rejected"));
        refreshBtn.addActionListener(e -> loadData());

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(SessionManager.getUsername());
        });

        setVisible(true);
    }

    // ================= LOAD DATA =================
    private void loadData() {

        try {
            model.setRowCount(0);

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM scheme_applications");

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("citizen_username"),
                        rs.getString("scheme_type"),
                        rs.getString("full_name"),
                        rs.getString("mobile"),
                        rs.getDouble("income"),
                        rs.getString("status")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= UPDATE STATUS =================
    private void updateStatus(String status) {

        int selectedRow = table.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this,"Select application first!");
            return;
        }

        int id = (int) model.getValueAt(selectedRow,0);
        String remarks = remarksArea.getText();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE scheme_applications SET status=?, remarks=? WHERE id=?"
            );

            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setInt(3, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Status Updated Successfully!");
            remarksArea.setText("");
            loadData();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= BUTTON STYLE =================
    private void styleButton(JButton button, Color bgColor) {

        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(130, 40));
        button.setBorder(BorderFactory.createEmptyBorder());
    }
}
