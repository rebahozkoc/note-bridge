import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateCardPageTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver-win32\\chromedriver.exe");
            driver = new ChromeDriver();
            driver.get("http://localhost:8080/notebridge/create-card.html");
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
    }

    /**
     * Tests if the form to create posts works correctly. After a post is created, the user is redirected to the 'Cards' page.
     */
    @Test
    @Order(4)
    public void testCreateCard() {
        driver.get("http://localhost:8080/notebridge/login.html");
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));

        emailField.sendKeys("alex@example.com");
        passwordField.sendKeys("Superpassword.123");
        loginBtn.click();

        driver.get("http://localhost:8080/notebridge/create-card.html");
        WebElement title = driver.findElement(By.id("title"));
        WebElement description = driver.findElement(By.id("description"));
        WebElement location = driver.findElement(By.id("location"));
        WebElement createPostBtn = driver.findElement(By.id("create-post-btn"));

        title.sendKeys("New post title");
        description.sendKeys("New post description");
        location.sendKeys("New post location");
        createPostBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("http://localhost:8080/notebridge/cards.html"));

        String expectedPageUrl = "http://localhost:8080/notebridge/cards.html";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);

        WebElement cardTitle = driver.findElement(By.cssSelector("#displayed-card > div > h5"));
        assertEquals(title.getText(), cardTitle.getText());
    }


    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}