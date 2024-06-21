import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ut.twente.notebridge.utils.Utils;

import static org.junit.Assert.assertEquals;

public class SeleniumTest {
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

    @Test
    @Order(1)
    public void testTitle() {
        String expectedTitle = "Note-Bridge";
        String actualTitle = driver.getTitle();
        assertEquals(expectedTitle, actualTitle);
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
