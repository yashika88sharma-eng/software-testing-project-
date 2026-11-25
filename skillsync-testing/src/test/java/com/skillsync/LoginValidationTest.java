
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

public class LoginValidationTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://localhost:5173/login");
    }

    // Test 1: Empty email validation
    @Test
    public void verifyEmptyEmail() {

        driver.findElement(
            By.cssSelector("[data-testid='login-submit']")
        ).click();

        WebElement message = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='login-message']")
            )
        );

        Assert.assertEquals(
            message.getText(),
            "Please enter your email address."
        );
    }

    // Test 2: Invalid email validation
    @Test
    public void verifyInvalidEmail() {

        driver.findElement(By.id("login-email"))
              .sendKeys("invalidemail");

        driver.findElement(
            By.cssSelector("[data-testid='login-submit']")
        ).click();

        WebElement message = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='login-message']")
            )
        );

        Assert.assertEquals(
            message.getText(),
            "Please enter a valid email address."
        );
    }

    // Test 3: Empty password validation
    @Test
    public void verifyEmptyPassword() {

        driver.findElement(By.id("login-email"))
              .sendKeys("test@example.com");

        driver.findElement(
            By.cssSelector("[data-testid='login-submit']")
        ).click();

        WebElement message = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='login-message']")
            )
        );

        Assert.assertEquals(
            message.getText(),
            "Please enter your password."
        );
    }

    // Test 4: Incorrect login credentials
    @Test
    public void verifyIncorrectCredentials() {

        driver.findElement(By.id("login-email"))
              .sendKeys("invaliduser@example.com");

        driver.findElement(By.id("login-password"))
              .sendKeys("WrongPassword123!");

        driver.findElement(
            By.cssSelector("[data-testid='login-submit']")
        ).click();

        WebElement message = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='login-message']")
            )
        );

        String actualMessage = message.getText();

        Assert.assertTrue(
            actualMessage.contains("Incorrect email or password")
            || actualMessage.contains("No account found"),
            "Unexpected login message: " + actualMessage
        );
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
