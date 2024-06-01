import java.sql.*;

public class createCard {

    private static Connection connection;

    public static void main(String[] args) {
        loadDriver();
        String username = "dab_di23242b_105";
        String password = "Eglj/88XRQFG7jZO";
        String url = "jdbc:postgresql://bronto.ewi.utwente.nl/"+username;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Exception " + e);
        }
    }


    public static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error " + e);
        }
    }
}
