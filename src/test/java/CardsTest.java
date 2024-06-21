import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ut.twente.notebridge.utils.Utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CardsTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        try {
            System.setProperty("webdriver.chrome.driver", Utils.readFromProperties("SELENIUM_WEB_DRIVER"));
            driver = new ChromeDriver();
        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    @Order(1)
    public void InterestedTest() throws InterruptedException {
        driver.get("http://localhost:8080/notebridge/");
        driver.get("http://localhost:8080/notebridge/card-details.html?id=180");

        WebElement button = driver.findElement(By.id("interested-button"));
        button.click();

        TimeUnit.SECONDS.sleep(2);
        assertEquals("http://localhost:8080/notebridge/login.html", driver.getCurrentUrl());
        TimeUnit.SECONDS.sleep(2);
    }



    @Test
    public void openCardTest() throws InterruptedException {
        driver.get("http://localhost:8080/notebridge/cards.html");
        TimeUnit.SECONDS.sleep(3);

        WebElement card = driver.findElement(By.id("displayed-card"));
        new Actions(driver).moveToElement(card).click().perform();
        TimeUnit.SECONDS.sleep(3);

        String cardurl = driver.getCurrentUrl().split("=")[0];


        assertEquals("http://localhost:8080/notebridge/card-details.html?id", cardurl);
    }


    @Test
    @Order(2)
    public void editCardTest() throws InterruptedException {

        driver.get("http://localhost:8080/notebridge/");
        driver.get("http://localhost:8080/notebridge/login.html");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        wait.until(ExpectedConditions.urlContains("http://localhost:8080/notebridge/login.html"));


        WebElement email = driver.findElement(By.id("email"));
        WebElement pass = driver.findElement(By.id("password"));
        WebElement login = driver.findElement(By.id("login-button"));

        email.sendKeys("a@email.com");
        pass.sendKeys("A12345678!!");
        login.click();

        wait.until(ExpectedConditions.urlContains("http://localhost:8080/notebridge/home.html"));

        driver.get("http://localhost:8080/notebridge/card-details.html?id=145");

        TimeUnit.SECONDS.sleep(5);

        WebElement edit2 = driver.findElement(By.id("edit-button"));
        assertEquals(true, edit2.isDisplayed());

        WebElement delete2 = driver.findElement(By.id("delete-button"));
        assertEquals(true, delete2.isDisplayed());

    }

    @Test
    public void loadCardsTest() throws InterruptedException {
        driver.get("http://localhost:8080/notebridge/cards.html");
        TimeUnit.SECONDS.sleep(3);

        WebElement load = driver.findElement(By.id("load-more-btn"));
        WebElement cardsContainer = driver.findElement(By.id("cards"));
        Dimension len1 = cardsContainer.getSize();

        new Actions(driver).moveToElement(load).click().perform();
        TimeUnit.SECONDS.sleep(5);

        Dimension len2 = cardsContainer.getSize();

        assertTrue(len2.height > len1.height);

    }



    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }



}
