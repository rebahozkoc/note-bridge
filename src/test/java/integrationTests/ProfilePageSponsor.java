package integrationTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ut.twente.notebridge.utils.Utils;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is used to test the profile page for sponsors.
 */
public class ProfilePageSponsor {
    private static WebDriver driver;

    /**
     * This method is used to set up the tests before all tests.
     */
    @BeforeClass
    public static void setUpAndLogIn() {
        try {
            System.setProperty("webdriver.chrome.driver", Utils.readFromProperties("SELENIUM_DRIVER_PATH"));
            driver = new ChromeDriver();
            driver.get("http://localhost:8080/notebridge/login.html");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));
            WebElement pass = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));
            WebElement login = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#login-form button[type='submit']")));

            email.sendKeys("googlegoogle@asd.com");
            pass.sendKeys("Testtest123.");
            login.click();

        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Tests if the username is displayed correctly on the profile page.
     */
    @Test
    @Order(1)
    public void accessProfilePage(){

        driver.get("http://localhost:8080/notebridge/profile.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));


        WebElement profileUsername = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#username")));

        //If user does not log in successfully, then the profile page prints out default username
        //Being not equal to this, means the user has logged in successfully
        assertTrue(profileUsername.getText()!="@username");


    }

    /**
     * Tests if the user can edit their name.
     */
    @Test
    @Order(2)
    public void tryEditingName(){

        driver.get("http://localhost:8080/notebridge/profile.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));


        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".profile-header h2 button")));
        editBtn.click();

        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#nameInput")));
        WebElement lastnameInput=wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#lastnameInput")));
        WebElement saveChangesBtn=wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#editHeaderModal .modal-footer button")));


// Clear and interact with nameInput
        nameInput.clear();
        nameInput.clear();
        lastnameInput.clear();
        nameInput.sendKeys("CHANGED");
        lastnameInput.sendKeys("Changed");
        saveChangesBtn.click();
        //alert showing up saying update successfull
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText=alert.getText();
        alert.accept();
        assertTrue(alertText.contains("Update successful"));


    }


    /**
     * This method is used to tear down the tests after all tests.
     */
    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
