import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class AdminReports extends JFrame {

    public AdminReports() {

        setTitle("Reports Panel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color bg = new Color(236,242,248);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);
        gbc.gridx = 0;

        JButton citizenBtn = createButton("Download Citizens Report");
        JButton certificateBtn = createButton("Download Certificates Report");
        JButton complaintBtn = createButton("Download Complaints Report");
        JButton backBtn = createButton("Back to Dashboard");

        gbc.gridy=0; panel.add(citizenBtn,gbc);
        gbc.gridy++; panel.add(certificateBtn,gbc);
        gbc.gridy++; panel.add(complaintBtn,gbc);
        gbc.gridy++; panel.add(backBtn,gbc);

        add(panel, BorderLayout.CENTER);

        citizenBtn.addActionListener(e ->
                generateReport("SELECT * FROM citizen",
                        "Citizens_Report.pdf",
                        "Citizens Report"));

        certificateBtn.addActionListener(e ->
                generateReport("SELECT * FROM certificate",
                        "Certificates_Report.pdf",
                        "Certificates Report"));

        complaintBtn.addActionListener(e ->
                generateReport("SELECT * FROM complaint",
                        "Complaints_Report.pdf",
                        "Complaints Report"));

        backBtn.addActionListener(e -> {
            dispose();
            new Dashboard(SessionManager.getUsername());
        });

        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(350,50));
        btn.setBackground(new Color(0,51,102));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial",Font.BOLD,14));
        return btn;
    }

    private void generateReport(String query, String fileName, String titleText) {

        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));

            if(chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            File file = chooser.getSelectedFile();

            // 🔥 Close PDF if already open manually before saving

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content =
                    new PDPageContentStream(document,page);

            float y = 800;

            // ===== HEADER =====
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),18);
            content.newLineAtOffset(150,y);
            content.showText("Gram Panchayat Management System");
            content.endText();

            y-=30;

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),14);
            content.newLineAtOffset(200,y);
            content.showText(titleText);
            content.endText();

            y-=25;

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),10);
            content.newLineAtOffset(40,y);
            content.showText("Generated on: "+
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            content.endText();

            y-=30;

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),9);

            while(rs.next()) {

                content.beginText();
                content.newLineAtOffset(40,y);

                for(int i=1;i<=rs.getMetaData().getColumnCount();i++) {

                    String value = rs.getString(i);
                    if(value == null) value = " ";

                    content.showText(value + " | ");
                }

                content.endText();
                y-=15;

                if(y<60) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document,page);
                    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),9);
                    y=800;
                }
            }

            content.close();

            // ✅ FIX: Delete file if exists (prevents corruption warning)
            if (file.exists()) {
                file.delete();
            }

            document.save(file.getAbsolutePath());
            document.close();
            con.close();

            JOptionPane.showMessageDialog(this,
                    "Report Downloaded Successfully!");

        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error Generating Report!");
        }
    }
}
