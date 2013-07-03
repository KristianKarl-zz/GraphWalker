package org.graphwalker.example;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.concurrent.TimeUnit;

public class CreateJobs {

    @Test
    public void create() {
        WebDriver driver = new SafariDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("http://localhost:8080");
        for (int i = 1; i <= 10; i++) {
            driver.findElement(By.linkText("New Job")).click();
            driver.findElement(By.id("name")).sendKeys("Example "+i);
            driver.findElement(By.id("copy")).click();
            driver.findElement(By.id("from")).sendKeys("Example x");
            driver.findElement(By.id("ok-button")).click();
            driver.findElement(By.name("config")).submit();
            driver.findElement(By.linkText("Back to Dashboard")).click();
        }
    }
}
