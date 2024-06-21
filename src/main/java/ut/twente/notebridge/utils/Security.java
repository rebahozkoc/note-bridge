package ut.twente.notebridge.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Security {
	private static final int ITERATIONS = 3;
	private static final int MEMORY = 65536; // 64 MB
	private static final int PARALLELISM = 1;

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

	public static String hashPassword(String password) {
		Argon2 argon2 = Argon2Factory.create();

		try {
			// Hash the password with Argon2
			return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray());
		} finally {
			argon2.wipeArray(password.toCharArray()); // Clear the password from memory
		}
	}

	public static boolean verifyHashedPassword(char[] password, String hash) {
		Argon2 argon2 = Argon2Factory.create();

		try {
			// Verify the password against the hash
			return argon2.verify(hash, password);
		} finally {
			argon2.wipeArray(password); // Clear the password from memory
		}
	}

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
				}else{
					return false;
				}
			}else{
				    return false;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Server not available");
		}

	}


	public static String sanitizeInput(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		return Jsoup.clean(input, Safelist.basic());

	}
}
