package dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostDaoTest {

	@BeforeAll
	public static void setUpAll() {
		try {
			DatabaseConnection.INSTANCE.load(false);

			System.out.println("Working Directory = " + System.getProperty("user.dir"));
		} catch (Exception e) {
			System.err.println("Error while loading data.");
			e.printStackTrace();
		}
		System.out.println("Notebridge TEST initialized.");
	}

	@AfterAll
	public static void tearDownAll() {
		try {
			DatabaseConnection.INSTANCE.getConnection().close();
		} catch (Exception e) {
			System.err.println("Error while closing the database connection.");
			e.printStackTrace();
		}
		System.out.println("Notebridge TEST shutdown.");
	}

	@Test
	public void testCreatePost () {
		Post post = new Post();

		PostDao.INSTANCE.create(post);
	}
}