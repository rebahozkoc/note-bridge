package ut.twente.notebridge.utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public enum DatabaseConnection {

	INSTANCE;

	private Connection connection;

	public void load(){

		String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
		System.out.println(rootPath);
		String appConfigPath = rootPath + "app.properties";

		Properties appProps = new Properties();
		try {
			appProps.load(new FileInputStream(appConfigPath));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String URL = appProps.getProperty("URL");
		String USER = appProps.getProperty("USER");
		String PASSWORD = appProps.getProperty("PASSWORD");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(true);
			System.out.println("Database connection established");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}
}
