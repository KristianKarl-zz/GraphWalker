package org.graphwalker.example;

import org.graphwalker.core.annotations.Before;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.utils.Assert;
import org.graphwalker.example.base.SeleniumBasedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

@GraphWalker(id = "youtubeSenario", model = "models/YouTubeModel.graphml")
public class YouTubeTest extends SeleniumBasedTest {

    private final String myStartPage = System.getProperty("youtube.start.page");
    private String myCurrentFeedTitle = null;
    private String myCurrentVideoTitle = null;
    private WebElement myCurrentGuideElement = null;

    @Before
    public void assertTestPreconditions() {
        Assert.assertNotNull(myStartPage);
    }

    public void e_startpage() {
        getWebDriver().navigate().to(myStartPage);
    }

    public void v_startpage() {
        Assert.assertTrue(myStartPage.equals(getWebDriver().getCurrentUrl()));
    }

    public void e_search() {
        WebElement webElement = getWebDriver().findElement(By.id("masthead-search-term"));
        webElement.sendKeys("model based testing");
        webElement.submit();
    }

    public void v_search() {
        WebElement searchHeader = getWebDriver().findElement(By.id("search-header-inner"));
        Assert.assertNotNull(searchHeader);
        Assert.assertTrue(searchHeader.getText().contains("model based testing"));
    }

    public void e_home() {
        getWebDriver().findElement(By.id("logo")).click();
    }

    public void e_guide() {
        List<WebElement> webElements = getWebDriver().findElements(By.cssSelector("a.guide-item"));
        myCurrentGuideElement = webElements.get(new Random().nextInt(webElements.size()));
        myCurrentGuideElement.click();
    }

    public void v_guide() {
        Assert.assertNotNull(myCurrentGuideElement);
        Assert.assertTrue(myCurrentGuideElement.isDisplayed());
        Assert.assertTrue(myCurrentGuideElement.isEnabled());
        Assert.assertTrue(myCurrentGuideElement.getAttribute("class").contains("selected"));
    }

    public void e_chooseVideo() {
        List<WebElement> webElements = getWebDriver().findElements(By.cssSelector("a.yt-uix-tile-link"));
        WebElement webElement = webElements.get(new Random().nextInt(webElements.size()));
        myCurrentVideoTitle = webElement.getText();
        webElement.click();
    }

    public void v_chooseVideo() {
        Assert.assertNotNull(myCurrentVideoTitle);
        //WebElement titleElement = getWebDriver().findElement(By.id("eow-title"));
        //Assert.assertTrue(titleElement.getAttribute("title").equals(myCurrentVideoTitle));
    }

    public void e_chooseFeed() {
        List<WebElement> webElements = getWebDriver().findElements(By.cssSelector("a.title.yt-uix-sessionlink"));
        WebElement webElement = webElements.get(new Random().nextInt(webElements.size()));
        myCurrentFeedTitle = webElement.getText();
        webElement.click();
    }

    public void v_chooseFeed() {
        Assert.assertNotNull(myCurrentFeedTitle);
        //WebElement titleElement = getWebDriver().findElement(By.id("eow-title"));
        //Assert.assertTrue(titleElement.getAttribute("title").equals(myCurrentFeedTitle));
    }
}
