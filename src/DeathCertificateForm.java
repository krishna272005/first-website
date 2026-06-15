import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DeathCertificateForm extends JFrame {

    private JTextField applicantNameField, phoneField, emailField;
    private JTextArea applicantAddressArea;

    private JTextField fullNameField, fatherNameField;
    private JTextField dobField, dodField;
    private JTextArea deathAddressArea;

    private String username;

    public DeathCertificateForm(String username) {

        this.username = username;

        setTitle("Death Certificate Application");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30,200,30,200));
        mainPanel.setBackground(new Color(245,245,245));

        // ================= TITLE =================
        JLabel title = new JLabel("Create Death Certificate Application");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        // ================= APPLICANT PANEL =================
        JPanel applicantPanel = createSectionPanel("Applicant Information");

        applicantNameField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);
        applicantAddressArea = new JTextArea(3,15);

        addField(applicantPanel,"Applicant Name*", applicantNameField);
        addField(applicantPanel,"Phone*", phoneField);
        addField(applicantPanel,"Email", emailField);
        addTextArea(applicantPanel,"Address", applicantAddressArea);

        mainPanel.add(applicantPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        // ================= DEATH DETAILS PANEL =================
        JPanel deathPanel = createSectionPanel("Death Certificate Details");

        fullNameField = new JTextField(15);
        fatherNameField = new JTextField(15);
        dobField = new JTextField(15);
        dodField = new JTextField(15);
        deathAddressArea = new JTextArea(3,15);

        addField(deathPanel,"Full Name*", fullNameField);
        addField(deathPanel,"Father Name*", fatherNameField);
        addField(deathPanel,"Date of Birth (YYYY-MM-DD)*", dobField);
        addField(deathPanel,"Date of Death (YYYY-MM-DD)*", dodField);
        addTextArea(deathPanel,"Address*", deathAddressArea);

        mainPanel.add(deathPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        // ================= BUTTONS =================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(mainPanel.getBackground());

        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(submitBtn, new Color(0,123,255));
        styleButton(cancelBtn, new Color(108,117,125));

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel);

        add(new JScrollPane(mainPanel));

        // ================= ACTIONS =================
        submitBtn.addActionListener(e -> submitForm());
        cancelBtn.addActionListener(e -> {
            dispose();
            new CitizenDashboard(username);
        });

        setVisible(true);
    }

    // ================= SECTION PANEL =================
    private JPanel createSectionPanel(String title) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(900,400));

        return panel;
    }

    private void addField(JPanel panel, String labelText, Component field) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = panel.getComponentCount()/2;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addTextArea(JPanel panel, String labelText, JTextArea area) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = panel.getComponentCount()/2;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(new JScrollPane(area), gbc);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150,40));
    }

    // ================= SUBMIT =================
    private void submitForm() {

        try {

            if(fullNameField.getText().isEmpty() ||
                    dobField.getText().isEmpty() ||
                    dodField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(this,"Please fill required fields!");
                return;
            }

            LocalDate dob;
            LocalDate dod;

            try {
                dob = LocalDate.parse(dobField.getText());
                dod = LocalDate.parse(dodField.getText());
            } catch(DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Date Format! Use YYYY-MM-DD");
                return;
            }

            if(dod.isBefore(dob)) {
                JOptionPane.showMessageDialog(this,
                        "Date of Death cannot be before Date of Birth!");
                return;
            }

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO certificate " +
                    "(citizen_username, certificate_type, application_date, " +
                    "full_name, father_name, date_of_birth, date_of_death, status, remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, "Death Certificate");
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setString(4, fullNameField.getText());
            ps.setString(5, fatherNameField.getText());
            ps.setDate(6, Date.valueOf(dob));
            ps.setDate(7, Date.valueOf(dod));
            ps.setString(8, "Pending");
            ps.setString(9, "Submitted");

            ps.executeUpdate();

            con.close();

            JOptionPane.showMessageDialog(this,
                    "Application Submitted Successfully!\nStatus: Pending");

            dispose();
            new CitizenDashboard(username);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
