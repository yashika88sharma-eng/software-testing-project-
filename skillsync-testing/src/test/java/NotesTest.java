
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

public class NotesTest {

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

        // Enter Credentials
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("login-email")
                )
        ).sendKeys(email.trim());

        driver.findElement(
                By.id("login-password")
        ).sendKeys(password);

        // Login
        driver.findElement(
                By.cssSelector("[data-testid='login-submit']")
        ).click();

        // Wait for Dashboard
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".dashboard-page")
                )
        );

        // Open Notes Page
        driver.get("http://localhost:5173/notes");

        wait.until(
        ExpectedConditions.attributeToBe(
                By.cssSelector("[data-testid='notes-page']"),
                "data-loaded",
                "true"
        )
);
    }

    // TEST 1: Notes Page Loads
    @Test
    public void verifyNotesPageLoads() {

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(".notes-page")
                ).isDisplayed()
        );

        Assert.assertTrue(
                driver.getPageSource().contains("My Notes")
        );

        System.out.println("PASS: Notes page loads.");
    }

    // TEST 2: Add Note
    @Test
    public void verifyAddNote() {

        String noteTitle = "Selenium Test Note";

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                ".notes-form input"
                        )
                )
        );

        title.sendKeys(noteTitle);

        driver.findElement(
                By.cssSelector(".notes-form textarea")
        ).sendKeys("This is an automated testing note.");

        driver.findElement(
                By.cssSelector(".add-notes")
        ).click();

        WebElement note = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'note-card')]//h3[text()='"
                                        + noteTitle + "']"
                        )
                )
        );

        Assert.assertTrue(note.isDisplayed());

        System.out.println("PASS: Note added successfully.");
    }

    // TEST 3: Edit Note
    @Test
    public void verifyEditNote() {

        String noteTitle = "Edit Test Note";

        WebElement title = driver.findElement(
                By.cssSelector(".notes-form input")
        );

        title.sendKeys(noteTitle);

        driver.findElement(
                By.cssSelector(".notes-form textarea")
        ).sendKeys("Original description");

        driver.findElement(
                By.cssSelector(".add-notes")
        ).click();

        WebElement noteCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'note-card')][.//h3[text()='"
                                        + noteTitle + "']]"
                        )
                )
        );

        noteCard.findElement(
                By.cssSelector(".edit-btn")
        ).click();

        WebElement editTitle = driver.findElement(
                By.cssSelector(".notes-form input")
        );

        editTitle.clear();
        editTitle.sendKeys("Updated Test Note");

        WebElement editDescription = driver.findElement(
                By.cssSelector(".notes-form textarea")
        );

        editDescription.clear();
        editDescription.sendKeys("Updated description");

        driver.findElement(
                By.cssSelector(".add-notes")
        ).click();

        WebElement updatedNote = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'note-card')]//h3[text()='Updated Test Note']"
                        )
                )
        );

        Assert.assertTrue(updatedNote.isDisplayed());

        System.out.println("PASS: Note edited successfully.");
    }

    // TEST 4: Delete Note
    @Test
    public void verifyDeleteNote() {

        String noteTitle = "Delete Test Note";

        WebElement title = driver.findElement(
                By.cssSelector(".notes-form input")
        );

        title.sendKeys(noteTitle);

        driver.findElement(
                By.cssSelector(".notes-form textarea")
        ).sendKeys("Note for deletion testing.");

        driver.findElement(
                By.cssSelector(".add-notes")
        ).click();

        WebElement noteCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'note-card')][.//h3[text()='"
                                        + noteTitle + "']]"
                        )
                )
        );

        noteCard.findElement(
                By.cssSelector(".delete-btn")
        ).click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'note-card')]//h3[text()='"
                                        + noteTitle + "']"
                        )
                )
        );

        System.out.println("PASS: Note deleted successfully.");
    }

    // TEST 5: Empty Note Validation
    @Test
    public void verifyEmptyNoteValidation() {

        int initialNotes = driver.findElements(
                By.cssSelector(".note-card")
        ).size();

        driver.findElement(
                By.cssSelector(".add-notes")
        ).click();

        int finalNotes = driver.findElements(
                By.cssSelector(".note-card")
        ).size();

        Assert.assertEquals(
                finalNotes,
                initialNotes,
                "Empty note should not be added."
        );

        System.out.println("PASS: Empty note validation works.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
