
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

public class SignupPageTest {

    WebDriver driver;
    WebDriverWait wait;

    By nameField = By.id("signup-name");
    By emailField = By.id("signup-email");
    By passwordField = By.id("signup-password");
    By confirmPasswordField = By.id("signup-confirm-password");

    By submitButton =
        By.cssSelector("[data-testid='signup-submit']");

    By errorMessage =
        By.cssSelector("[data-testid='signup-error']");

    By passwordToggle =
        By.cssSelector("[data-testid='toggle-signup-password']");

    By confirmPasswordToggle =
        By.cssSelector("[data-testid='toggle-confirm-password']");

    @BeforeMethod
    public void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("http://localhost:5173/signup");

        wait.until(
            ExpectedConditions.visibilityOfElementLocated(nameField)
        );
    }

    // Test 1: Verify signup page loads
    @Test
    public void verifySignupPageLoads() {

        Assert.assertTrue(
            driver.findElement(nameField).isDisplayed()
        );

        Assert.assertTrue(
            driver.getPageSource().contains("Create Account")
        );
    }

    // Test 2: Verify all signup fields
    @Test
    public void verifySignupFields() {

        Assert.assertTrue(driver.findElement(nameField).isDisplayed());
        Assert.assertTrue(driver.findElement(emailField).isDisplayed());
        Assert.assertTrue(driver.findElement(passwordField).isDisplayed());
        Assert.assertTrue(driver.findElement(confirmPasswordField).isDisplayed());
    }

    // Test 3: Verify empty name
    @Test
    public void verifyEmptyName() {

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Please enter your full name."
        );
    }

    // Test 4: Verify short name
    @Test
    public void verifyShortName() {

        driver.findElement(nameField).sendKeys("A");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Name must contain at least 2 characters."
        );
    }

    // Test 5: Verify empty email
    @Test
    public void verifyEmptyEmail() {

        fillName();

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Please enter your email address."
        );
    }

    // Test 6: Verify invalid email
    @Test
    public void verifyInvalidEmail() {

        fillName();

        driver.findElement(emailField).sendKeys("invalidemail");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Please enter a valid email address."
        );
    }

    // Test 7: Verify empty password
    @Test
    public void verifyEmptyPassword() {

        fillNameAndEmail();

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Please enter a password."
        );
    }

    // Test 8: Verify short password
    @Test
    public void verifyShortPassword() {

        fillNameAndEmail();

        driver.findElement(passwordField).sendKeys("abc123");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Password must be at least 8 characters long."
        );
    }

    // Test 9: Verify password letter and number requirements
    @Test
    public void verifyPasswordRequirements() {

        fillNameAndEmail();

        driver.findElement(passwordField).sendKeys("abcdefgh");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Password must contain at least one letter and one number."
        );
    }

    // Test 10: Verify empty confirm password
    @Test
    public void verifyEmptyConfirmPassword() {

        fillNameAndEmail();

        driver.findElement(passwordField).sendKeys("Password123");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Please confirm your password."
        );
    }

    // Test 11: Verify password mismatch
    @Test
    public void verifyPasswordMismatch() {

        fillNameAndEmail();

        driver.findElement(passwordField).sendKeys("Password123");

        driver.findElement(confirmPasswordField).sendKeys("Password456");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Passwords do not match."
        );
    }

    // Test 12: Verify terms and conditions
    @Test
    public void verifyTermsValidation() {

        fillNameAndEmail();

        driver.findElement(passwordField).sendKeys("Password123");

        driver.findElement(confirmPasswordField).sendKeys("Password123");

        driver.findElement(submitButton).click();

        Assert.assertEquals(
            getErrorMessage(),
            "Please accept the terms and conditions."
        );
    }

    // Test 13: Verify show/hide password
    @Test
    public void verifyShowHidePassword() {

        WebElement password = driver.findElement(passwordField);

        Assert.assertEquals(
            password.getAttribute("type"),
            "password"
        );

        driver.findElement(passwordToggle).click();

        wait.until(
            ExpectedConditions.attributeToBe(
                passwordField, "type", "text"
            )
        );

        Assert.assertEquals(
            driver.findElement(passwordField).getAttribute("type"),
            "text"
        );

        driver.findElement(passwordToggle).click();

        wait.until(
            ExpectedConditions.attributeToBe(
                passwordField, "type", "password"
            )
        );

        Assert.assertEquals(
            driver.findElement(passwordField).getAttribute("type"),
            "password"
        );
    }

    // Test 14: Verify confirm password visibility
    @Test
    public void verifyConfirmPasswordVisibility() {

        Assert.assertEquals(
            driver.findElement(confirmPasswordField).getAttribute("type"),
            "password"
        );

        driver.findElement(confirmPasswordToggle).click();

        wait.until(
            ExpectedConditions.attributeToBe(
                confirmPasswordField, "type", "text"
            )
        );

        Assert.assertEquals(
            driver.findElement(confirmPasswordField).getAttribute("type"),
            "text"
        );

        driver.findElement(confirmPasswordToggle).click();

        wait.until(
            ExpectedConditions.attributeToBe(
                confirmPasswordField, "type", "password"
            )
        );

        Assert.assertEquals(
            driver.findElement(confirmPasswordField).getAttribute("type"),
            "password"
        );
    }

    // Helper method: Fill name
    public void fillName() {

        driver.findElement(nameField).sendKeys("Test User");
    }

    // Helper method: Fill name and email
    public void fillNameAndEmail() {

        fillName();

        driver.findElement(emailField).sendKeys("test@example.com");
    }

    // Helper method: Get validation error
    public String getErrorMessage() {

        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(errorMessage)
        );

        return error.getText();
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}

