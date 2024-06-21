package dao;
import org.junit.jupiter.api.*;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


public class SponsorDaoTest {
    private static Sponsor sponsor;

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
        SponsorDao.INSTANCE.deleteAll();
        BaseUserDao.INSTANCE.deleteAll();

    }

    @AfterAll
    public static void tearDownAll() {

        SponsorDao.INSTANCE.deleteAll();
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
        if (sponsor == null) {
            sponsor = new Sponsor();
            sponsor.setCompanyName("Test Company");
            sponsor.setWebsiteURL("www.google.comm");
            sponsor.setEmail("testsponsor@gmail.com");
            sponsor.setUsername("iamsponsor");
            sponsor.setPassword("Password.123");
            sponsor.setPhoneNumber("1234567890");


        }

    }
    @Test
    @Order(1)
    public void stage1_testCreateBaseUser() {
        BaseUser baseUser = sponsor;

        BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(baseUser);
        assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser, baseUser, "Base user is created");
        sponsor.setId(createdBaseUser.getId());
    }
    @Test
    @Order(2)
    public void stage2_testCreateSponsor() {
        Sponsor createdSponsor = SponsorDao.INSTANCE.create(sponsor);
        assertNotEquals(sponsor.getId(), 0, "Sponsor ID should not be null before creating sponsor");
        assertEquals(createdSponsor, sponsor, "Sponsor is created");
    }

}
