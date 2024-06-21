import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import ut.twente.notebridge.utils.Utils;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class CardDetailsSeleniumTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\simay\\OneDrive\\Masaüstü\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
            driver = new ChromeDriver();
            driver.get("http://localhost:8080/notebridge/");
        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tests if the title of the page is correct.
     */
    @Test
    @Order(1)
    public void testPageTitle() {
        String expectedTitle = "Note-Bridge";
        String actualTitle = driver.getTitle();
        assertEquals(expectedTitle, actualTitle);
    }

    /**
     * Tests if the 'Browse posts' button redirects the user to the 'Browse posts' page.
     */
    @Test
    @Order(2)
    public void testBrowsePostsBtn() {
        WebElement browsePostsBtn = driver.findElement(By.id("browse-posts-btn"));
        browsePostsBtn.click();
        String expectedPageUrl = "http://localhost:8080/notebridge/cards.html";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);
        driver.navigate().back();
    }

    /**
     * Tests if the 'Contact us' button redirects the user to the 'Contact us' form.
     */
    @Test
    @Order(3)
    public void testContactUsBtn() {
        WebElement contactUsBtn = driver.findElement(By.id("contact-us-btn"));
        contactUsBtn.click();
        String expectedPageUrl = "http://localhost:8080/notebridge/#feedback-form-container";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);
        driver.navigate().back();
    }

    /**
     * Tests if the Login button redirects the user to the Login page.
     */
    @Test
    @Order(4)
    public void testLoginBtn() {
        WebElement loginBtn = driver.findElement(By.id("log-in-btn"));
        loginBtn.click();
        String expectedPageUrl = "http://localhost:8080/notebridge/login.html";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);
        driver.navigate().back();
    }

    /**
     * Tests if the Profile button is hidden when the user is logged out.
     */
    @Test
    @Order(5)
    public void testHiddenProfileBtn() {
        WebElement profileBtn = driver.findElement(By.id("profile-btn"));
        if(profileBtn.isDisplayed()){
            System.out.println("Profile button is displayed.");
        } else {
            System.out.println("Profile button is not displayed.");
        }
    }

    /**
     * Tests if the Messenger button is hidden when the user is logged out.
     */
    @Test
    @Order(6)
    public void testHiddenMessengerBtn() {
        WebElement messengerBtn = driver.findElement(By.id("messenger-btn"));
        if(messengerBtn.isDisplayed()){
            System.out.println("Messenger button is displayed.");
        } else {
            System.out.println("Messenger button is not displayed.");
        }
    }

public class CardsTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", "D:\\Uni\\BIT 2023-2024\\MOD4\\.Project\\browserDriver\\chromedriver.exe");
            driver = new ChromeDriver();
        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
    @Test
    public void testInterested() {
        driver.get("http://localhost:8080/notebridge/card-details.html?id=180");

        WebElement button = driver.findElement(By.id("interested-button"));
        button.click();
        assertEquals("http://localhost:8080/notebridge/login.html", driver.getCurrentUrl());
    }


    @Test
    public void openCard() {
        driver.get("http://localhost:8080/notebridge/cards.html");
        WebElement card = driver.findElement(By.id("cards"));
        card.sendKeys(Keys.RETURN);
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/notebridge/card-details.html");
    }
     **/

    @Test
    public void editCard() {
        driver.get("http://localhost:8080/notebridge/login.html");

        WebElement email = driver.findElement(By.id("email"));
        WebElement pass = driver.findElement(By.id("password"));
        WebElement login = driver.findElement(By.id("login-button"));

        email.sendKeys("a@email.com");
        pass.sendKeys("A12345678!!");
        login.click();

        assertEquals("http://localhost:8080/notebridge/", driver.getCurrentUrl());

    }



    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }



}
