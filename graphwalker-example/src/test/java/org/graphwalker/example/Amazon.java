package org.graphwalker.example;

import org.graphwalker.core.annotation.Execute;
import org.graphwalker.core.annotation.GraphWalker;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.ReachedVertex;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.script.ScriptContext;
import org.graphwalker.example.models.ShoppingCart;
import org.openqa.selenium.By;

@GraphWalker({
        @Execute(pathGenerator = AStarPath.class
                , stopCondition = ReachedVertex.class
                , stopConditionValue = "v_ShoppingCart"),

        @Execute(group = "shortest"
                , pathGenerator = AStarPath.class
                , stopCondition = ReachedVertex.class
                , stopConditionValue = "v_ShoppingCart"
                , language = "groovy"),

        @Execute(group = "random"
                , pathGenerator = RandomPath.class
                , stopCondition = EdgeCoverage.class
                , stopConditionValue = "100")
})
public class Amazon extends AbstractTest implements ShoppingCart {

    @Override
    public void e_ShoppingCart(ScriptContext context) {
        getDriver().findElement(By.id("nav-cart")).click();
    }

    @Override
    public void v_BrowserStarted(ScriptContext context) {
        Assert.assertNotNull(getDriver());
    }

    @Override
    public void v_OtherBoughtBooks(ScriptContext context) {
        Assert.assertTrue(verifyTextPresent(By.id("hlb-upsell"), "Customers Also Bought these Highly Rated Items"));
    }

    @Override
    public void e_EnterBaseURL(ScriptContext context) {
        getDriver().get("http://www.amazon.com");
    }

    @Override
    public void e_AddBookToCart(ScriptContext context) {
        getDriver().findElement(By.id("bb_atc_button")).click();
    }

    @Override
    public void e_SearchBook(ScriptContext context) {
        getDriver().findElement(By.id("twotabsearchtextbox")).clear();
        getDriver().findElement(By.id("twotabsearchtextbox")).sendKeys("Model-based testing");
        getDriver().findElement(By.id("twotabsearchtextbox")).submit();
    }

    @Override
    public void e_StartBrowser(ScriptContext context) {
        getDriver();
    }

    @Override
    public void v_BookInformation(ScriptContext context) {
    }

    @Override
    public void v_ShoppingCart(ScriptContext context) {
        Assert.assertTrue(verifyTitle("^Amazon\\.com Shopping Cart.*"));
        Integer expected_num_of_books = (Integer) context.getAttribute("num_of_books");
        if (expected_num_of_books.equals(0)) {
            Assert.assertTrue(verifyTextPresent(By.id("sc-active-cart"), "Your Shopping Cart is empty"));
        } else {
            Integer actual_num_of_books = Integer.parseInt(getDriver().findElement(By.id("nav-cart-count")).getText());
            Assert.assertEquals(expected_num_of_books, actual_num_of_books);
        }
    }

    @Override
    public void v_SearchResult(ScriptContext context) {
        Assert.assertNotNull(getDriver().findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")));
    }

    @Override
    public void v_BaseURL(ScriptContext context) {
        Assert.assertTrue(verifyTitle("^Amazon\\.com:.*$"));
        Integer num_of_books = Integer.parseInt(getDriver().findElement(By.id("nav-cart-count")).getText());
        if (!num_of_books.equals(context.getAttribute("num_of_books"))) {
            context.setAttribute("num_of_books", num_of_books);
        }
    }

    @Override
    public void e_ClickBook(ScriptContext context) {
        getDriver().findElement(By.linkText("Practical Model-Based Testing: A Tools Approach")).click();
    }

    @Override
    public void v_ClearShoppingCart(ScriptContext context) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
        }
        Assert.assertEquals(Integer.parseInt(getDriver().findElement(By.id("nav-cart-count")).getText()), 0);
    }

    @Override
    public void e_ClearShoppingCart(ScriptContext context) {
        getDriver().findElement(By.id("nav-cart")).click();
        getDriver().findElement(By.linkText("Delete")).click();
    }

}