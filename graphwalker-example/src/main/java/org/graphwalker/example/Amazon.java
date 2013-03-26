package org.graphwalker.example;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphwalker.core.model.support.ModelContext;
import org.graphwalker.core.utils.Assert;
import org.graphwalker.java.annotations.AfterModel;
import org.graphwalker.java.annotations.BeforeModel;
import org.graphwalker.java.annotations.ExceptionHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Amazon {

    private WebDriver driver = null;

    @BeforeModel
    public void createBrowser() {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    @AfterModel
    @ExceptionHandler
    public void closeBrowser() {
        if (null != driver) {
            driver.close();
        }
    }

    /**
     * This method implements the Edge 'e_AddBookToCart'
     */
    public void e_AddBookToCart() {
        driver.findElement(By.id("bb_atc_button")).click();
    }

    /**
     * This method implements the Edge 'e_ClickBook'
     */
    public void e_ClickBook() {
        driver.findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")).click();
    }

    /**
     * This method implements the Edge 'e_EnterBaseURL'
     */
    public void e_EnterBaseURL() {
        driver.get("http://www.amazon.com");
    }

    /**
     * This method implements the Edge 'e_SearchBook'
     */
    public void e_SearchBook() {
        WebElement element;
        element = driver.findElement(By.id("twotabsearchtextbox"));
        element.clear();
        element.sendKeys("Model-based testing");
        try {
            driver.findElement(By.xpath("//*[@id='navGoButton']/input")).click();
        } catch (NoSuchElementException e) {
            driver.findElement(By.xpath("//*[@class='nav-submit-button nav-sprite']/input")).click();
        }
    }

    /**
     * This method implements the Edge 'e_ShoppingCart'
     */
    public void e_ShoppingCart() {
        driver.findElement(By.id("nav-cart")).click();
    }

    /**
     * This method implements the Edge 'e_StartBrowser'
     */
    public void e_StartBrowser() {
        // the browser creation is moved
    }

    /**
     * This method implements the Vertex 'v_BaseURL'
     */
    public void v_BaseURL() {
        Assert.assertTrue(driver.getTitle().matches("^Amazon\\.com: .*"));
    }

    /**
     * This method implements the Vertex 'v_BookInformation'
     */
    public void v_BookInformation() {}

    /**
     * This method implements the Vertex 'v_BrowserStarted'
     */
    public void v_BrowserStarted() {
        Assert.assertNotNull(driver);
    }

    /**
     * This method implements the Vertex 'v_OtherBoughtBooks'
     */
    public void v_OtherBoughtBooks() {
        Assert.assertTrue(verifyTextPresent("Customers Who Bought "));
    }

    /**
     * This method implements the Vertex 'v_SearchResult'
     */
    public void v_SearchResult() {
        Assert.assertTrue(driver.findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")) != null);
    }

    /**
     * This method implements the Vertex 'v_ShoppingCart'
     */
    public void v_ShoppingCart(ModelContext context) {
        Assert.assertTrue(driver.getTitle().matches("^Amazon\\.com Shopping Cart.*"));
        Integer expected_num_of_books = context.getEdgeFilter().getDataValue("num_of_books", Double.class).intValue();
        Integer actual_num_of_books = null;

        if (expected_num_of_books == 0) {
            Assert.assertTrue(verifyTextPresent("Your Shopping Cart is empty"));
            return;
        }

        String itemsInCart = driver.findElement(By.id("gutterCartViewForm")).getText();
        Pattern pattern = Pattern.compile("Subtotal \\(([0-9]+) items*\\):", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(itemsInCart);
        if (matcher.find()) {
            actual_num_of_books = Integer.valueOf(matcher.group(1));
        }
        Assert.assertEquals(expected_num_of_books, actual_num_of_books);
    }

    /**
     * @param text The text to verify
     * @return true if the test is present on the web page
     */
    public boolean verifyTextPresent(String text) {
        return driver.findElements(By.xpath("//*[contains(text(),\"" + text + "\")]")).size() > 0;
    }
}
