package ug.schoolscreening;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

/** Run once to create the first administrator account. */
public class SetupAdmin {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Administrator username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Administrator full name: ");
        String fullName = scanner.nextLine().trim();

        Console console = System.console();
        char[] password;
        if (console != null) {
            password = console.readPassword("Choose password (minimum 10 characters): ");
        } else {
            System.out.print("Choose password (minimum 10 characters): ");
            password = scanner.nextLine().toCharArray();
        }
        if (username.isBlank() || fullName.isBlank() || password.length < 10) {
            System.err.println("Username/name are required and password must be at least 10 characters.");
            System.exit(1);
        }

        String sql = "INSERT INTO app_users(username,password_hash,full_name,role) VALUES(?,?,?,'ADMIN')";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            ps.setString(3, fullName);
            ps.executeUpdate();
            System.out.println("Administrator account created. Keep the credentials private.");
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
