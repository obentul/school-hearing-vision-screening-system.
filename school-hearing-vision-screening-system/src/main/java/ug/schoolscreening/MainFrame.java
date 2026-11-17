package ug.schoolscreening;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;

public class MainFrame extends JFrame {
    private final UserSession user;
    private final JLabel summary = new JLabel("Loading summary...");
    private final DefaultTableModel pupilModel = model("ID", "Admission No.", "First name", "Last name", "Class", "Sex");
    private final DefaultTableModel screeningModel = model("ID", "Pupil", "Date", "Type", "Outcome", "Recorded by");
    private final DefaultTableModel referralModel = model("ID", "Pupil", "Date", "Reason", "Destination", "Status");

    public MainFrame(UserSession user) {
        this.user = user;
        setTitle("School Hearing and Vision Screening Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(16, 38, 64));
        top.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel brand = new JLabel("SCHOOL HEARING & VISION SCREENING ASSISTANT");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel identity = new JLabel(user.fullName() + "  |  " + user.role() + "  |  Sign out ");
        identity.setForeground(Color.WHITE);
        identity.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        identity.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { dispose(); new LoginFrame().setVisible(true); }
        });
        top.add(brand, BorderLayout.WEST);
        top.add(identity, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", dashboardPanel());
        tabs.addTab("Pupils", pupilsPanel());
        tabs.addTab("Screenings", screeningsPanel());
        tabs.addTab("Referrals", referralsPanel());
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(28,28,28,28));
        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        summary.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JTextArea note = new JTextArea("Use the tabs to register pupils, record hearing or vision screening, and track referrals.\\n\\n"
                + "Important: This application stores screening information only. It does not diagnose medical conditions. "
                + "Refer concerns to qualified health professionals using the school's approved procedure.");
        note.setLineWrap(true); note.setWrapStyleWord(true); note.setEditable(false);
        note.setBackground(new Color(239,246,250));
        note.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        p.add(heading, BorderLayout.NORTH);
        p.add(summary, BorderLayout.CENTER);
        p.add(note, BorderLayout.SOUTH);
        return p;
    }

    private JPanel pupilsPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JTable table = new JTable(pupilModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Register pupil");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");
        add.addActionListener(e -> addPupil());
        refresh.addActionListener(e -> loadPupils());
        export.addActionListener(e -> exportTable(pupilModel, "pupils.csv"));
        buttons.add(add); buttons.add(refresh); buttons.add(export);
        p.add(buttons, BorderLayout.NORTH);
        return p;
    }

    private JPanel screeningsPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JScrollPane(new JTable(screeningModel)), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Record screening");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");
        add.addActionListener(e -> addScreening());
        refresh.addActionListener(e -> loadScreenings());
        export.addActionListener(e -> exportTable(screeningModel, "screenings.csv"));
        buttons.add(add); buttons.add(refresh); buttons.add(export);
        p.add(buttons, BorderLayout.NORTH);
        return p;
    }

    private JPanel referralsPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JScrollPane(new JTable(referralModel)), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add referral");
        JButton update = new JButton("Update selected status");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");
        add.addActionListener(e -> addReferral());
        update.addActionListener(e -> updateReferral());
        refresh.addActionListener(e -> loadReferrals());
        export.addActionListener(e -> exportTable(referralModel, "referrals.csv"));
        buttons.add(add); buttons.add(update); buttons.add(refresh); buttons.add(export);
        p.add(buttons, BorderLayout.NORTH);
        return p;
    }

    private void addPupil() {
        JTextField admission = new JTextField();
        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField clazz = new JTextField();
        JComboBox<String> sex = new JComboBox<>(new String[]{"NOT_RECORDED","F","M","OTHER"});
        JTextField dob = new JTextField();
        JTextField guardian = new JTextField();
        Object[] fields = {"Admission number*", admission, "First name*", first, "Last name*", last,
                "Class*", clazz, "Sex", sex, "Date of birth (YYYY-MM-DD, optional)", dob,
                "Guardian contact (optional)", guardian};
        if (JOptionPane.showConfirmDialog(this, fields, "Register pupil", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (admission.getText().isBlank() || first.getText().isBlank() || last.getText().isBlank() || clazz.getText().isBlank()) {
            message("Admission number, first name, last name and class are required."); return;
        }
        String sql = "INSERT INTO pupils(admission_no,first_name,last_name,class_name,sex,date_of_birth,guardian_contact) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, admission.getText().trim()); ps.setString(2, first.getText().trim());
            ps.setString(3, last.getText().trim()); ps.setString(4, clazz.getText().trim());
            ps.setString(5, (String) sex.getSelectedItem());
            if (dob.getText().isBlank()) ps.setNull(6, Types.DATE); else ps.setDate(6, Date.valueOf(dob.getText().trim()));
            ps.setString(7, guardian.getText().trim().isBlank() ? null : guardian.getText().trim());
            ps.executeUpdate(); loadPupils(); message("Pupil registered.");
        } catch (Exception ex) { error(ex); }
    }

    private void addScreening() {
        JComboBox<PupilChoice> pupil = pupilChoices();
        if (pupil.getItemCount() == 0) { message("Register a pupil before recording a screening."); return; }
        JTextField date = new JTextField(LocalDate.now().toString());
        JComboBox<String> type = new JComboBox<>(new String[]{"HEARING","VISION"});
        JComboBox<String> outcome = new JComboBox<>(new String[]{"PASS","REFER","INCONCLUSIVE"});
        JTextField notes = new JTextField();
        Object[] fields = {"Pupil", pupil, "Screening date (YYYY-MM-DD)", date, "Screening type", type,
                "Outcome", outcome, "Method / notes (optional)", notes};
        if (JOptionPane.showConfirmDialog(this, fields, "Record screening", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        String sql = "INSERT INTO screenings(pupil_id,screening_date,screening_type,outcome,method_notes,recorded_by) VALUES(?,?,?,?,?,?)";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ((PupilChoice)pupil.getSelectedItem()).id);
            ps.setDate(2, Date.valueOf(date.getText().trim())); ps.setString(3, (String)type.getSelectedItem());
            ps.setString(4, (String)outcome.getSelectedItem()); ps.setString(5, notes.getText().trim());
            ps.setLong(6, user.userId()); ps.executeUpdate(); loadScreenings(); message("Screening saved.");
        } catch (Exception ex) { error(ex); }
    }

    private void addReferral() {
        JComboBox<PupilChoice> pupil = pupilChoices();
        if (pupil.getItemCount() == 0) { message("Register a pupil before adding a referral."); return; }
        JTextField date = new JTextField(LocalDate.now().toString());
        JTextField reason = new JTextField();
        JTextField destination = new JTextField();
        JComboBox<String> status = new JComboBox<>(new String[]{"PENDING","REFERRED","FOLLOW_UP_DONE","CLOSED"});
        Object[] fields = {"Pupil", pupil, "Referral date (YYYY-MM-DD)", date, "Reason*", reason,
                "Destination (optional)", destination, "Status", status};
        if (JOptionPane.showConfirmDialog(this, fields, "Add referral", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (reason.getText().isBlank()) { message("A referral reason is required."); return; }
        String sql = "INSERT INTO referrals(pupil_id,referral_date,reason,destination,status,recorded_by) VALUES(?,?,?,?,?,?)";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ((PupilChoice)pupil.getSelectedItem()).id);
            ps.setDate(2, Date.valueOf(date.getText().trim())); ps.setString(3, reason.getText().trim());
            ps.setString(4, destination.getText().trim().isBlank() ? null : destination.getText().trim());
            ps.setString(5, (String)status.getSelectedItem()); ps.setLong(6, user.userId());
            ps.executeUpdate(); loadReferrals(); message("Referral saved.");
        } catch (Exception ex) { error(ex); }
    }

    private void updateReferral() {
        JTable table = findTable(referralModel);
        if (table == null || table.getSelectedRow() < 0) { message("Select a referral row first."); return; }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        long id = ((Number)referralModel.getValueAt(row,0)).longValue();
        String[] options = {"PENDING","REFERRED","FOLLOW_UP_DONE","CLOSED"};
        String status = (String)JOptionPane.showInputDialog(this, "Choose new status:", "Update referral",
                JOptionPane.PLAIN_MESSAGE, null, options, referralModel.getValueAt(row,5));
        if (status == null) return;
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE referrals SET status=? WHERE referral_id=?")) {
            ps.setString(1,status); ps.setLong(2,id); ps.executeUpdate(); loadReferrals(); message("Referral status updated.");
        } catch (Exception ex) { error(ex); }
    }

    private JTable findTable(DefaultTableModel model) {
        return findTableIn(this, model);
    }
    private JTable findTableIn(Container parent, DefaultTableModel model) {
        for (Component comp : parent.getComponents()) {
            if (comp instanceof JTable t && t.getModel() == model) return t;
            if (comp instanceof Container c) { JTable found = findTableIn(c, model); if (found != null) return found; }
        }
        return null;
    }

    private JComboBox<PupilChoice> pupilChoices() {
        JComboBox<PupilChoice> combo = new JComboBox<>();
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT pupil_id, admission_no, first_name, last_name FROM pupils ORDER BY first_name,last_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) combo.addItem(new PupilChoice(rs.getLong(1),
                    rs.getString(2) + " - " + rs.getString(3) + " " + rs.getString(4)));
        } catch (Exception ex) { error(ex); }
        return combo;
    }

    private void refreshAll() { loadPupils(); loadScreenings(); loadReferrals(); loadSummary(); }
    private void loadPupils() {
        pupilModel.setRowCount(0);
        String sql = "SELECT pupil_id,admission_no,first_name,last_name,class_name,sex FROM pupils ORDER BY pupil_id DESC";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) pupilModel.addRow(new Object[]{rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)});
        } catch (Exception ex) { error(ex); }
    }
    private void loadScreenings() {
        screeningModel.setRowCount(0);
        String sql = "SELECT s.screening_id, CONCAT(p.first_name,' ',p.last_name), s.screening_date,s.screening_type,s.outcome,u.username "
                + "FROM screenings s JOIN pupils p ON p.pupil_id=s.pupil_id JOIN app_users u ON u.user_id=s.recorded_by ORDER BY s.screening_date DESC";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) screeningModel.addRow(new Object[]{rs.getLong(1),rs.getString(2),rs.getDate(3),rs.getString(4),rs.getString(5),rs.getString(6)});
        } catch (Exception ex) { error(ex); }
    }
    private void loadReferrals() {
        referralModel.setRowCount(0);
        String sql = "SELECT r.referral_id,CONCAT(p.first_name,' ',p.last_name),r.referral_date,r.reason,r.destination,r.status "
                + "FROM referrals r JOIN pupils p ON p.pupil_id=r.pupil_id ORDER BY r.referral_date DESC";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) referralModel.addRow(new Object[]{rs.getLong(1),rs.getString(2),rs.getDate(3),rs.getString(4),rs.getString(5),rs.getString(6)});
        } catch (Exception ex) { error(ex); }
    }
    private void loadSummary() {
        try (Connection c = Db.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT (SELECT COUNT(*) FROM pupils),(SELECT COUNT(*) FROM screenings),(SELECT COUNT(*) FROM referrals WHERE status IN ('PENDING','REFERRED'))")) {
            if (rs.next()) summary.setText("<html><b>" + rs.getInt(1) + "</b> registered pupils&nbsp;&nbsp;&nbsp; <b>"
                    + rs.getInt(2) + "</b> screening records&nbsp;&nbsp;&nbsp; <b>" + rs.getInt(3) + "</b> open referrals</html>");
        } catch (Exception ex) { summary.setText("Database summary unavailable. Check MySQL connection."); }
    }

    private void exportTable(DefaultTableModel model, String filename) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(filename));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (FileWriter out = new FileWriter(chooser.getSelectedFile())) {
            for (int col=0; col<model.getColumnCount(); col++) {
                if (col>0) out.write(",");
                out.write(csv(model.getColumnName(col)));
            }
            out.write("\n");
            for (int row=0; row<model.getRowCount(); row++) {
                for (int col=0; col<model.getColumnCount(); col++) {
                    if (col>0) out.write(",");
                    out.write(csv(String.valueOf(model.getValueAt(row,col))));
                }
                out.write("\n");
            }
            message("CSV export completed.");
        } catch (Exception ex) { error(ex); }
    }
    private String csv(String s) { return "\"" + s.replace("\"", "\"\"") + "\""; }

    private static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns,0) { public boolean isCellEditable(int r,int c){ return false; } };
    }
    private void message(String text) { JOptionPane.showMessageDialog(this,text); }
    private void error(Exception ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Operation failed",JOptionPane.ERROR_MESSAGE); }

    private static class PupilChoice {
        final long id; final String label;
        PupilChoice(long id, String label) { this.id=id; this.label=label; }
        public String toString() { return label; }
    }
}
