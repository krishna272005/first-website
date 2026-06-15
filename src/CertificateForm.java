import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class CertificateForm extends JFrame {

    JComboBox<String> typeBox;
    JTextField nameField, fatherField, dobField, incomeField;
    JTextArea addressArea, purposeArea;

    String username;

    public CertificateForm(String username) {

        this.username = username;

        setTitle("Apply Certificate");
        setSize(700,600);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240,248,255));

        JLabel title = new JLabel("Certificate Application");
        title.setFont(new Font("Arial",Font.BOLD,22));
        title.setBounds(220,20,300,30);
        add(title);

        JLabel typeLabel = new JLabel("Certificate Type:");
        typeLabel.setBounds(100,80,150,30);
        add(typeLabel);

        typeBox = new JComboBox<>(new String[]{
                "Birth Certificate",
                "Income Certificate",
                "Residence Certificate"
        });
        typeBox.setBounds(250,80,200,30);
        add(typeBox);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(100,130,150,30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(250,130,200,30);
        add(nameField);

        JLabel fatherLabel = new JLabel("Father Name:");
        fatherLabel.setBounds(100,180,150,30);
        add(fatherLabel);

        fatherField = new JTextField();
        fatherField.setBounds(250,180,200,30);
        add(fatherField);

        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobLabel.setBounds(100,230,200,30);
        add(dobLabel);

        dobField = new JTextField();
        dobField.setBounds(300,230,150,30);
        add(dobField);

        JLabel incomeLabel = new JLabel("Annual Income:");
        incomeLabel.setBounds(100,280,150,30);
        add(incomeLabel);

        incomeField = new JTextField();
        incomeField.setBounds(250,280,200,30);
        add(incomeField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(100,330,150,30);
        add(addressLabel);

        addressArea = new JTextArea();
        addressArea.setBounds(250,330,300,60);
        add(addressArea);

        JLabel purposeLabel = new JLabel("Purpose:");
        purposeLabel.setBounds(100,410,150,30);
        add(purposeLabel);

        purposeArea = new JTextArea();
        purposeArea.setBounds(250,410,300,60);
        add(purposeArea);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setBounds(200,500,120,35);
        add(submitBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(350,500,120,35);
        add(backBtn);

        submitBtn.addActionListener(e -> submitApplication());

        backBtn.addActionListener(e -> {
            dispose();
            new CitizenDashboard(username);
        });

        setVisible(true);
    }

    private void submitApplication() {

        try {
            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO certificate (citizen_username, certificate_type, application_date, full_name, father_name, date_of_birth, address, income, purpose, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, typeBox.getSelectedItem().toString());
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setString(4, nameField.getText());
            ps.setString(5, fatherField.getText());
            ps.setDate(6, Date.valueOf(dobField.getText()));
            ps.setString(7, addressArea.getText());

            if(incomeField.getText().isEmpty())
                ps.setNull(8, Types.DOUBLE);
            else
                ps.setDouble(8, Double.parseDouble(incomeField.getText()));

            ps.setString(9, purposeArea.getText());
            ps.setString(10, "Pending");
            ps.setString(11, "Under Review");

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Application Submitted!");

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error in Submission!");
        }
    }
}
