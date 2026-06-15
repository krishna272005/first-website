import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class IncomeCertificateForm extends JFrame {

    private JTextField fullNameField, fatherNameField, incomeField;
    private String username;

    public IncomeCertificateForm(String username) {

        this.username = username;

        setTitle("Income Certificate Application");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40,250,40,250));
        mainPanel.setBackground(new Color(245,245,245));

        JLabel title = new JLabel("Create Income Certificate Application");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Income Details"));
        formPanel.setBackground(Color.WHITE);

        fullNameField = new JTextField(15);
        fatherNameField = new JTextField(15);
        incomeField = new JTextField(15);

        addField(formPanel,"Full Name*", fullNameField,0);
        addField(formPanel,"Father Name*", fatherNameField,1);
        addField(formPanel,"Annual Income*", incomeField,2);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(mainPanel.getBackground());

        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(submitBtn,new Color(0,123,255));
        styleButton(cancelBtn,new Color(108,117,125));

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel);

        add(new JScrollPane(mainPanel));

        submitBtn.addActionListener(e -> submitForm());
        cancelBtn.addActionListener(e -> {
            dispose();
            new CitizenDashboard(username);
        });

        setVisible(true);
    }

    private void addField(JPanel panel, String label, JTextField field, int y){

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleButton(JButton btn, Color color){
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));
        btn.setPreferredSize(new Dimension(150,40));
        btn.setFocusPainted(false);
    }

    private void submitForm(){

        try{

            if(fullNameField.getText().isEmpty() || incomeField.getText().isEmpty()){
                JOptionPane.showMessageDialog(this,"Please fill required fields!");
                return;
            }

            double income;

            try{
                income = Double.parseDouble(incomeField.getText());
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Income must be numeric!");
                return;
            }

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO certificate " +
                    "(citizen_username, certificate_type, application_date, " +
                    "full_name, father_name, income, status, remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, "Income Certificate");
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setString(4, fullNameField.getText());
            ps.setString(5, fatherNameField.getText());
            ps.setDouble(6, income);
            ps.setString(7, "Pending");
            ps.setString(8, "Submitted");

            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this,"Application Submitted!\nStatus: Pending");

            dispose();
            new CitizenDashboard(username);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
