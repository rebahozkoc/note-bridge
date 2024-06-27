package ut.twente.notebridge.utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * This class is used to handle the database connection.
 */
public enum DatabaseConnection {

    /**
     * The instance of the database connection to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * The connection to the database.
     */
    private Connection connection;

    /**
     * This method is used to create a connection to the database when initializing the app.
     * Depending on the deployment mode, it will load the properties file and create a connection to the database.
     */
    public void load(boolean deploymentMode) {

        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        System.out.println(rootPath);
        String appConfigPath = rootPath + "app.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String URL;
        if (deploymentMode) {
            URL = appProps.getProperty("PROD_URL");
        } else {
            URL = appProps.getProperty("TEST_URL");
        }
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

    /**
     * This method is used to get the connection to the database.
     *
     * @return The connection to the database
     */
    public Connection getConnection() {
        return connection;
    }
}
