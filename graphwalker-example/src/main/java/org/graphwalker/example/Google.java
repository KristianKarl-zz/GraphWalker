package org.graphwalker.example;

import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.script.Context;
import org.graphwalker.example.models.google.GoogleStart;
import org.graphwalker.example.models.google.SearchImage;
import org.graphwalker.example.models.google.SearchText;
import org.graphwalker.example.models.google.SearchYoutube;
import org.openqa.selenium.By;

@GraphWalker
public class Google extends AbstractTest implements GoogleStart, SearchText, SearchImage, SearchYoutube {

    @Override
    public void e_SearchImage(Context context) {
        getDriver().findElement(By.id("gb_2")).click();
        getDriver().findElement(By.id("lst-ib")).sendKeys("model based test framework");
        getDriver().findElement(By.id("tsf")).submit();
    }

    @Override
    public void v_Google(Context context) {
    }

    @Override
    public void v_Search(Context context) {
        Assert.assertFalse(true);
    }

    @Override
    public void e_VisitGoogle(Context context) {
        getDriver().get("http://www.google.se");
    }

    @Override
    public void e_SearchYoutube(Context context) {
        getDriver().findElement(By.id("gb_36")).click();
        getDriver().findElement(By.id("masthead-search-term")).sendKeys("model based test framework");
        getDriver().findElement(By.id("masthead-search")).submit();
    }

    @Override
    public void e_SearchText(Context context) {
        getDriver().findElement(By.id("gbqfq")).sendKeys("model based test framework");
        getDriver().findElement(By.id("gbqf")).submit();
    }
}