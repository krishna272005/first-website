import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    Color primaryOrange = new Color(255, 102, 0);
    Color lightBg = new Color(245, 245, 245);

    public Dashboard(String adminName) {

        if (!SessionManager.isLoggedIn() ||
                !SessionManager.getRole().equals("Admin")) {

            JOptionPane.showMessageDialog(this, "Unauthorized Access!");
            dispose();
            new LoginFrame();
            return;
        }

        setTitle("Admin Dashboard - Gram Panchayat");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= NAVBAR =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 40, 10, 40));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("🏛 Gram Panchayat - Admin Panel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        navPanel.setBackground(Color.WHITE);

        JButton complaintBtn = createNavButton("Complaints", false);
        JButton certificateBtn = createNavButton("Certificate Approval", false);
        JButton schemeBtn = createNavButton("Scheme Approval", false);
        JButton reportsBtn = createNavButton("Reports", false);

        // ⭐ Post Notice with highlight color
        JButton noticeBtn = createNavButton("Post Notice", true);

        JButton logoutBtn = createNavButton("Logout", true);

        navPanel.add(complaintBtn);
        navPanel.add(certificateBtn);
        navPanel.add(schemeBtn);
        navPanel.add(reportsBtn);
        navPanel.add(noticeBtn);
        navPanel.add(logoutBtn);

        header.add(title, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ================= MAIN CENTER PANEL =================
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(lightBg);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 100, 40, 100));

        JLabel actionTitle = new JLabel("Admin Quick Actions");
        actionTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        actionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(actionTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        JPanel actionPanel = new JPanel(new GridLayout(1,2,30,30));
        actionPanel.setBackground(lightBg);

        JButton registerBtn = createActionCard("Register Citizen");
        JButton manageBtn = createActionCard("Manage Citizens");

        actionPanel.add(registerBtn);
        actionPanel.add(manageBtn);

        mainPanel.add(actionPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,50)));

        // ================= STATISTICS =================
        JLabel statsTitle = new JLabel("System Statistics");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        statsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(statsTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        JPanel statsPanel = new JPanel(new GridLayout(2,3,30,30));
        statsPanel.setBackground(lightBg);

        JLabel totalCitizens = createStatCard("Total Citizens", new Color(0,123,255));
        JLabel totalApps = createStatCard("Total Applications", new Color(108,117,125));
        JLabel approved = createStatCard("Approved Certificates", new Color(40,167,69));
        JLabel rejected = createStatCard("Rejected Certificates", new Color(220,53,69));
        JLabel pending = createStatCard("Pending Applications", new Color(255,140,0));
        JLabel complaints = createStatCard("Total Complaints", new Color(111,66,193));

        statsPanel.add(totalCitizens);
        statsPanel.add(totalApps);
        statsPanel.add(approved);
        statsPanel.add(rejected);
        statsPanel.add(pending);
        statsPanel.add(complaints);

        mainPanel.add(statsPanel);

        add(mainPanel, BorderLayout.CENTER);

        loadStatistics(totalCitizens, totalApps,
                approved, rejected, pending, complaints);

        // ================= ACTIONS =================

        complaintBtn.addActionListener(e -> {
            dispose();
            new AdminComplaintView();
        });

        certificateBtn.addActionListener(e -> {
            dispose();
            new AdminCertificateApproval();
        });

        schemeBtn.addActionListener(e -> {
            dispose();
            new AdminSchemeApproval();
        });

        reportsBtn.addActionListener(e -> {
            dispose();
            new AdminReports();
        });

        noticeBtn.addActionListener(e -> {
            new AdminPostNotice();
        });

        registerBtn.addActionListener(e -> {
            dispose();
            new CitizenForm();
        });

        manageBtn.addActionListener(e -> {
            dispose();
            new AdminCitizenManagement();
        });

        logoutBtn.addActionListener(e -> {
            SessionManager.clearSession();
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    // ================= NAV BUTTON =================
    private JButton createNavButton(String text, boolean highlight) {

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (highlight) {

            btn.setBackground(primaryOrange);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));

        } else {

            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
        }

        return btn;
    }

    // ================= ACTION CARD =================
    private JButton createActionCard(String text) {

        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        btn.setPreferredSize(new Dimension(250,120));

        return btn;
    }

    // ================= STAT CARD =================
    private JLabel createStatCard(String title, Color color) {

        JLabel label = new JLabel(title + ": 0", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(30,20,30,20));

        return label;
    }

    // ================= LOAD STATS =================
    private void loadStatistics(JLabel totalCitizens,
                                JLabel totalApps,
                                JLabel approved,
                                JLabel rejected,
                                JLabel pending,
                                JLabel complaints) {

        try {

            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM citizen");
            rs1.next();
            totalCitizens.setText("Total Citizens: " + rs1.getInt(1));

            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM certificate");
            rs2.next();
            totalApps.setText("Total Applications: " + rs2.getInt(1));

            ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM certificate WHERE status='Approved'");
            rs3.next();
            approved.setText("Approved Certificates: " + rs3.getInt(1));

            ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM certificate WHERE status='Rejected'");
            rs4.next();
            rejected.setText("Rejected Certificates: " + rs4.getInt(1));

            ResultSet rs5 = st.executeQuery("SELECT COUNT(*) FROM certificate WHERE status='Pending'");
            rs5.next();
            pending.setText("Pending Applications: " + rs5.getInt(1));

            ResultSet rs6 = st.executeQuery("SELECT COUNT(*) FROM complaint");
            rs6.next();
            complaints.setText("Total Complaints: " + rs6.getInt(1));

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}