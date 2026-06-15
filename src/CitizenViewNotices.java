import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CitizenViewNotices extends JFrame {

    JTable table;
    DefaultTableModel model;

    public CitizenViewNotices() {

        setTitle("Grampanchayat Notices");
        setSize(600,400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel();
        model.addColumn("Title");
        model.addColumn("Message");
        model.addColumn("Posted On");
        model.addColumn("Expiry");

        table = new JTable(model);

        add(new JScrollPane(table));

        loadNotices();

        setVisible(true);
    }

    void loadNotices() {

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM notices WHERE expiry_date >= CURDATE()";

            PreparedStatement pst = con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while(rs.next()) {

                model.addRow(new Object[]{
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getDate("post_date"),
                        rs.getDate("expiry_date")
                });
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}