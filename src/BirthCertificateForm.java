import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BirthCertificateForm extends JFrame {

    private JTextField applicantNameField, phoneField, emailField;
    private JTextArea applicantAddressArea;

    private JTextField childNameField, placeOfBirthField;
    private JTextField dobField;
    private JComboBox<String> genderBox;
    private JTextField fatherNameField, motherNameField;
    private JTextArea birthAddressArea;

    private String username;

    public BirthCertificateForm(String username) {

        this.username = username;

        setTitle("Birth Certificate Application");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30,200,30,200));
        mainPanel.setBackground(new Color(245,245,245));

        // ================= TITLE =================
        JLabel title = new JLabel("Create Birth Certificate Application");
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

        // ================= BIRTH DETAILS PANEL =================
        JPanel birthPanel = createSectionPanel("Birth Certificate Details");

        childNameField = new JTextField(15);
        placeOfBirthField = new JTextField(15);
        dobField = new JTextField(15);
        fatherNameField = new JTextField(15);
        motherNameField = new JTextField(15);
        birthAddressArea = new JTextArea(3,15);

        genderBox = new JComboBox<>(new String[]{"Select Gender","Male","Female","Other"});

        addField(birthPanel,"Child Name*", childNameField);
        addField(birthPanel,"Date of Birth (YYYY-MM-DD)*", dobField);
        addField(birthPanel,"Place of Birth*", placeOfBirthField);
        addField(birthPanel,"Gender*", genderBox);
        addField(birthPanel,"Father Name*", fatherNameField);
        addField(birthPanel,"Mother Name*", motherNameField);
        addTextArea(birthPanel,"Address*", birthAddressArea);

        mainPanel.add(birthPanel);
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

    // ================= ADD FIELD =================
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

    // ================= ADD TEXTAREA =================
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

    // ================= BUTTON STYLE =================
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150,40));
    }

    // ================= SUBMIT FORM =================
    private void submitForm() {

        try {

            if(applicantNameField.getText().isEmpty() ||
                    childNameField.getText().isEmpty() ||
                    dobField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(this,"Please fill required fields!");
                return;
            }

            LocalDate dob;
            try {
                dob = LocalDate.parse(dobField.getText());
            } catch(DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Date Format! Use YYYY-MM-DD");
                return;
            }

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO certificate " +
                    "(citizen_username, certificate_type, application_date, " +
                    "full_name, father_name, date_of_birth, status, remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, "Birth Certificate");
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setString(4, childNameField.getText());
            ps.setString(5, fatherNameField.getText());
            ps.setDate(6, Date.valueOf(dob));
            ps.setString(7, "Pending");
            ps.setString(8, "Submitted");

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
