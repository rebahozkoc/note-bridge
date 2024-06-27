package unitTests;

import jakarta.ws.rs.NotFoundException;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.dao.PostDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the MessageDao class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageDaoTest {
    /**
     * The message to be used in the tests.
     */
    private static Message m;

    /**
     * The message history to be used in the tests.
     */
    private static MessageHistory mh;

    /**
     * The first base user to be used in the tests.
     */
    private static BaseUser user1;
    /**
     * The second base user to be used in the tests.
     */
    private static BaseUser user2;

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
        MessageDao.INSTANCE.deleteAllMessages();
        MessageDao.INSTANCE.deleteAllHistories();
        PersonDao.INSTANCE.deleteAll();

    }

    /**
     * This method is used to tear down the tests.
     */
    @AfterAll
    public static void tearDownAll() {

        MessageDao.INSTANCE.deleteAllMessages();
        MessageDao.INSTANCE.deleteAllHistories();
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

    /**
     * This method is used to set up the tests.
     */
    @BeforeEach
    public void setUp() {
        if (user1 == null) {
            user1 = new BaseUser();
            user1.setUsername("testusername");
            user1.setEmail("testemail@gmail.com");
            user1.setPassword("Password.123");
        }
        if (user2 == null) {
            user2 = new BaseUser();
            user2.setUsername("testusername2");
            user2.setEmail("testemail2@gmail.com");
            user2.setPassword("Password.1233");
        }
        if (m == null) {
            m = new Message();
            m.setContent("content");
        }
        if (mh == null) {
            mh = new MessageHistory();
        }
    }

    /**
     * This method is used to test the create method of the MessageDao class.
     */
    @Test
    @Order(1)
    public void stage1_testCreateBaseUser() {

        BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(user1);
        assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser, user1, "Base user is created");
        user1.setId(createdBaseUser.getId());
        BaseUser createdBaseUser2 = BaseUserDao.INSTANCE.create(user2);
        assertNotEquals(createdBaseUser2.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser2, user2, "Base user is created");
        user2.setId(createdBaseUser2.getId());
    }

    /**
     * This method is used to test the create message history method of the MessageDao class.
     */
    @Test
    @Order(2)
    public void stage2_testCreateMessageHistory() {
        mh.setUser1(Integer.toString(user1.getId()));
        mh.setUser2(Integer.toString(user2.getId()));
        MessageHistory createdMh = MessageDao.INSTANCE.create(mh);
        assertNotEquals(mh.getId(), 0, "History ID should not be null after creating History");
        assertEquals(createdMh, mh, "History is created");
    }

    /**
     * This method is used to test the create message method of the MessageDao class.
     */
    @Test
    @Order(3)
    public void stage3_testCreateMessage() {
        m.setUser_id(user1.getId());
        Message createdMessage = MessageDao.INSTANCE.createNewMessage(Integer.toString(user2.getId()), m);
        assertNotEquals(m.getId(), 0, "Message ID should not be null after creating Message");
        assertNotEquals(m.getMessagehistory_id(), 0, "Message History ID should not be null after creating Message");
        assertEquals(createdMessage, m, "Message is created");
    }

    /**
     * This method is used to test the get contacts method of the MessageDao class.
     */
    @Test
    @Order(4)
    public void stage4_testGetContacts() {
        BaseUser user3 = new BaseUser();
        user3.setUsername("testusername3");
        user3.setEmail("testemail3@gmail.com");
        user3.setPassword("Password.1233333");
        BaseUser createdBaseUser3 = BaseUserDao.INSTANCE.create(user3);
        assertNotEquals(createdBaseUser3.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser3, user3, "Base user is created");
        user3.setId(createdBaseUser3.getId());
        MessageHistory mh2 = new MessageHistory();
        mh2.setUser1(Integer.toString(user1.getId()));
        mh2.setUser2(Integer.toString(user3.getId()));
        MessageHistory createdMh = MessageDao.INSTANCE.create(mh2);
        assertNotEquals(mh2.getId(), 0, "History ID should not be null after creating History");
        assertEquals(createdMh, mh2, "History is created");
        MessageHistory mh3 = new MessageHistory();
        mh3.setUser1(Integer.toString(user2.getId()));
        mh3.setUser2(Integer.toString(user3.getId()));
        MessageHistory createdMh2 = MessageDao.INSTANCE.create(mh3);
        assertNotEquals(mh3.getId(), 0, "History ID should not be null after creating History");
        assertEquals(createdMh2, mh3, "History is created");
        List<BaseUser> contacts = MessageDao.INSTANCE.getContacts(5, 1, null,
                String.valueOf(user3.getId()));
        assertEquals(contacts.size(), 2, "There should be 2 contacts of this user");
    }

    /**
     * This method is used to test the get messages method of the MessageDao class.
     */
    @Test
    @Order(5)
    public void stage5_testGetMessages() {
        Message m1 = new Message();
        m1.setContent("content");
        m1.setUser_id(user1.getId());
        Message createdMessage = MessageDao.INSTANCE.createNewMessage(Integer.toString(user2.getId()), m1);
        assertNotEquals(m1.getId(), 0, "Message ID should not be null after creating Message");
        assertNotEquals(m1.getMessagehistory_id(), 0, "Message History ID should not be null after creating Message");
        assertEquals(createdMessage, m1, "Message is created");
        Message m2 = new Message();
        m2.setContent("content");
        m2.setUser_id(user2.getId());
        Message createdMessage2 = MessageDao.INSTANCE.createNewMessage(Integer.toString(user1.getId()), m2);
        assertNotEquals(m2.getId(), 0, "Message ID should not be null after creating Message");
        assertNotEquals(m2.getMessagehistory_id(), 0, "Message History ID should not be null after creating Message");
        assertEquals(createdMessage2, m2, "Message is created");
        List<Message> messages = MessageDao.INSTANCE.getMessages(5, 1, null, String.valueOf(user1.getId()), String.valueOf(user2.getId()));
        assertEquals(messages.size(), 3, "There should be 3 messages of this user");
    }

    /**
     * This method is used to test the count unread messages method of the MessageDao class.
     */
    @Test
    @Order(6)
    public void stage6_testReadMessage() {
        int firstCount = MessageDao.INSTANCE.countUnreadMessages(user2.getId(), user1.getId());
        MessageDao.INSTANCE.readMessages(m.getId());
        assertNotEquals(firstCount, MessageDao.INSTANCE.countUnreadMessages(user2.getId(), user1.getId()));
    }

    /**
     * This method is used to test the delete message method of the MessageDao class.
     */
    @Test
    @Order(7)
    public void stage7_testDeleteMessage() {
        MessageDao.INSTANCE.deleteMessage(m, String.valueOf(m.getCreateddate()));
        assertFalse(MessageDao.INSTANCE.getMessages(5, 1, null, Integer.toString(m.getUser_id()), Integer.toString(user2.getId())).contains(m));
    }
}
