package unitTests;

import jakarta.ws.rs.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the BaseUserDao class.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BaseUserDaoTest {

    /**
     * The base user to be used in the tests.
     */
    private static BaseUser baseUser;
    /**
     * The malicious base user to be used in the tests.
     */
    private static BaseUser maliciousBaseUser;

    /**
     * This method is used to set up the tests.
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

        //initalize malicious base user
        maliciousBaseUser = new BaseUser();
        maliciousBaseUser.setUsername("<script>alert('XSS');</script>\n");
        maliciousBaseUser.setEmail("<a href=\"javascript:alert('XSS')\">Click me</a>\n");
        maliciousBaseUser.setPassword("fkjnsdf");
        // Clean the database
        BaseUserDao.INSTANCE.deleteAll();
    }

    /**
     * This method is used to tear down the tests.
     */
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
        if (baseUser == null) {
            baseUser = new BaseUser();
            baseUser.setUsername("testusername");
            baseUser.setEmail("testemail@gmail.com");
            baseUser.setPassword("Password.123");
        }
    }

    /**
     * This method is used to test the create method of the BaseUserDao class.
     */
    @Test
    @Order(1)
    public void stage1_testCreateBaseUser() {
        BaseUser baseUser = BaseUserDaoTest.baseUser;

        BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(baseUser);
        assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser, baseUser, "Base user is created");
        BaseUserDaoTest.baseUser.setId(createdBaseUser.getId());
    }


    /**
     * This method is used to test the getUser method of the BaseUserDao class.
     */
    @Test
    @Order(2)
    public void stage2_getBaseUser() {
        BaseUser newBaseUser = BaseUserDao.INSTANCE.getUser(BaseUserDaoTest.baseUser.getId());
        assertEquals(baseUser.getEmail(), newBaseUser.getEmail(), "Base user is retrieved by email");
        assertEquals(baseUser.getId(), newBaseUser.getId(), "Base user is retrieved by email");
        assertEquals(baseUser.getUsername(), newBaseUser.getUsername(), "Base user is retrieved by email");
        assertNotEquals(baseUser.getPassword(), newBaseUser.getPassword(), "Base user is retrieved by email");
    }

    /**
     * This method is used to test the getUserByEmail method of the BaseUserDao class.
     */
    @Test
    @Order(3)
    public void stage3_testGetBaseUserByEmail() {
        BaseUser newBaseUser = BaseUserDao.INSTANCE.getUserByEmail(BaseUserDaoTest.baseUser.getEmail());
        assertEquals(baseUser.getEmail(), newBaseUser.getEmail(), "Base user is retrieved by email");
        assertEquals(baseUser.getId(), newBaseUser.getId(), "Base user is retrieved by email");
        assertEquals(baseUser.getUsername(), newBaseUser.getUsername(), "Base user is retrieved by email");
        assertNotEquals(baseUser.getPassword(), newBaseUser.getPassword(), "Base user is retrieved by email");
    }

    /**
     * This method is used to test the isPerson method of the BaseUserDao class.
     */
    @Test
    @Order(4)
    public void stage4_isPerson() {
        Boolean isPerson = BaseUserDao.INSTANCE.isPerson(BaseUserDaoTest.baseUser.getId());
        assertEquals(false, isPerson, "Base user is not a person");
    }

    /**
     * This method is used to test the update method of the BaseUserDao class.
     */
    @Test
    @Order(5)
    public void stage5_updateBaseUser() {
        baseUser.setUsername("newusername");
        BaseUser newBaseUser = BaseUserDao.INSTANCE.update(baseUser);
        assertEquals(baseUser.getEmail(), newBaseUser.getEmail(), "Base user is updated");
        assertEquals(baseUser.getId(), newBaseUser.getId(), "Base user is updated");
        assertEquals(baseUser.getUsername(), newBaseUser.getUsername(), "Base user is updated");

        baseUser.setDescription("new description");
        newBaseUser = BaseUserDao.INSTANCE.update(baseUser);
        assertEquals(baseUser.getDescription(), newBaseUser.getDescription(), "Base user is updated");
        assertEquals(baseUser.getEmail(), newBaseUser.getEmail(), "Base user is updated");
        assertEquals(baseUser.getId(), newBaseUser.getId(), "Base user is updated");
        assertEquals(baseUser.getUsername(), newBaseUser.getUsername(), "Base user is updated");

        baseUser.setPhoneNumber("+1234567890");
        newBaseUser = BaseUserDao.INSTANCE.update(baseUser);
        assertEquals(baseUser.getPhoneNumber(), newBaseUser.getPhoneNumber(), "Base user is updated");
        assertEquals(baseUser.getDescription(), newBaseUser.getDescription(), "Base user is updated");
        assertEquals(baseUser.getEmail(), newBaseUser.getEmail(), "Base user is updated");
        assertEquals(baseUser.getId(), newBaseUser.getId(), "Base user is updated");
        assertEquals(baseUser.getUsername(), newBaseUser.getUsername(), "Base user is updated");

    }

    /**
     * This method is used to test the delete method of the BaseUserDao class.
     */
    @Test
    @Order(6)
    public void stage6_deleteBaseUser() {
        BaseUserDao.INSTANCE.delete(BaseUserDaoTest.baseUser.getId());
        assertThrows(NotFoundException.class, () -> {
            BaseUserDao.INSTANCE.getUser(BaseUserDaoTest.baseUser.getId());
        }, "Base user is deleted");
    }

    /**
     * This method is used to test the create method of the BaseUserDao class with malicious input.
     */
    @Test
    @Order(7)
    public void stage1_testCreateMaliciousBaseUser() {
        BaseUser maliciousBaseUser1 = BaseUserDaoTest.maliciousBaseUser;

        BaseUser userStoredInDatabase = BaseUserDao.INSTANCE.getUser(BaseUserDao.INSTANCE.create(maliciousBaseUser1).getId());

        System.out.println(userStoredInDatabase.getUsername());
        System.out.println(userStoredInDatabase.getEmail());

        assertTrue(!userStoredInDatabase.getUsername().equals(maliciousBaseUser1.getUsername()));
        assertTrue(!userStoredInDatabase.getEmail().equals(maliciousBaseUser1.getEmail()));

    }

}
