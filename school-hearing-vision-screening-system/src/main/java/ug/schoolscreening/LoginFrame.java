package ug.schoolscreening;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private final JTextField username = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);

    public LoginFrame() {
        setTitle("School Hearing and Vision Screening Assistant - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(470, 330);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(16, 38, 64));
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel title = new JLabel("School Screening Assistant", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(16, 65, 91));
        card.add(title, g);
        g.gridwidth = 1; g.gridy++;
        card.add(new JLabel("Username"), g);
        g.gridx = 1; card.add(username, g);
        g.gridx = 0; g.gridy++;
        card.add(new JLabel("Password"), g);
        g.gridx = 1; card.add(password, g);
        g.gridx = 0; g.gridy++; g.gridwidth = 2;
        JButton login = new JButton("Sign in");
        login.setBackground(new Color(26, 153, 145));
        login.setForeground(Color.WHITE);
        login.addActionListener(e -> authenticate());
        card.add(login, g);
        g.gridy++;
        JLabel note = new JLabel("Authorized school users only", SwingConstants.CENTER);
        note.setForeground(Color.GRAY);
        card.add(note, g);
        root.add(card);
        setContentPane(root);
        getRootPane().setDefaultButton(login);
    }

    private void authenticate() {
        String sql = "SELECT user_id, username, full_name, role, password_hash FROM app_users WHERE username=? AND active=TRUE";
        char[] entered = password.getPassword();
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.getText().trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(entered, rs.getString("password_hash"))) {
                    UserSession session = new UserSession(
                            rs.getLong("user_id"), rs.getString("username"),
                            rs.getString("full_name"), rs.getString("role"));
                    dispose();
                    new MainFrame(session).setVisible(true);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Sign in failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not connect to MySQL. Check database setup and connection settings.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(entered, '\0');
            password.setText("");
        }
    }
}
