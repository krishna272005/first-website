import javax.swing.*;
import java.awt.*;

public class PublicServicesPage extends JFrame {

    public PublicServicesPage() {

        setTitle("Public Services - Mahagaon Grampanchayat");
        setSize(900,600);
        setLocationRelativeTo(null);
        setLayout(null);

        Color mainColor = new Color(240,248,255);
        getContentPane().setBackground(mainColor);

        JLabel title = new JLabel("Public Services");
        title.setFont(new Font("Arial",Font.BOLD,26));
        title.setBounds(320,30,300,40);
        add(title);

        // Services Buttons
        JButton birthBtn = new JButton("Birth Certificate");
        birthBtn.setBounds(200,120,200,50);
        add(birthBtn);

        JButton deathBtn = new JButton("Death Certificate");
        deathBtn.setBounds(450,120,200,50);
        add(deathBtn);

        JButton incomeBtn = new JButton("Income Certificate");
        incomeBtn.setBounds(200,220,200,50);
        add(incomeBtn);

        JButton residenceBtn = new JButton("Residence Certificate");
        residenceBtn.setBounds(450,220,200,50);
        add(residenceBtn);

        JButton complaintBtn = new JButton("Register Complaint");
        complaintBtn.setBounds(325,320,250,50);
        add(complaintBtn);

        JButton backBtn = new JButton("Back to Home");
        backBtn.setBounds(350,420,200,40);
        add(backBtn);

        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        setVisible(true);
    }
}
