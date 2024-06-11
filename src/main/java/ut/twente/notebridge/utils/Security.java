package ut.twente.notebridge.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

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

	public static boolean verifyHashedPassword(String password, String hash) {
		Argon2 argon2 = Argon2Factory.create();

		try {
			// Verify the password against the hash
			return argon2.verify(hash, password.toCharArray());
		} finally {
			argon2.wipeArray(password.toCharArray()); // Clear the password from memory
		}
	}

	public static Boolean checkCredentials(String email, String password) {
		String sql = """
						SELECT password FROM BaseUser
				  					WHERE email=?;
				""";

		try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				System.out.println("User with the email exists, checking the password...");
				String hashedPassword = rs.getString("password");
				if (verifyHashedPassword(password, hashedPassword)) {
					System.out.println("User credentials are valid");
					return true;
				}else{
					System.out.println("User with the email exists, but password is incorrect.");
				}
			}else{
				System.out.println("User with the email does not exist.");
			}
			return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
