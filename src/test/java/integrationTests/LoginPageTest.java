package integrationTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ut.twente.notebridge.utils.Utils;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginPageTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", Utils.readFromProperties("SELENIUM_DRIVER_PATH"));
            driver = new ChromeDriver();
            driver.get("http://localhost:8080/notebridge/login.html");
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
     * Tests if the 'Login' form does not allow users with incorrect credentials to log in.
     * In this case, a warning message appears.
     */
    @Test
    @Order(2)
    public void testLoginFormWrongCredentials() {
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));
        WebElement warningMessage = driver.findElement(By.id("warning-message"));

        emailField.sendKeys("wrongemail@example.com");
        passwordField.sendKeys("wrongPassword");
        loginBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(warningMessage));

        assertTrue(warningMessage.isDisplayed());

        if(warningMessage.isDisplayed()) {
            System.out.println("Warning message is displayed, its content is: " + warningMessage.getText());
        } else {
            System.out.println("Warning message is not displayed");
        }
    }

    /**
     * Tests if the 'Login' form works correctly. In this case, the user must be redirected to the Home page, which
     * has the 'Log out', 'Messenger' and 'Profile' buttons displayed in the navigation bar.
     */
    @Test
    @Order(3)
    public void testLoginFormCorrectCredentials() {
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));

        emailField.sendKeys("alex@example.com");
        passwordField.sendKeys("Superpassword.123");
        loginBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("http://localhost:8080/notebridge/home.html"));

        String expectedPageUrl = "http://localhost:8080/notebridge/home.html";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("log-out-btn"))));

        WebElement messengerBtn = driver.findElement(By.id("messenger-btn"));
        WebElement profileBtn = driver.findElement(By.id("profile-btn"));
        WebElement logoutBtn = driver.findElement(By.id("log-out-btn"));

        assertTrue(messengerBtn.isDisplayed());
        assertTrue(logoutBtn.isDisplayed());
        assertTrue(profileBtn.isDisplayed());
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
