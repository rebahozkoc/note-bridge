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

public class RegisterPageTest {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", Utils.readFromProperties("SELENIUM_DRIVER_PATH"));
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
     * Tests if the 'Register' form works correctly. In this case, the user must be redirected to the Login page, and
     * an alert is shown on screen with a corresponding message.
     * Note: If you run this method more times, make sure to change the content of the fields 'username' and 'email', since
     * these must be unique each time the method is run (because a new account is created).
     */
    @Test
    @Order(2)
    public void testRegisterFormCorrectCredentials() {
        WebElement firstName = driver.findElement(By.id("firstname"));
        WebElement lastName = driver.findElement(By.id("lastname"));
        WebElement username = driver.findElement(By.id("username"));
        WebElement phoneNumber = driver.findElement(By.id("phoneNumber"));
        WebElement emailField = driver.findElement(By.id("floatingInput"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement repeatPasswordField = driver.findElement(By.id("repeat-password"));
        WebElement registerBtn = driver.findElement(By.id("register-btn"));

        firstName.sendKeys("Test first name");
        lastName.sendKeys("Test last name");
        username.sendKeys("Test username 8");
        phoneNumber.sendKeys("123456789");
        emailField.sendKeys("email108@example.com");
        passwordField.sendKeys("Superpassword.123");
        repeatPasswordField.sendKeys("Superpassword.123");
        registerBtn.click();

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

        String expectedPageUrl = "http://localhost:8080/notebridge/login.html";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedPageUrl, actualPageUrl);
    }

    /**
     * Tests if the 'Register' form allows users to create accounts only with unique usernames.
     * If this is not the case, a warning message appears.
     */
    @Test
    @Order(3)
    public void testRegisterFormNotUniqueUsername() {
        WebElement firstName = driver.findElement(By.id("firstname"));
        WebElement lastName = driver.findElement(By.id("lastname"));
        WebElement username = driver.findElement(By.id("username"));
        WebElement phoneNumber = driver.findElement(By.id("phoneNumber"));
        WebElement emailField = driver.findElement(By.id("floatingInput"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement repeatPasswordField = driver.findElement(By.id("repeat-password"));
        WebElement registerBtn = driver.findElement(By.id("register-btn"));
        WebElement warningMessage = driver.findElement(By.id("warning-message"));

        firstName.sendKeys("Test first name");
        lastName.sendKeys("Test last name");
        username.sendKeys("alex2");
        phoneNumber.sendKeys("123456789");
        emailField.sendKeys("email150@example.com");
        passwordField.sendKeys("Superpassword.123");
        repeatPasswordField.sendKeys("Superpassword.123");
        registerBtn.click();

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
     * Tests if the 'Register' form allows users to create accounts only with unique emails.
     * If this is not the case, a warning message appears.
     */
    @Test
    @Order(3)
    public void testRegisterFormNotUniqueEmail() {
        WebElement firstName = driver.findElement(By.id("firstname"));
        WebElement lastName = driver.findElement(By.id("lastname"));
        WebElement username = driver.findElement(By.id("username"));
        WebElement phoneNumber = driver.findElement(By.id("phoneNumber"));
        WebElement emailField = driver.findElement(By.id("floatingInput"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement repeatPasswordField = driver.findElement(By.id("repeat-password"));
        WebElement registerBtn = driver.findElement(By.id("register-btn"));
        WebElement warningMessage = driver.findElement(By.id("warning-message"));

        firstName.sendKeys("Test first name");
        lastName.sendKeys("Test last name");
        username.sendKeys("username150");
        phoneNumber.sendKeys("123456789");
        emailField.sendKeys("alex@example.com");
        passwordField.sendKeys("Superpassword.123");
        repeatPasswordField.sendKeys("Superpassword.123");
        registerBtn.click();

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
     * Tests if the 'Register' form allows users to create accounts only if the 'password' field has the same content
     * as the 'repeat password' field.
     * If this is not the case, a warning message appears.
     */
    @Test
    @Order(3)
    public void testRegisterFormNotMatchingPasswords() {
        WebElement firstName = driver.findElement(By.id("firstname"));
        WebElement lastName = driver.findElement(By.id("lastname"));
        WebElement username = driver.findElement(By.id("username"));
        WebElement phoneNumber = driver.findElement(By.id("phoneNumber"));
        WebElement emailField = driver.findElement(By.id("floatingInput"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement repeatPasswordField = driver.findElement(By.id("repeat-password"));
        WebElement registerBtn = driver.findElement(By.id("register-btn"));
        WebElement warningMessage = driver.findElement(By.id("warning-message"));

        firstName.sendKeys("Test first name");
        lastName.sendKeys("Test last name");
        username.sendKeys("username105");
        phoneNumber.sendKeys("123456789");
        emailField.sendKeys("alex@example.com");
        passwordField.sendKeys("Superpassword.123");
        repeatPasswordField.sendKeys("NotMatchingPassword");
        registerBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(warningMessage));

        assertTrue(warningMessage.isDisplayed());

        if(warningMessage.isDisplayed()) {
            System.out.println("Warning message is displayed, its content is: " + warningMessage.getText());
        } else {
            System.out.println("Warning message is not displayed");
        }
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
