package ut.twente.notebridge;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Utils;

import java.sql.SQLException;

/**
 * This class is used to initialize the Notebridge application.
 */
@WebListener
public class InitializationListener implements ServletContextListener {
    /**
     * This method is used to initialize the Notebridge application.
     *
     * @param sce The ServletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing Notebridge...");
        try {
            DatabaseConnection.INSTANCE.load(true);

            System.out.println("Working Directory = " + System.getProperty("user.dir"));
        } catch (Exception e) {
            System.err.println("Error while loading data.");
            e.printStackTrace();
        }
        System.out.println("Notebridge initialized.");
    }

    /**
     * This method is used to destroy the Notebridge application.
     *
     * @param sce The ServletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Shutting down Notebridge...");
        try {
            DatabaseConnection.INSTANCE.getConnection().close();
        } catch (SQLException e) {
            System.err.println("Error while closing the database connection.");
            e.printStackTrace();
        }
        System.out.println("Notebridge shutdown.");
    }
}
