package unitTests;

import jakarta.ws.rs.NotFoundException;
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
import ut.twente.notebridge.dto.PostDto;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Like;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.utils.DatabaseConnection;

import java.util.List;

import static org.junit.Assert.assertThrows;
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
		PostDao.INSTANCE.deleteAll();

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

	@Test
	@Order(5)
	public void stage5_testUpdatePost() {
		post.setTitle("Updated Post");
		post.setDescription("Updated Description");
		Post updatedPost = PostDao.INSTANCE.update(post);
		assertEquals(updatedPost.getTitle(), post.getTitle(), "Post title should be updated");
		assertEquals(updatedPost.getDescription(), post.getDescription(), "Post description should be updated");
	}

	@Test
	@Order(6)
	public void stage6_testGetPosts() {
		Post post2 = new Post();
		post2.setTitle("Test Post 2");
		post2.setDescription("Test Description 2");
		post2.setEventType("jam");
		post2.setPersonId(person.getId());
		PostDao.INSTANCE.create(post2);
		Post post3 = new Post();
		post3.setTitle("Test Post 3");
		post3.setDescription("Test Description 3");
		post3.setEventType("jam");
		post3.setPersonId(person.getId());
		PostDao.INSTANCE.create(post3);
		List<PostDto> returnedPosts = PostDao.INSTANCE.getPosts(5,1, null, false, null);
		assertEquals(returnedPosts.size(), 3, "There should be 3 posts");

		PostDao.INSTANCE.delete(post2.getId());
		PostDao.INSTANCE.delete(post3.getId());

	}

	@Test
	@Order(7)
	public void stage7_testGetMyPosts() {
		Post post2 = new Post();
		post2.setTitle("Test Post 2");
		post2.setDescription("Test Description 2");
		post2.setEventType("jam");
		post2.setPersonId(person.getId());
		PostDao.INSTANCE.create(post2);

		Person person2 = new Person();
		person2.setUsername("testusername2");
		person2.setEmail("testusername@gmail.com");
		person2.setPassword("Password.123");
		person2.setName("Test Person 2");
		person2.setLastname("Test Lastname 2");
		BaseUser baseUser = BaseUserDao.INSTANCE.create(person2);
		person2.setId(baseUser.getId());
		PersonDao.INSTANCE.create(person2);

		Post post3 = new Post();
		post3.setTitle("Test Post 3");
		post3.setDescription("Test Description 3");
		post3.setEventType("jam");
		post3.setPersonId(person2.getId());
		PostDao.INSTANCE.create(post3);

		List<PostDto> returnedPosts = PostDao.INSTANCE.getPosts(5,1, null, false, person.getId());
		assertEquals(returnedPosts.size(), 2, "There should be 2 posts of this user");

		PostDao.INSTANCE.delete(post2.getId());
		PostDao.INSTANCE.delete(post3.getId());
		PersonDao.INSTANCE.delete(person2.getId());
		BaseUserDao.INSTANCE.delete(person2.getId());

	}



	@Test
	@Order(8)
	public void stage8_testToggleLike(){
		Like like = new Like();
		like.setPersonId(person.getId());
		like.setPostId(post.getId());
		Like createdLike = PostDao.INSTANCE.toggleLike(like);
		assertEquals(createdLike.getPersonId(), person.getId(), "Person id should be the same");
		assertEquals(createdLike.getPostId(), post.getId(), "Post id should be the same");

	}

	@Test
	@Order(9)
	public void state9_testGetTotalPosts(){
		int totalPosts = PostDao.INSTANCE.getTotalPosts();
		assertEquals(totalPosts, 1, "There should be 1 post");
	}

	@Test
	@Order(10)
	public void stage10_testDeletePost() {
		PostDao.INSTANCE.delete(post.getId());
		assertThrows(NotFoundException.class, () -> PostDao.INSTANCE.getPost(post.getId()));
	}
}