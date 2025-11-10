import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DonorManagementApp extends Frame implements ActionListener {

    // Components
    Label l1, l2, l3, l4;
    TextField tName, tBlood, tContact;
    Button bAdd, bView, bClear, bExit;
    TextArea taOutput;

    // JDBC
    Connection con;
    PreparedStatement pstmt;
    Statement stmt;
    ResultSet rs;

    // Constructor
    DonorManagementApp() {
        setTitle("Donor Management System");
        setSize(500, 500);
        setLayout(null);

        l1 = new Label("Donor Management System");
        l1.setFont(new Font("Arial", Font.BOLD, 18));
        l1.setBounds(100, 50, 300, 30);
        add(l1);

        l2 = new Label("Name:");
        l2.setBounds(50, 100, 100, 30);
        add(l2);

        tName = new TextField();
        tName.setBounds(160, 100, 200, 30);
        add(tName);

        l3 = new Label("Blood Group:");
        l3.setBounds(50, 140, 100, 30);
        add(l3);

        tBlood = new TextField();
        tBlood.setBounds(160, 140, 200, 30);
        add(tBlood);

        l4 = new Label("Contact:");
        l4.setBounds(50, 180, 100, 30);
        add(l4);

        tContact = new TextField();
        tContact.setBounds(160, 180, 200, 30);
        add(tContact);

        bAdd = new Button("Add Donor");
        bAdd.setBounds(50, 230, 100, 30);
        bAdd.addActionListener(this);
        add(bAdd);

        bView = new Button("View Donors");
        bView.setBounds(170, 230, 100, 30);
        bView.addActionListener(this);
        add(bView);

        bClear = new Button("Clear");
        bClear.setBounds(290, 230, 80, 30);
        bClear.addActionListener(this);
        add(bClear);

        bExit = new Button("Exit");
        bExit.setBounds(380, 230, 60, 30);
        bExit.addActionListener(this);
        add(bExit);

        taOutput = new TextArea();
        taOutput.setBounds(50, 280, 380, 150);
        add(taOutput);

        // Connect to DB
        connectDB();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                closeDB();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // Connect to MySQL
    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/donor_db", 
            "root", "vasu089");
            stmt = con.createStatement();
        } catch (Exception e) {
            taOutput.setText("DB Connection Failed: " + e);
        }
    }

    // Close DB
    void closeDB() {
        try {
            if (con != null) con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Event Handling
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();

        if (cmd.equals("Add Donor")) {
            addDonor();
        } else if (cmd.equals("View Donors")) {
            viewDonors();
        } else if (cmd.equals("Clear")) {
            clearFields();
        } else if (cmd.equals("Exit")) {
            closeDB();
            System.exit(0);
        }
    }

    void addDonor() {
        try {
            String name = tName.getText();
            String blood = tBlood.getText();
            String contact = tContact.getText();

            if (name.isEmpty() || blood.isEmpty() || contact.isEmpty()) {
                taOutput.setText("All fields are required!");
                return;
            }

            pstmt = con.prepareStatement("INSERT INTO donors(name, blood_group, contact) VALUES (?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, blood);
            pstmt.setString(3, contact);

            int rows = pstmt.executeUpdate();
            if (rows > 0)
                taOutput.setText("Donor added successfully!");

            clearFields();
        } catch (Exception e) {
            taOutput.setText("Error: " + e.getMessage());
        }
    }

    void viewDonors() {
        try {
            rs = stmt.executeQuery("SELECT * FROM donors");
            StringBuilder sb = new StringBuilder();
            sb.append("ID\tName\tBlood Group\tContact\n");
            sb.append("-----------------------------------------\n");
            while (rs.next()) {
                sb.append(rs.getInt("id")).append("\t")
                  .append(rs.getString("name")).append("\t")
                  .append(rs.getString("blood_group")).append("\t")
                  .append(rs.getString("contact")).append("\n");
            }
            taOutput.setText(sb.toString());
        } catch (Exception e) {
            taOutput.setText("Error: " + e.getMessage());
        }
    }

    void clearFields() {
        tName.setText("");
        tBlood.setText("");
        tContact.setText("");
    }

    public static void main(String[] args) {
        new DonorManagementApp();
    }
}
