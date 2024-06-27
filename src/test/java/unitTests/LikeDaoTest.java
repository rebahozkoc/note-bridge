package unitTests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.LikeDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Like;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.model.Post;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * This class is used to test the LikeDao class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LikeDaoTest {

    /**
     * The person to be used in the tests.
     */
    private static Person person;
    /**
     * The post to be used in the tests.
     */
    private static Post post;

    /**
     * This method is used to set up the tests before all tests.
     */
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

    /**
     * This method is used to tear down the tests.
     */
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

    /**
     * This method is used to set up the tests.
     */
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
        if (post == null) {
            post = new Post();
            post.setTitle("Test Post");
            post.setDescription("Test Description");
            post.setEventType("jam");
        }
    }

    /**
     * This method is used to test the creation of a base user.
     */
    @Test
    @Order(1)
    public void stage1_testCreateBaseUser() {
        BaseUser baseUser = person;

        BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(baseUser);
        assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser, baseUser, "Base user is created");
        person.setId(createdBaseUser.getId());
    }

    /**
     * This method is used to test the creation of a person.
     */
    @Test
    @Order(2)
    public void stage2_testCreatePerson() {
        Person createdPerson = PersonDao.INSTANCE.create(person);
        assertNotEquals(person.getId(), 0, "Person ID should not be null before creating Person");
        assertEquals(createdPerson, person, "Person is created");
    }

    /**
     * This method is used to test the creation of a post.
     */
    @Test
    @Order(3)
    public void stage3_testCreatePost() {
        post.setPersonId(person.getId());
        Post createdPost = PostDao.INSTANCE.create(post);
        assertNotEquals(post.getId(), 0, "Post ID should not be null before creating Post");
        assertEquals(createdPost, post, "Post is created");
    }

    /**
     * This method is used to test the isLiked method of the LikeDao class.
     */
    @Test
    @Order(4)
    public void stage4_testIsLikedBeforeLike() {
        Boolean isLiked = LikeDao.INSTANCE.isLiked(post.getId(), person.getId());
        assertEquals(isLiked, false, "Post should not be liked");
    }

    /**
     * This method is used to test the toggleLike method of the PostDao class.
     */
    @Test
    @Order(5)
    public void stage5_testIsLikedAfterLike() {

        Like like = new Like();
        like.setPersonId(person.getId());
        like.setPostId(post.getId());
        Boolean isLiked = LikeDao.INSTANCE.isLiked(post.getId(), person.getId());

        assertEquals(isLiked, false, "Post should not be liked");

        Like createdLike = PostDao.INSTANCE.toggleLike(like);
        assertEquals(createdLike.getPersonId(), person.getId(), "Person id should be the same");
        assertEquals(createdLike.getPostId(), post.getId(), "Post id should be the same");

        isLiked = LikeDao.INSTANCE.isLiked(post.getId(), person.getId());
        assertEquals(isLiked, true, "Post should be liked");
    }

    /**
     * This method is used to test the getTotalLikes method of the LikeDao class.
     */
    @Test
    @Order(6)
    public void stage6_testGetTotalLikes() {
        int totalLikes = LikeDao.INSTANCE.getTotalLikes(post.getId());
        assertEquals(totalLikes, 1, "Total likes should be 1");
    }


}