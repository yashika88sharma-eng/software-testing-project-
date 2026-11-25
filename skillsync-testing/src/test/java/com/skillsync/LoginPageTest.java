
package com.skillsync;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginPageTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
            driver,
            Duration.ofSeconds(15)
        );

        driver.get("http://localhost:5173/login");
    }

    // Test 1: Verify Login Page Opens
    @Test
    public void verifyLoginPageTitle() {

        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("login-email")
            )
        );

        String pageTitle = driver.getTitle();

        Assert.assertFalse(
            pageTitle.isEmpty(),
            "Page title should not be empty"
        );
    }

    // Test 2: Verify Email and Password Fields
    @Test
    public void verifyLoginFields() {

        WebElement email = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("login-email")
            )
        );

        WebElement password = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("login-password")
            )
        );

        Assert.assertTrue(
            email.isDisplayed(),
            "Email field should be visible"
        );

        Assert.assertTrue(
            password.isDisplayed(),
            "Password field should be visible"
        );
    }

    // Test 3: Verify Show and Hide Password
    @Test
    public void verifyShowHidePassword() {

        WebElement password = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("login-password")
            )
        );

        WebElement toggleButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(
                    "button[aria-label='Show password']"
                )
            )
        );

        // Initially password should be hidden
        Assert.assertEquals(
            password.getAttribute("type"),
            "password"
        );

        // Click Show Password
        toggleButton.click();

        wait.until(
            ExpectedConditions.attributeToBe(
                By.id("login-password"),
                "type",
                "text"
            )
        );

        password = driver.findElement(
            By.id("login-password")
        );

        Assert.assertEquals(
            password.getAttribute("type"),
            "text",
            "Password should become visible"
        );

        // Click Hide Password
        toggleButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(
                    "button[aria-label='Hide password']"
                )
            )
        );

        toggleButton.click();

        wait.until(
            ExpectedConditions.attributeToBe(
                By.id("login-password"),
                "type",
                "password"
            )
        );

        password = driver.findElement(
            By.id("login-password")
        );

        Assert.assertEquals(
            password.getAttribute("type"),
            "password",
            "Password should become hidden again"
        );
    }

    // Close Browser After Every Test
    @AfterMethod
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}

