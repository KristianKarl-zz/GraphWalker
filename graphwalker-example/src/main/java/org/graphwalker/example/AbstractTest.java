package org.graphwalker.example;

import org.graphwalker.core.annotations.AfterExecution;
import org.graphwalker.core.annotations.ExceptionHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTest {

    private final static int TIMEOUT = 5;

    private WebDriver driver = null;

    protected WebDriver getDriver() {
        if (null == driver) {
            if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
                driver = new SafariDriver();
            } else {
                driver = new FirefoxDriver();
            }
            driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        }
        return driver;
    }

    protected boolean verifyTextPresent(final By locator, final String text) {
        return new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.textToBePresentInElement(locator, text));
    }

    protected boolean verifyTitle(final String regexp) {
        return new WebDriverWait(driver, TIMEOUT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getTitle().matches(regexp);
            }
        });
    }

    @ExceptionHandler
    @AfterExecution
    public void tearDown() {
        if (null != driver) {
            driver.quit();
        }
    }

}
