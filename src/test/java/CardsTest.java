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
