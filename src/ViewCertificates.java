import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ViewCertificates extends JFrame {

    JTable table;
    DefaultTableModel model;
    String citizenUsername;

    public ViewCertificates(String citizenUsername) {

        if (!SessionManager.isLoggedIn() ||
                !SessionManager.getRole().equals("Citizen")) {

            JOptionPane.showMessageDialog(this, "Unauthorized Access!");
            dispose();
            new LoginFrame();
            return;
        }

        this.citizenUsername = citizenUsername;

        setTitle("My Certificate Applications");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel("My Certificate Applications");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(300, 20, 400, 30);
        add(title);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Type");
        model.addColumn("Application Date");
        model.addColumn("Status");
        model.addColumn("Remarks");

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 80, 850, 300);
        add(scroll);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(250, 400, 120, 35);
        add(refreshBtn);

        JButton downloadBtn = new JButton("Download Certificate (PDF)");
        downloadBtn.setBounds(400, 400, 220, 35);
        add(downloadBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(650, 400, 120, 35);
        add(backBtn);

        refreshBtn.addActionListener(e -> loadData());
        downloadBtn.addActionListener(e -> downloadCertificate());
        backBtn.addActionListener(e -> {
            dispose();
            new CitizenDashboard(citizenUsername);
        });

        loadData();
        setVisible(true);
    }

    private void loadData() {

        try {
            model.setRowCount(0);

            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM certificate WHERE citizen_username=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, citizenUsername);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("certificate_type"),
                        rs.getDate("application_date"),
                        rs.getString("status"),
                        rs.getString("remarks")
                });
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadCertificate() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select certificate first!");
            return;
        }

        // STATUS COLUMN IS INDEX 3
        String status = model.getValueAt(row, 3).toString();

        if (!status.equals("Approved")) {
            JOptionPane.showMessageDialog(this, "Certificate not approved yet!");
            return;
        }

        try {

            int id = (int) model.getValueAt(row, 0);

            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM certificate WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Certificate as PDF");

                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    File fileToSave = new File(fileChooser.getSelectedFile() + ".pdf");

                    PDDocument document = new PDDocument();
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    PDPageContentStream contentStream =
                            new PDPageContentStream(document, page);

                    // ===== TITLE =====
                    contentStream.beginText();
                    contentStream.setFont(
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                            18
                    );
                    contentStream.newLineAtOffset(170, 750);
                    contentStream.showText("GRAM PANCHAYAT CERTIFICATE");
                    contentStream.endText();

                    // ===== BODY =====
                    contentStream.beginText();
                    contentStream.setFont(
                            new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                            12
                    );
                    contentStream.newLineAtOffset(100, 700);

                    contentStream.showText("Certificate ID: " + id);
                    contentStream.newLineAtOffset(0, -20);

                    contentStream.showText("Name: " +
                            safe(rs.getString("full_name")));
                    contentStream.newLineAtOffset(0, -20);

                    contentStream.showText("Father Name: " +
                            safe(rs.getString("father_name")));
                    contentStream.newLineAtOffset(0, -20);

                    contentStream.showText("Certificate Type: " +
                            rs.getString("certificate_type"));
                    contentStream.newLineAtOffset(0, -20);

                    contentStream.showText("Address: " +
                            safe(rs.getString("address")));
                    contentStream.newLineAtOffset(0, -20);

                    contentStream.showText("Status: APPROVED");
                    contentStream.newLineAtOffset(0, -40);

                    contentStream.showText("Issued By: Gram Panchayat Office");
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Authorized Signature");

                    contentStream.endText();
                    contentStream.close();

                    document.save(fileToSave);
                    document.close();

                    JOptionPane.showMessageDialog(this,
                            "PDF Certificate Generated Successfully!");
                }
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}