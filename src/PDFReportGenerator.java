import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFReportGenerator {

    // Define a professional color palette
    private static final BaseColor HEADER_BG = new BaseColor(44, 62, 80);   // Dark Navy
    private static final BaseColor ROW_ALT_BG = new BaseColor(245, 245, 245); // Light Grey
    private static final BaseColor ACCENT_COLOR = new BaseColor(41, 128, 185); // Blue Accent

    public static void generateReport(String titleText, String query, String fileName) {
        // Use A4 Landscape for more breathing room
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 50, 50);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // ====== FONTS ======
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, HEADER_BG);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, ACCENT_COLOR);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 9);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

            // ====== HEADER SECTION ======
            Paragraph title = new Paragraph("Gram Panchayat Management System", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subTitle = new Paragraph(titleText, subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            subTitle.setSpacingAfter(15);
            document.add(subTitle);

            // Horizontal Line
            Paragraph line = new Paragraph();
            line.add(new Chunk(new com.itextpdf.text.pdf.draw.LineSeparator(1f, 100, ACCENT_COLOR, Element.ALIGN_CENTER, -2)));
            document.add(line);

            String dateStr = new SimpleDateFormat("MMM dd, yyyy - HH:mm").format(new Date());
            Paragraph datePara = new Paragraph("Report Generated: " + dateStr, footerFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            datePara.setSpacingAfter(20);
            document.add(datePara);

            // ====== DATABASE CONNECTION ======
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // ====== TABLE STYLING ======
            PdfPTable table = new PdfPTable(columnCount);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // ====== RENDER TABLE HEADERS ======
            for (int i = 1; i <= columnCount; i++) {
                PdfPCell headerCell = new PdfPCell(new Phrase(metaData.getColumnName(i).toUpperCase(), headerFont));
                headerCell.setBackgroundColor(HEADER_BG);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPadding(8);
                headerCell.setBorderColor(BaseColor.WHITE); // White border for a modern look
                table.addCell(headerCell);
            }

            // ====== RENDER TABLE DATA ======
            int rowCount = 0;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    PdfPCell cell = new PdfPCell(new Phrase(value == null ? "-" : value, dataFont));

                    // Alternating Row Colors
                    if (rowCount % 2 != 0) {
                        cell.setBackgroundColor(ROW_ALT_BG);
                    }

                    cell.setPadding(6);
                    cell.setBorderColor(new BaseColor(220, 220, 220)); // Soft grey borders
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                rowCount++;
            }

            document.add(table);

            // ====== FOOTER ======
            Paragraph summary = new Paragraph("Total Records Found: " + rowCount,
                    new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
            summary.setAlignment(Element.ALIGN_LEFT);
            document.add(summary);

            document.close();
            con.close();

            // Final notification
            javax.swing.JOptionPane.showMessageDialog(null, "Professional Report Generated!");
            java.awt.Desktop.getDesktop().open(new java.io.File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}