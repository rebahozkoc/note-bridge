package unitTests;

import java.sql.Timestamp;
import org.junit.jupiter.api.*;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.MessageDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Message;
import ut.twente.notebridge.model.MessageHistory;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MessageDaoTest {
    private static Message m;

    private static MessageHistory mh;

    private static BaseUser user1;
    private static BaseUser user2;

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
            m.setCreateddate(new Timestamp(System.currentTimeMillis()));
        }
        if(mh == null){
            mh=new MessageHistory();
        }
    }

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
        user2.setId(createdBaseUser.getId());
    }

    @Test
    @Order(2)
    public void stage2_testCreateMessageHistory(){
        mh.setUser1(Integer.toString(user1.getId()));
        mh.setUser2(Integer.toString(user2.getId()+1));
        MessageHistory createdMh=MessageDao.INSTANCE.create(mh);
        assertNotEquals(mh.getId(), 0, "History ID should not be null before creating History");
        assertEquals(createdMh, mh, "History is created");
    }

    @Test
    @Order(3)
    public void stage3_testCreateMessage(){
        m.setUser_id(user1.getId());
        Message createdMessage=MessageDao.INSTANCE.createNewMessage(Integer.toString(user2.getId()+1),m);
        assertNotEquals(m.getId(), 0, "Message ID should not be null before creating Message");
        assertEquals(createdMessage, m, "Message is created");
    }
}
