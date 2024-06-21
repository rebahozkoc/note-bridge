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

public class RegisterPageSeleniumTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver-win32\\chromedriver.exe");
            driver = new ChromeDriver();
            driver.get("http://localhost:8080/notebridge/register.html");
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
     * Tests if the 'Register' form works correctly. In this case, an alert is shown on the screen with a specific message.
     * After the user signs up, the user must be redirected to the Home page.
     */
//    @Test
//    @Order(2)
//    public void testLoginFormCorrectCredentials() {
//        WebElement emailField = driver.findElement(By.id("email"));
//        WebElement passwordField = driver.findElement(By.id("password"));
//        WebElement loginBtn = driver.findElement(By.id("login-btn"));
//
//        emailField.sendKeys("alex@example.com");
//        passwordField.sendKeys("Superpassword.123");
//        loginBtn.click();
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.until(ExpectedConditions.alertIsPresent());
//        Alert alert = driver.switchTo().alert();
//        String alertText = alert.getText();
//
//        if (alertText != null) {
//            System.out.println("Alert is shown on the screen, its content is: " + alertText);
//        } else {
//            System.out.println("The alert did not appear.");
//        }
//        alert.accept();
//
//        String expectedPageUrl = "http://localhost:8080/notebridge/home.html";
//        String actualPageUrl = driver.getCurrentUrl();
//        assertEquals(expectedPageUrl, actualPageUrl);
//    }
//
//    /**
//     * Tests if the 'Login' form does not allow users with incorrect credentials to log in.
//     * In this case, an alert is shown on the screen with a specific message.
//     */
//    @Test
//    @Order(3)
//    public void testLoginFormWrongCredentials() {
//        WebElement emailField = driver.findElement(By.id("email"));
//        WebElement passwordField = driver.findElement(By.id("password"));
//        WebElement loginBtn = driver.findElement(By.id("login-btn"));
//        WebElement warningMessage = driver.findElement(By.id("warning-message"));
//
//        emailField.sendKeys("wrongemail@example.com");
//        passwordField.sendKeys("wrongPassword");
//        loginBtn.click();
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.until(ExpectedConditions.alertIsPresent());
//        Alert alert = driver.switchTo().alert();
//        String alertText = alert.getText();
//        alert.accept();
//
//        if (alertText != null) {
//            System.out.println("Alert is shown on the screen, its content is: " + alertText);
//        } else {
//            System.out.println("The alert did not appear.");
//        }
//
//        if(warningMessage.isDisplayed()) {
//            System.out.println("Warning message is displayed, its content is: " + warningMessage.getText());
//        } else {
//            System.out.println("Warning message is not displayed");
//        }
//    }
//
//    @AfterClass
//    public static void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
}