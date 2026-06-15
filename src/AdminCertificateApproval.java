import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminCertificateApproval extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextArea remarksArea;

    public AdminCertificateApproval() {

        setTitle("Certificate Approval - Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= TITLE =================
        JLabel title = new JLabel("Certificate Applications", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(20,0,20,0));
        add(title, BorderLayout.NORTH);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID",
                "Citizen Username",
                "Certificate Type",
                "Application Date",
                "Status",
                "Remarks"
        });

        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM PANEL =================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(20,40,20,40));

        JPanel remarksPanel = new JPanel(new BorderLayout());

        JLabel remarksLabel = new JLabel("Admin Remarks:");
        remarksLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        remarksArea = new JTextArea(3, 30);
        remarksArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);

        JScrollPane remarksScroll = new JScrollPane(remarksArea);

        remarksPanel.add(remarksLabel, BorderLayout.NORTH);
        remarksPanel.add(remarksScroll, BorderLayout.CENTER);

        bottomPanel.add(remarksPanel, BorderLayout.CENTER);

        // ================= BUTTONS =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton backBtn = new JButton("Back to Dashboard");

        styleButton(approveBtn, new Color(40,167,69));
        styleButton(rejectBtn, new Color(220,53,69));
        styleButton(backBtn, new Color(108,117,125));

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(backBtn);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        loadCertificates();

        // ================= ACTIONS =================
        approveBtn.addActionListener(e -> updateStatus("Approved"));
        rejectBtn.addActionListener(e -> updateStatus("Rejected"));

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(SessionManager.getUsername());
        });

        setVisible(true);
    }

    // ================= LOAD DATA =================
    private void loadCertificates() {

        model.setRowCount(0);

        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT id, citizen_username, certificate_type, application_date, status, remarks FROM certificate"
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("citizen_username"),
                        rs.getString("certificate_type"),
                        rs.getDate("application_date"),
                        rs.getString("status"),
                        rs.getString("remarks")
                });
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= UPDATE STATUS =================
    private void updateStatus(String status) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an application first.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String remarks = remarksArea.getText();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE certificate SET status=?, remarks=? WHERE id=?"
            );

            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setInt(3, id);
            ps.executeUpdate();

            con.close();

            JOptionPane.showMessageDialog(this,
                    "Application " + status + " successfully!");

            loadCertificates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= BUTTON STYLE =================
    private void styleButton(JButton btn, Color color) {

        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 40));
    }
}
