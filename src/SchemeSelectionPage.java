import javax.swing.*;
import java.awt.*;

public class SchemeSelectionPage extends JFrame {

    public SchemeSelectionPage(String username) {

        setTitle("Select Scheme");
        setSize(500,400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4,1,20,20));

        JButton eduBtn = new JButton("Education Scholarship");
        JButton widowBtn = new JButton("Widow Pension");
        JButton agriBtn = new JButton("Agriculture Subsidy");
        JButton backBtn = new JButton("Back");

        add(eduBtn);
        add(widowBtn);
        add(agriBtn);
        add(backBtn);

        eduBtn.addActionListener(e -> {
            dispose();
            new SchemeApplicationForm(username,"Education Scholarship");
        });

        widowBtn.addActionListener(e -> {
            dispose();
            new SchemeApplicationForm(username,"Widow Pension");
        });

        agriBtn.addActionListener(e -> {
            dispose();
            new SchemeApplicationForm(username,"Agriculture Subsidy");
        });

        backBtn.addActionListener(e -> {
            dispose();
            new CitizenDashboard(username);
        });

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
