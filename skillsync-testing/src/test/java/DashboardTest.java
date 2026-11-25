
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Map;

public class DashboardTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {

        // Disable Chrome Password Manager popups
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

        // Read Test Credentials
        String email = System.getenv("SKILLSYNC_TEST_EMAIL");
        String password = System.getenv("SKILLSYNC_TEST_PASSWORD");

        Assert.assertNotNull(email, "Test email is missing.");
        Assert.assertNotNull(password, "Test password is missing.");

        Assert.assertFalse(email.isBlank(), "Email is empty.");
        Assert.assertFalse(password.isBlank(), "Password is empty.");

        // Enter Email
        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("login-email")
                )
        );

        emailField.clear();
        emailField.sendKeys(email.trim());

        // Enter Password
        WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("login-password")
                )
        );

        passwordField.clear();
        passwordField.sendKeys(password);

        // Click Login
        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-testid='login-submit']")
                )
        );

        loginButton.click();

        // Wait for Dashboard or Login Error
        try {

            wait.until(ExpectedConditions.or(

                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".dashboard-page")
                    ),

                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("[data-testid='login-message']")
                    )
            ));

            // Check Login Error
            if (!driver.findElements(
                    By.cssSelector("[data-testid='login-message']")
            ).isEmpty()) {

                WebElement message = driver.findElement(
                        By.cssSelector("[data-testid='login-message']")
                );

                if (message.isDisplayed()
                        && !message.getText().isBlank()
                        && message.getAttribute("class").contains("error")) {

                    Assert.fail(
                            "Login failed: " + message.getText()
                    );
                }
            }

            // Confirm Dashboard
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".dashboard-page")
                    )
            );

            System.out.println("Login successful!");
            System.out.println("Dashboard opened successfully!");
            System.out.println("Current URL: " + driver.getCurrentUrl());

        } catch (TimeoutException e) {

            System.out.println("========== LOGIN DEBUG ==========");

            System.out.println("Current URL: " + driver.getCurrentUrl());

            System.out.println(
                    driver.findElement(By.tagName("body")).getText()
            );

            System.out.println("Dashboard was not opened.");

            System.out.println("=================================");

            throw e;
        }
    }

    // TEST 1: Dashboard Loads
    @Test
    public void verifyDashboardLoads() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(".dashboard-page")
                ).isDisplayed(),
                "Dashboard is not visible."
        );

        System.out.println("PASS: Dashboard loads.");
    }

    // TEST 2: Stats Cards
    @Test
    public void verifyStatsCardsVisible() {

        Assert.assertTrue(
                driver.findElements(
                        By.cssSelector(".stat-card")
                ).size() > 0,
                "Stats cards are not visible."
        );

        System.out.println("PASS: Stats cards are visible.");
    }

    // TEST 3: Weekly Goals
    @Test
    public void verifyWeeklyGoalsSectionVisible() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(".weekly-goals")
                ).isDisplayed(),
                "Weekly Goals section is not visible."
        );

        System.out.println("PASS: Weekly Goals section is visible.");
    }

    // TEST 4: Upcoming Tasks
    @Test
    public void verifyUpcomingTasksSectionVisible() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector("[data-testid='task-input']")
                ).isDisplayed(),
                "Upcoming Tasks section is not visible."
        );

        System.out.println("PASS: Upcoming Tasks section is visible.");
    }

    // TEST 5: Roadmap Section
    @Test
    public void verifyRoadmapSectionVisible() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(".roadmap-card")
                ).isDisplayed(),
                "Roadmap section is not visible."
        );

        System.out.println("PASS: Roadmap section is visible.");
    }

    // TEST 6: Progress Section
    @Test
    public void verifyProgressSectionVisible() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(".progress-card")
                ).isDisplayed(),
                "Progress section is not visible."
        );

        System.out.println("PASS: Progress section is visible.");
    }

    // TEST 7: Generate Roadmap
    @Test
    public void verifyGenerateRoadmapButton() {

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "[data-testid='generate-roadmap']"
                        )
                )
        );

        button.click();

        WebElement phase = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "[data-testid='roadmap-phase']"
                        )
                )
        );

        Assert.assertTrue(
                phase.isDisplayed(),
                "Roadmap phases were not generated."
        );

        System.out.println(
                "PASS: Roadmap generated successfully."
        );
    }

    // CLOSE BROWSER
    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
