package dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostDaoTest {

	private static Person person;
	private static Post post;

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
		if (post == null){
			post = new Post();
			post.setTitle("Test Post");
			post.setDescription("Test Description");
			post.setEventType("jam");
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

	@Test
	@Order(3)
	public void stage3_testCreatePost() {
		post.setPersonId(person.getId());
		Post createdPost = PostDao.INSTANCE.create(post);
		assertNotEquals(post.getId(), 0, "Post ID should not be null before creating Post");
		assertEquals(createdPost, post, "Post is created");
	}

	@Test
	@Order(4)
	public void stage4_testGetPost() {
		Post retrievedPost = PostDao.INSTANCE.getPost(post.getId());
		assertEquals(retrievedPost.getId(), post.getId(), "Post id should be the same");
		assertEquals(retrievedPost.getPersonId(), post.getPersonId(), "Person id should be the same");
		assertEquals(retrievedPost.getTitle(), post.getTitle(), "Post title should be the same");
		assertEquals(retrievedPost.getDescription(), post.getDescription(), "Post description should be the same");
		assertEquals(retrievedPost.getEventType(), post.getEventType(), "Post event type should be the same");
	}
}