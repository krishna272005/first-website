import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class CitizenDashboard extends JFrame {

    String username;
    Color primaryOrange = new Color(255, 102, 0);
    Color lightBg = new Color(245, 245, 245);

    public CitizenDashboard(String username) {

        this.username = username;

        setTitle("Citizen Dashboard - Gram Panchayat");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 40, 10, 40));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("🏛 Citizen Portal - Gram Panchayat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        navPanel.setBackground(Color.WHITE);

        JButton homeBtn = createNavButton("Home", false);
        JButton certBtn = createNavButton("Apply Certificate", false);
        JButton schemeBtn = createNavButton("Apply Scheme", false);
        JButton trackBtn = createNavButton("Track Certificates", false);
        JButton complaintBtn = createNavButton("Post Complaint", false);
        JButton noticeBtn = createNavButton("View Notices", false);
        JButton logoutBtn = createNavButton("Logout", true);

        navPanel.add(homeBtn);
        navPanel.add(certBtn);
        navPanel.add(schemeBtn);
        navPanel.add(trackBtn);
        navPanel.add(complaintBtn);
        navPanel.add(noticeBtn);
        navPanel.add(logoutBtn);

        header.add(title, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(lightBg);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(50, 200, 50, 200));

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage your certificates and schemes easily");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(welcome);
        mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0,40)));

        // ================= SEARCH SECTION =================
        JPanel searchSection = new JPanel();
        searchSection.setBackground(Color.WHITE);
        searchSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220), 2),
                new EmptyBorder(30,40,30,40)
        ));
        searchSection.setLayout(new GridBagLayout());
        searchSection.setMaximumSize(new Dimension(600, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel searchTitle = new JLabel("📄 Get Certificate by Application Number");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JTextField idField = new JTextField(20);
        idField.setPreferredSize(new Dimension(250, 35));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(primaryOrange);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(100,35));

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        searchSection.add(searchTitle, gbc);

        gbc.gridy++;
        gbc.gridwidth=1;
        searchSection.add(idField, gbc);

        gbc.gridx=1;
        searchSection.add(searchBtn, gbc);

        mainPanel.add(searchSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0,40)));

        // ================= QUICK ACTIONS =================
        JButton quickApply = new JButton("Quick Apply Certificate");
        quickApply.setBackground(primaryOrange);
        quickApply.setForeground(Color.WHITE);
        quickApply.setFocusPainted(false);
        quickApply.setAlignmentX(Component.CENTER_ALIGNMENT);
        quickApply.setMaximumSize(new Dimension(250,45));

        JButton quickScheme = new JButton("Quick Apply Scheme");
        quickScheme.setBackground(primaryOrange);
        quickScheme.setForeground(Color.WHITE);
        quickScheme.setFocusPainted(false);
        quickScheme.setAlignmentX(Component.CENTER_ALIGNMENT);
        quickScheme.setMaximumSize(new Dimension(250,45));

        mainPanel.add(quickApply);
        mainPanel.add(Box.createRigidArea(new Dimension(0,15)));
        mainPanel.add(quickScheme);

        add(mainPanel, BorderLayout.CENTER);

        // ================= ACTIONS =================

        homeBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        certBtn.addActionListener(e -> showCertificateOptions());
        quickApply.addActionListener(e -> showCertificateOptions());

        schemeBtn.addActionListener(e -> {
            dispose();
            new SchemeSelectionPage(username);
        });

        quickScheme.addActionListener(e -> {
            dispose();
            new SchemeSelectionPage(username);
        });

        trackBtn.addActionListener(e -> {
            dispose();
            new ViewCertificates(username);
        });

        complaintBtn.addActionListener(e -> {
            dispose();
            new CitizenComplaintForm(username);
        });

        noticeBtn.addActionListener(e -> {
            new CitizenViewNotices();
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        // ================= SEARCH LOGIC =================
        searchBtn.addActionListener(e -> {
            try {

                String input = idField.getText().trim();

                if (input.isEmpty()) {
                    showColoredMessage("Please enter Certificate ID.",
                            "Input Required",
                            new Color(255, 193, 7));
                    return;
                }

                Connection con = DBConnection.getConnection();
                PreparedStatement ps =
                        con.prepareStatement("SELECT status FROM certificate WHERE id=?");

                ps.setInt(1, Integer.parseInt(input));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    String status = rs.getString("status");

                    if (status.equalsIgnoreCase("Approved")) {

                        showColoredMessage(
                                "Certificate Status: " + status +
                                        "\n\nYou can download your certificate\nfrom 'Track Certificates' option.",
                                "Approved",
                                new Color(40, 167, 69)
                        );

                    } else if (status.equalsIgnoreCase("Pending")) {

                        showColoredMessage(
                                "Certificate Status: " + status,
                                "Pending",
                                new Color(255, 140, 0)
                        );

                    } else if (status.equalsIgnoreCase("Rejected")) {

                        showColoredMessage(
                                "Certificate Status: " + status,
                                "Rejected",
                                new Color(220, 53, 69)
                        );

                    } else {

                        JOptionPane.showMessageDialog(this,
                                "Certificate Status: " + status);
                    }

                } else {

                    showColoredMessage("Certificate Not Found!",
                            "Not Found",
                            new Color(220, 53, 69));
                }

                rs.close();
                ps.close();
                con.close();

            } catch (NumberFormatException ex) {

                showColoredMessage("Certificate ID must be numeric.",
                        "Invalid Input",
                        new Color(220, 53, 69));

            } catch (Exception ex) {

                showColoredMessage("Database Error!",
                        "Error",
                        new Color(220, 53, 69));
            }
        });

        setVisible(true);
    }

    // ================= NAV BUTTON =================
    private JButton createNavButton(String text, boolean highlight) {

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    // ================= COLORED POPUP =================
    private void showColoredMessage(String message, String title, Color bgColor) {

        JPanel panel = new JPanel();
        panel.setBackground(bgColor);

        panel.add(new JLabel("<html><div style='color:white; padding:10px;'>"
                + message.replace("\n", "<br>") + "</div></html>"));

        JOptionPane.showMessageDialog(this, panel, title,
                JOptionPane.PLAIN_MESSAGE);
    }

    // ================= CERTIFICATE POPUP =================
    private void showCertificateOptions() {

        String[] options = {
                "Birth Certificate",
                "Death Certificate",
                "Residence Certificate",
                "Income Certificate"
        };

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Certificate Type:",
                "Certificate Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if(selected != null) {

            dispose();

            switch(selected) {

                case "Birth Certificate":
                    new BirthCertificateForm(username);
                    break;

                case "Death Certificate":
                    new DeathCertificateForm(username);
                    break;

                case "Residence Certificate":
                    new ResidenceCertificateForm(username);
                    break;

                case "Income Certificate":
                    new IncomeCertificateForm(username);
                    break;
            }
        }
    }
}