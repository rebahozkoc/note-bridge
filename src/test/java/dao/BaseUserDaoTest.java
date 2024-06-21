package dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class BaseUserDaoTest {

	private static BaseUser baseUser;

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

		// Clean the database
		BaseUserDao.INSTANCE.deleteAll();
	}

	@AfterAll
	public static void tearDownAll() {

		BaseUserDao.INSTANCE.deleteAll();

		try {
			DatabaseConnection.INSTANCE.getConnection().close();
		} catch (Exception e) {
			System.err.println("Error while closing the database connection.");
			e.printStackTrace();
		}
		System.out.println("Notebridge TEST shutdown.");
	}

	@BeforeEach
	public void setUp() {
		this.baseUser = new BaseUser();

		this.baseUser.setUsername("testusername");
		this.baseUser.setEmail("testemail@gmail.com");
		this.baseUser.setPassword("Password.123");
	}

	@Test
	@Order(1)
	public void stage1_testCreateBaseUser() {
		BaseUser baseUser = this.baseUser;

		BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(baseUser);
		assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
		assertEquals(createdBaseUser, baseUser, "Base user is created");
		this.baseUser.setId(createdBaseUser.getId());
	}

	@Test
	@Order(2)
	public void stage2_getUser() {
		BaseUser baseUser = BaseUserDao.INSTANCE.getUser(this.baseUser.getId());
		assertEquals(baseUser, this.baseUser, "Base user is retrieved");
	}

}
