package org.graphwalker.example;

import org.graphwalker.core.annotation.GraphWalker;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.script.ScriptContext;
import org.graphwalker.example.models.google.GoogleStart;
import org.graphwalker.example.models.google.SearchImage;
import org.graphwalker.example.models.google.SearchText;
import org.graphwalker.example.models.google.SearchYoutube;
import org.openqa.selenium.By;

@GraphWalker
public class Google extends AbstractTest implements GoogleStart, SearchText, SearchImage, SearchYoutube {

    @Override
    public void e_SearchImage(ScriptContext context) {
        getDriver().findElement(By.id("gb_2")).click();
        getDriver().findElement(By.id("lst-ib")).sendKeys("model based test framework");
        getDriver().findElement(By.id("tsf")).submit();
    }

    @Override
    public void v_Google(ScriptContext context) {
    }

    @Override
    public void v_Search(ScriptContext context) {
        Assert.assertFalse(true);
    }

    @Override
    public void e_VisitGoogle(ScriptContext context) {
        getDriver().get("http://www.google.se");
    }

    @Override
    public void e_SearchYoutube(ScriptContext context) {
        getDriver().findElement(By.id("gb_36")).click();
        getDriver().findElement(By.id("masthead-search-term")).sendKeys("model based test framework");
        getDriver().findElement(By.id("masthead-search")).submit();
    }

    @Override
    public void e_SearchText(ScriptContext context) {
        getDriver().findElement(By.id("gbqfq")).sendKeys("model based test framework");
        getDriver().findElement(By.id("gbqf")).submit();
    }
}
