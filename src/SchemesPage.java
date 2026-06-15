import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SchemesPage extends JFrame {

    JPanel gridPanel;

    public SchemesPage() {

        setTitle("Government Schemes");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color primaryOrange = new Color(255, 102, 0);
        Color lightGray = new Color(240, 240, 240);

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(primaryOrange);
        header.setPreferredSize(new Dimension(1200, 100));
        header.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Government Schemes");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== GRID PANEL =====
        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(lightGray);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        loadSchemesFromDB();

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        add(scrollPane, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel footer = new JPanel();
        JButton backBtn = new JButton("Back to Home");
        footer.add(backBtn);
        add(footer, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        setVisible(true);
    }

    private void loadSchemesFromDB() {

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM schemes");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String title = rs.getString("title");
                String desc = rs.getString("description");
                String eligibility = rs.getString("eligibility");
                String benefits = rs.getString("benefits");

                gridPanel.add(createSchemeCard(title, desc, eligibility, benefits));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createSchemeCard(String titleText,
                                    String description,
                                    String eligibility,
                                    String benefits) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea details = new JTextArea(
                "Description: " + description + "\n\n" +
                        "Eligibility: " + eligibility + "\n\n" +
                        "Benefits: " + benefits
        );

        details.setWrapStyleWord(true);
        details.setLineWrap(true);
        details.setEditable(false);
        details.setBackground(Color.WHITE);
        details.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        card.add(title, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);

        return card;
    }
}
