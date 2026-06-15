import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class HomePage extends JFrame {

    public HomePage() {

        setTitle("Mahagaon Grampanchayat - Digital Portal");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color primaryOrange = new Color(255, 102, 0);
        Color lightBg = new Color(245, 245, 245);

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 40, 10, 40));
        header.setPreferredSize(new Dimension(100, 70));

        JLabel title = new JLabel("🏛 Mahagaon Grampanchayat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        navPanel.setBackground(Color.WHITE);

        JButton homeBtn = createNavButton("Home", false);
        JButton schemesBtn = createNavButton("Schemes", false);
        JButton servicesBtn = createNavButton("Services", false);
        JButton contactBtn = createNavButton("Contact", false);
        JButton loginBtn = createNavButton("Login", true); // Highlighted

        navPanel.add(homeBtn);
        navPanel.add(schemesBtn);
        navPanel.add(servicesBtn);
        navPanel.add(contactBtn);
        navPanel.add(loginBtn);

        header.add(title, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ================= MAIN CONTAINER =================
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(lightBg);

        // ================= HERO SECTION =================
        JPanel hero = new JPanel();
        hero.setBackground(primaryOrange);
        hero.setPreferredSize(new Dimension(100, 250));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));

        JLabel heroTitle = new JLabel("Mahagaon Grampanchayat");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heroTitle.setForeground(Color.WHITE);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heroSub = new JLabel("Digital Services Portal");
        heroSub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        heroSub.setForeground(Color.WHITE);
        heroSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        hero.add(Box.createVerticalGlue());
        hero.add(heroTitle);
        hero.add(Box.createRigidArea(new Dimension(0, 10)));
        hero.add(heroSub);
        hero.add(Box.createVerticalGlue());

        container.add(hero);

        // ================= SCHEME SECTION =================
        JPanel schemeSection = new JPanel();
        schemeSection.setBackground(lightBg);
        schemeSection.setBorder(new EmptyBorder(40, 80, 40, 80));
        schemeSection.setLayout(new BorderLayout());

        JLabel schemeTitle = new JLabel("Government Schemes (Yojanas)", JLabel.CENTER);
        schemeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(lightBg);

        cardsPanel.add(createSchemeCard("Agriculture Subsidy", "Support for farmers."));
        cardsPanel.add(createSchemeCard("Toilet Construction", "Swachh Bharat Mission support."));
        cardsPanel.add(createSchemeCard("Education Scholarship", "Support for students."));

        schemeSection.add(schemeTitle, BorderLayout.NORTH);
        schemeSection.add(cardsPanel, BorderLayout.CENTER);

        container.add(schemeSection);

        // ================= CERTIFICATE SEARCH SECTION =================
        JPanel searchSection = new JPanel();
        searchSection.setBackground(Color.WHITE);
        searchSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(40, 250, 40, 250),
                BorderFactory.createLineBorder(new Color(220,220,220), 2)
        ));
        searchSection.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel searchTitle = new JLabel("📄 Get Certificate by Application Number");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JTextField idField = new JTextField(20);
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idField.setPreferredSize(new Dimension(250, 35));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(128, 0, 255));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(100, 35));

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        searchSection.add(searchTitle, gbc);

        gbc.gridy++;
        gbc.gridwidth=1;
        searchSection.add(idField, gbc);

        gbc.gridx=1;
        searchSection.add(searchBtn, gbc);

        container.add(searchSection);

        add(container, BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        footer.add(new JLabel("© 2026 Mahagaon Grampanchayat - All Rights Reserved"));
        add(footer, BorderLayout.SOUTH);

        // ================= ACTIONS =================

        homeBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "You are already on Home Page."));

        schemesBtn.addActionListener(e -> new SchemesPage());
        servicesBtn.addActionListener(e -> new PublicServicesPage());
        contactBtn.addActionListener(e -> new ContactPage());

        loginBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        searchBtn.addActionListener(e -> {
            try {
                String input = idField.getText().trim();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Enter Certificate ID");
                    return;
                }

                Connection con = DBConnection.getConnection();
                PreparedStatement ps =
                        con.prepareStatement("SELECT status FROM certificate WHERE id=?");

                ps.setInt(1, Integer.parseInt(input));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "Certificate Status: " + rs.getString("status"));
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Certificate Not Found!");
                }

                rs.close();
                ps.close();
                con.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid ID or Database Error");
            }
        });

        setVisible(true);
    }

    // ================= NAV BUTTON =================
    private JButton createNavButton(String text, boolean highlight) {

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (highlight) {
            btn.setBackground(new Color(255, 102, 0));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));
        } else {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
        }

        return btn;
    }

    // ================= SCHEME CARD =================
    private JPanel createSchemeCard(String title, String desc) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(10,10,5,10));

        JLabel descLabel = new JLabel("<html>"+desc+"</html>");
        descLabel.setBorder(new EmptyBorder(0,10,10,10));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);

        return card;
    }
}
