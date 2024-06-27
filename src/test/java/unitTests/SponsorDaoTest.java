package unitTests;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.*;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.SponsorDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Sponsor;
import ut.twente.notebridge.utils.DatabaseConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class is used to test the SponsorDao class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SponsorDaoTest {
    /**
     * The sponsor to be used in the tests.
     */
    private static Sponsor sponsor;

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
        SponsorDao.INSTANCE.deleteAll();
        BaseUserDao.INSTANCE.deleteAll();

    }

    /**
     * This method is used to tear down the tests.
     */
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

    /**
     * This method is used to set up the tests.
     */
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

    /**
     * This method is used to test the creation of a base user.
     */
    @Test
    @Order(1)
    public void stage1_testCreateBaseUser() {
        BaseUser baseUser = sponsor;

        BaseUser createdBaseUser = BaseUserDao.INSTANCE.create(baseUser);
        assertNotEquals(createdBaseUser.getId(), 0, "Base user ID should not be null");
        assertEquals(createdBaseUser, baseUser, "Base user is created");
        sponsor.setId(createdBaseUser.getId());
    }

    /**
     * This method is used to test the creation of a sponsor.
     */
    @Test
    @Order(2)
    public void stage2_testCreateSponsor() {
        Sponsor createdSponsor = SponsorDao.INSTANCE.create(sponsor);
        assertNotEquals(sponsor.getId(), 0, "Sponsor ID should not be null before creating sponsor");
        assertEquals(createdSponsor, sponsor, "Sponsor is created");
    }

    /**
     * This method is used to test the retrieval of a sponsor.
     */
    @Test
    @Order(3)
    public void stage2_getSponsor() {
        Sponsor newSponsor = SponsorDao.INSTANCE.getSponsor(sponsor.getId());
        assertEquals(sponsor.getEmail(), newSponsor.getEmail(), "Sponsor is retrieved");
        assertEquals(sponsor.getId(), newSponsor.getId(), "Sponsor is retrieved");
        assertEquals(sponsor.getUsername(), newSponsor.getUsername(), "Sponsor is retrieved");
        assertEquals(sponsor.getPhoneNumber(), newSponsor.getPhoneNumber(), "Sponsor is retrieved");
        assertEquals(sponsor.getPicture(), newSponsor.getPicture(), "Sponsor is retrieved");
        assertEquals(sponsor.getDescription(), newSponsor.getDescription(), "Sponsor is retrieved");
        assertEquals(sponsor.getCompanyName(), newSponsor.getCompanyName(), "Sponsor is retrieved");
        assertEquals(sponsor.getWebsiteURL(), newSponsor.getWebsiteURL(), "Sponsor is retrieved");
        assertEquals(sponsor.getPassword(), newSponsor.getPassword(), "Sponsor is retrieved");
    }

    /**
     * This method is used to test the update of a sponsor.
     */
    @Test
    @Order(4)
    public void stage3_updateSponsor() {
        Sponsor updatedSponsor = sponsor;
        updatedSponsor.setCompanyName("Updated Company Name");
        updatedSponsor.setWebsiteURL("wwww.updated.com");
        updatedSponsor.setPhoneNumber("1234567890");
        updatedSponsor.setPicture("picture.jpg");
        updatedSponsor.setDescription("Updated Description");
        SponsorDao.INSTANCE.update(updatedSponsor);
        Sponsor newSponsor = SponsorDao.INSTANCE.getSponsor(sponsor.getId());
        assertEquals(updatedSponsor.getEmail(), newSponsor.getEmail(), "Sponsor is updated");
        assertEquals(updatedSponsor.getId(), newSponsor.getId(), "Sponsor is updated");
        assertEquals(updatedSponsor.getUsername(), newSponsor.getUsername(), "Sponsor is updated");
        assertEquals(updatedSponsor.getPhoneNumber(), newSponsor.getPhoneNumber(), "Sponsor is updated");
        assertEquals(updatedSponsor.getPicture(), newSponsor.getPicture(), "Sponsor is updated");
        assertEquals(updatedSponsor.getDescription(), newSponsor.getDescription(), "Sponsor is updated");
        assertEquals(updatedSponsor.getCompanyName(), newSponsor.getCompanyName(), "Sponsor is updated");
        assertEquals(updatedSponsor.getWebsiteURL(), newSponsor.getWebsiteURL(), "Sponsor is updated");
        assertEquals(updatedSponsor.getPassword(), newSponsor.getPassword(), "Sponsor is updated");
    }

    /**
     * This method is used to test the deletion of a sponsor.
     */
    @Test
    @Order(5)
    public void stage4_deleteSponsor() {
        SponsorDao.INSTANCE.delete(sponsor.getId());

        assertThrows(NotFoundException.class, () -> {
                    SponsorDao.INSTANCE.getSponsor(sponsor.getId());
                },
                "Sponsor is deleted");
    }
}
