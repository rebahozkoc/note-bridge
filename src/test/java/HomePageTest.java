import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.*;

public class HomePageTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver-win32\\chromedriver.exe");
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
    }

    /**
     * Tests if the Login button redirects the user to the Login page.
     */
    @Test
    @Order(3)
    public void testLoginBtn() {
        WebElement loginBtn = driver.findElement(By.id("login-btn"));
        loginBtn.click();
        String expectedPageUrl = "http://localhost:8080/notebridge/login.html";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);
    }

    /**
     * Tests if the Profile button is hidden when the user is logged out.
     */
    @Test
    @Order(4)
    public void testHiddenProfileBtn() {
        WebElement profileBtn = driver.findElement(By.id("profile-btn"));

        assertFalse(profileBtn.isDisplayed());

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
    @Order(5)
    public void testHiddenMessengerBtn() {
        WebElement messengerBtn = driver.findElement(By.id("messenger-btn"));

        assertFalse(messengerBtn.isDisplayed());

        if(messengerBtn.isDisplayed()){
            System.out.println("Messenger button is displayed.");
        } else {
            System.out.println("Messenger button is not displayed.");
        }
    }

    /**
     * Tests if the 'Contact us' button redirects the user to the 'Contact us' form.
     */
    @Test
    @Order(6)
    public void testContactUsBtn() {
        WebElement contactUsBtn = driver.findElement(By.id("contact-us-btn"));
        contactUsBtn.click();
        String expectedPageUrl = "http://localhost:8080/notebridge/#feedback-form-container";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);
    }

    /**
     * Tests if the 'Contact us' form is sent successfully. In this case, an alert is shown on the screen with a specific message.
     */
    @Test
    @Order(7)
    public void testContactUsForm() {
        driver.get("http://localhost:8080/notebridge/#feedback-form-container");
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement messageField = driver.findElement(By.id("message"));
        WebElement sendMessageBtn = driver.findElement(By.id("send-message-btn"));

        emailField.sendKeys("testEmailField@example.com");
        messageField.sendKeys("testMessageField");
        sendMessageBtn.sendKeys(Keys.RETURN);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        if (alertText != null) {
            System.out.println("Alert is shown on the screen, its content is: " + alertText);
        } else {
            System.out.println("The alert did not appear.");
        }
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
