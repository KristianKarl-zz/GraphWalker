package org.graphwalker.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTest {

    private WebDriver driver = null;

    protected WebDriver getDriver() {
        if (null == driver) {
            if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
                driver = new SafariDriver();
            } else {
                driver = new FirefoxDriver();
            }
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        }
        return driver;
    }

    public boolean verifyTextPresent(String text) {
        return driver.findElements(By.xpath("//*[contains(text(),\"" + text + "\")]")).size() > 0;
    }
}
