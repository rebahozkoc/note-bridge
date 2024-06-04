package cards;

import java.sql.*;

public class cards {

    private static Connection connection;

    public static void main(String[] args) {
        loadDriver();
        String username = "dab_di23242b_105";
        String password = "Eglj/88XRQFG7jZO";
        String url = "jdbc:postgresql://bronto.ewi.utwente.nl/"+username+"?currentSchema=notebridge";
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Exception " + e);
        }

    }

    public void createCard(int id, String title, String desc){
        try {

            String query = "insert into notebridge.cards (card_id, title, event_type) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            statement.setString(2, title);
            statement.setString(3, desc);

            statement.executeQuery();

            statement.close();
        } catch (SQLException e) {
            System.err.println("Error " + e);
        }
    }

    public void findCardByID(int id){
        try{
            String query = "SELECT * FROM notebridge.cards WHERE card_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                System.out.println(rs.getString(1));
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println("Error " + e);
        }
    }

    public void findCardByType(String type){
        try{
            String query = "SELECT * FROM notebridge.cards WHERE event_type = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, type);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                System.out.println(rs.getString(1));
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println("Error " + e);
        }
    }

    public void updateCard(int id, String title, String desc){
        try {
            String query = "UPDATE notebridge.cards SET title = ?, event_type = ? WHERE card_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, title);
            statement.setString(2, desc);
            statement.setInt(3, id);

            statement.executeQuery();

            statement.close();

        } catch (SQLException e) {
            System.err.println("Error " + e);
        }

    }


    public void deleteCard(int id){
        try {
            String query = "DELETE FROM notebridge.cards WHERE card_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);

            statement.executeQuery();

            statement.close();

        } catch (SQLException e) {
            System.err.println("Error " + e);
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
