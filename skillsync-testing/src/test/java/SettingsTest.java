
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Map;

public class SettingsTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");

        options.setExperimentalOption(
                "prefs",
                Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false
                )
        );

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(25)
        );

        driver.manage().window().maximize();

        // Open Login Page
        driver.get("http://localhost:5173/login");

        String email = System.getenv("SKILLSYNC_TEST_EMAIL");
        String password = System.getenv("SKILLSYNC_TEST_PASSWORD");

        Assert.assertNotNull(email, "Email is missing.");
        Assert.assertNotNull(password, "Password is missing.");

        // Enter Email
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("login-email")
                )
        ).sendKeys(email.trim());

        // Enter Password
        driver.findElement(
                By.id("login-password")
        ).sendKeys(password);

        // Login
        driver.findElement(
                By.cssSelector("[data-testid='login-submit']")
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".dashboard-page")
                )
        );

        // Open Settings
        driver.get("http://localhost:5173/settings");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".settings-page")
                )
        );
    }

    // TEST 1: Settings Page Loads
    @Test
    public void verifySettingsPageLoads() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(".settings-page")
                ).isDisplayed()
        );

        Assert.assertTrue(
                driver.getPageSource().contains("Settings")
        );

        System.out.println("PASS: Settings page loads.");
    }

    // TEST 2: Profile Fields Visible
    @Test
    public void verifyProfileFieldsVisible() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(
                                ".profile-right input[type='email']"
                        )
                ).isDisplayed()
        );

        Assert.assertTrue(
                driver.findElements(
                        By.cssSelector(".profile-right input")
                ).size() >= 4
        );

        System.out.println("PASS: Profile fields are visible.");
    }

    // TEST 3: Account Statistics Visible
    @Test
    public void verifyAccountStatsVisible() {

        WebElement stats = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                ".settings-card .stats-grid"
                        )
                )
        );

        Assert.assertTrue(stats.isDisplayed());

        Assert.assertTrue(
                stats.getText().contains("Skills")
        );

        Assert.assertTrue(
                stats.getText().contains("Goals")
        );

        Assert.assertTrue(
                stats.getText().contains("Notes")
        );

        System.out.println("PASS: Account statistics are visible.");
    }

    // TEST 4: Notification Options Visible
    @Test
    public void verifyNotificationOptionsVisible() {

        Assert.assertEquals(
                driver.findElements(
                        By.cssSelector(
                                ".notification-item input[type='checkbox']"
                        )
                ).size(),
                3,
                "Three notification options should be visible."
        );

        System.out.println("PASS: Notification options are visible.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
