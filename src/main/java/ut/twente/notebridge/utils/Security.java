package ut.twente.notebridge.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is used to handle the security.
 */
public class Security {
    /**
     * The number of iterations for the Argon2 hashing algorithm.
     */
    private static final int ITERATIONS = 3;
    /**
     * The memory size for the Argon2 hashing algorithm.
     */
    private static final int MEMORY = 65536;
    /**
     * The parallelism for the Argon2 hashing algorithm.
     */
    private static final int PARALLELISM = 1;

    /**
     * This method is used to check the validity of a password.
     * By validity, it means that the password should be at least 8 characters long, at most 64 characters long,
     * And at the same time should contain at least one uppercase letter, two digits, and one special character.
     *
     * @param password The password to be checked
     * @return The boolean value indicating the validity of the password
     */
    public static Boolean checkPasswordValidity(String password) {
        if (password.length() < 8 || password.length() > 64) {
            return false;
        }

        boolean hasUppercase = false;
        int digitCount = 0;
        boolean hasSpecialChar = false;
        String specialChars = "!@#$%^&*()-_+=.";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                digitCount++;
            } else if (specialChars.indexOf(c) >= 0) {
                hasSpecialChar = true;
            }
        }

        return hasUppercase && digitCount >= 2 && hasSpecialChar;
    }

    /**
     * This method is used to hash a password.
     *
     * @param password The password to be hashed
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create();

        try {
            // Hash the password with Argon2
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray()); // Clear the password from memory
        }
    }

    /**
     * This method is used to verify a hashed password.
     *
     * @param password The password to be verified
     * @param hash     The hash of the password
     * @return The boolean value indicating the verification of the password
     */
    public static boolean verifyHashedPassword(char[] password, String hash) {
        Argon2 argon2 = Argon2Factory.create();

        try {
            // Verify the password against the hash
            return argon2.verify(hash, password);
        } finally {
            argon2.wipeArray(password); // Clear the password from memory
        }
    }

    /**
     * This method is used to check the credentials of a user.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return The boolean value indicating the validity of the credentials
     */
    public static Boolean checkCredentials(String email, char[] password) {
        String sql = """
                		SELECT password FROM BaseUser
                  					WHERE email=?;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (verifyHashedPassword(password, hashedPassword)) {

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Server not available");
        }

    }


    /**
     * This method is used to sanitize the input.
     *
     * @param input The input to be sanitized
     * @return The sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Jsoup.clean(input, Safelist.basic());

    }

    /**
     * This method is used to check if a user is authorized.
     *
     * @param session The session of the user
     * @param role    The role of the user
     * @return The boolean value indicating the authorization of the user
     */
    public static boolean isAuthorized(HttpSession session, String role) {
        if (session == null || session.getAttribute("role") == null) {
            return false;
        }
        return session.getAttribute("role").equals(role);
    }
}
