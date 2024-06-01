package ut.twente.notebridge;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.dao.UserDao;

import java.io.IOException;

@WebListener
public class InitializationListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Initializing Notebridge...");
		try {
			PostDao.INSTANCE.load();
			UserDao.INSTANCE.load();
		} catch (IOException e) {
			System.err.println("Error while loading data.");
			e.printStackTrace();
		}
		System.out.println("Notebridge initialized.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Shutting down Notebridge...");
		try {
			PostDao.INSTANCE.save();
			UserDao.INSTANCE.save();
		} catch (IOException e) {
			System.err.println("Error while saving data.");
			e.printStackTrace();
		}
		System.out.println("Notebridge shutdown.");
	}
}
