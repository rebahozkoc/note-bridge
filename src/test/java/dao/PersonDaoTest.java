package dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonDaoTest {

	private static Person person;

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
		PersonDao.INSTANCE.deleteAll();
		BaseUserDao.INSTANCE.deleteAll();

	}

	@AfterAll
	public static void tearDownAll() {

		PersonDao.INSTANCE.deleteAll();
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
		if (person == null) {
			person = new Person();
			person.setUsername("testusername");
			person.setEmail("testemail@gmail.com");
			person.setPassword("Password.123");
			person.setName("Test Person");
			person.setLastname("Test Lastname");
		}

	}

	@Test
	@Order(1)
	public void stage1_testCreateBaseUser() {
		BaseUser baseUser = person;

		BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(baseUser);
		assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
		assertEquals(createdBaseUser, baseUser, "Base user is created");
		person.setId(createdBaseUser.getId());
	}

	@Test
	@Order(2)
	public void stage2_testCreatePerson() {
		Person createdPerson = PersonDao.INSTANCE.create(person);
		assertNotEquals(person.getId(), 0, "Person ID should not be null before creating Person");
		assertEquals(createdPerson, person, "Person is created");
	}
}
