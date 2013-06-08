package org.graphwalker.example;

import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.conditions.support.Length;
import org.graphwalker.core.generators.support.AStarPath;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.example.models.ShoppingCart;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@GraphWalker({
        @Execute(group = "shortest"
                , pathGenerator = AStarPath.class
                , stopCondition = EdgeCoverage.class
                , stopConditionValue = "100"),

        @Execute(group = "random"
                , pathGenerator = RandomPath.class
                , stopCondition = Length.class
                , stopConditionValue = "20")
})
public class Amazon extends AbstractTest implements ShoppingCart {

    @Override
    public void e_ShoppingCart(ExecutionContext executionContext) {
        try {
            getDriver().findElement(By.cssSelector("#navCartEmpty > a.destination > span.text")).click();
        } catch (NoSuchElementException e) {
            try {
                getDriver().findElement(By.cssSelector("a.destination.count")).click();
            } catch (NoSuchElementException e1) {
                getDriver().findElement(By.xpath("//*[@id='nav-cart-count']")).click();
            }
        }
    }

    @Override
    public void v_BrowserStarted(ExecutionContext executionContext) {
        Assert.assertNotNull(getDriver());
    }

    @Override
    public void v_OtherBoughtBooks(ExecutionContext executionContext) {
        Assert.assertTrue(verifyTextPresent("Customers Who Bought "));
    }

    @Override
    public void e_EnterBaseURL(ExecutionContext executionContext) {
        getDriver().get("http://www.amazon.com");
    }

    @Override
    public void e_AddBookToCart(ExecutionContext executionContext) {
        getDriver().findElement(By.id("bb_atc_button")).click();
    }

    @Override
    public void e_SearchBook(ExecutionContext executionContext) {
        WebElement element = getDriver().findElement(By.id("twotabsearchtextbox"));
        element.clear();
        element.sendKeys("Model-based testing");
        try {
            getDriver().findElement(By.xpath("//*[@id='navGoButton']/input")).click();
        } catch (NoSuchElementException e) {
            getDriver().findElement(By.xpath("//*[@class='nav-submit-button nav-sprite']/input")).click();
        }
    }

    @Override
    public void e_StartBrowser(ExecutionContext executionContext) {
        getDriver();
    }

    @Override
    public void v_BookInformation(ExecutionContext executionContext) {
    }

    @Override
    public void v_ShoppingCart(ExecutionContext executionContext) {
        Assert.assertTrue("^Amazon\\.com Shopping Cart.*".matches(getDriver().getTitle()));
        Integer expected_num_of_books = 0; //Integer.valueOf(getMbt().getDataValue("num_of_books"));
        Integer actual_num_of_books = null;

        if (0 == expected_num_of_books) {
            Assert.assertTrue(verifyTextPresent("Your Shopping Cart is empty"));
            return;
        }

        String itemsInCart = getDriver().findElement(By.id("gutterCartViewForm")).getText();
        Pattern pattern = Pattern.compile("Subtotal \\(([0-9]+) items*\\):", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(itemsInCart);
        if (matcher.find()) {
            actual_num_of_books = Integer.valueOf(matcher.group(1));
        }
        Assert.assertEquals(expected_num_of_books, actual_num_of_books);
    }

    @Override
    public void v_SearchResult(ExecutionContext executionContext) {
        Assert.assertNotNull(getDriver().findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")));
    }

    @Override
    public void v_BaseURL(ExecutionContext executionContext) {
        Assert.assertTrue(getDriver().getTitle().matches("^Amazon\\.com:.*$"));
    }

    @Override
    public void e_ClickBook(ExecutionContext executionContext) {
        getDriver().findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")).click();
    }

}