package org.graphwalker.example;

import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.script.Context;
import org.graphwalker.core.generators.support.AStarPath;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.example.models.ShoppingCart;
import org.openqa.selenium.By;

@GraphWalker({
        @Execute(group = "shortest"
                , pathGenerator = AStarPath.class
                , stopCondition = EdgeCoverage.class
                , stopConditionValue = "100"),

        @Execute(group = "random"
                , pathGenerator = RandomPath.class
                , stopCondition = EdgeCoverage.class
                , stopConditionValue = "100")
})
public class Amazon extends AbstractTest implements ShoppingCart {

    @Override
    public void e_ShoppingCart(Context context) {
        getDriver().findElement(By.id("nav-cart")).click();
    }

    @Override
    public void v_BrowserStarted(Context context) {
        Assert.assertNotNull(getDriver());
    }

    @Override
    public void v_OtherBoughtBooks(Context context) {
        Assert.assertTrue(verifyTextPresent(By.id("hlb-upsell"), "Customers Who Bought "));
    }

    @Override
    public void e_EnterBaseURL(Context context) {
        getDriver().get("http://www.amazon.com");
    }

    @Override
    public void e_AddBookToCart(Context context) {
        getDriver().findElement(By.id("bb_atc_button")).click();
    }

    @Override
    public void e_SearchBook(Context context) {
        getDriver().findElement(By.id("twotabsearchtextbox")).clear();
        getDriver().findElement(By.id("twotabsearchtextbox")).sendKeys("Model-based testing");
        getDriver().findElement(By.id("twotabsearchtextbox")).submit();
    }

    @Override
    public void e_StartBrowser(Context context) {
        getDriver();
    }

    @Override
    public void v_BookInformation(Context context) {
    }

    @Override
    public void v_ShoppingCart(Context context) {
        Assert.assertTrue(verifyTitle("^Amazon\\.com Shopping Cart.*"));
        Integer expected_num_of_books = (Integer)context.getAttribute("num_of_books");
        if (expected_num_of_books.equals(0)) {
            Assert.assertTrue(verifyTextPresent(By.id("cart-active-items"), "Your Shopping Cart is empty"));
        } else {
            Integer actual_num_of_books = Integer.parseInt(getDriver().findElement(By.id("nav-cart-count")).getText());
            Assert.assertEquals(expected_num_of_books, actual_num_of_books);
        }
    }

    @Override
    public void v_SearchResult(Context context) {
        Assert.assertNotNull(getDriver().findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")));
    }

    @Override
    public void v_BaseURL(Context context) {
        Assert.assertTrue(verifyTitle("^Amazon\\.com:.*$"));
        Integer num_of_books = Integer.parseInt(getDriver().findElement(By.id("nav-cart-count")).getText());
        if (!num_of_books.equals(context.getAttribute("num_of_books"))) {
            context.setAttribute("num_of_books", num_of_books);
        }
    }

    @Override
    public void e_ClickBook(Context context) {
        getDriver().findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")).click();
    }

    @Override
    public void v_ClearShoppingCart(Context context) {
        Assert.assertEquals(Integer.parseInt(getDriver().findElement(By.id("nav-cart-count")).getText()), 0);
    }

    @Override
    public void e_ClearShoppingCart(Context context) {
        getDriver().findElement(By.id("nav-cart")).click();
        getDriver().findElement(By.linkText("Delete")).click();
    }

}