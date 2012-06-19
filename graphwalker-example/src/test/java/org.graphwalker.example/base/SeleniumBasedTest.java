package org.graphwalker.example.base;

import com.google.common.base.Predicate;
import com.thoughtworks.selenium.SeleniumException;
import org.graphwalker.core.annotations.AfterModel;
import org.graphwalker.core.annotations.BeforeGroup;
import org.graphwalker.core.annotations.BeforeModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.concurrent.TimeUnit;

public abstract class SeleniumBasedTest {

    private static final long DEFAULT_TIMEOUT = 30;
    private static final BrowserType DEFAULT_BROWSER = BrowserType.FIREFOX;
    private WebDriver myWebDriver;

    @BeforeGroup
    public void beforeGroup() {
        System.out.println("before group");
    }

    @BeforeModel
    public void createWebDriver() {
        setWebDriver(createWebDriver(getBrowserType()));
        getWebDriver().manage().timeouts().implicitlyWait(getTimeout(), TimeUnit.SECONDS);
    }

    @AfterModel
    public void destroyWebDriver() {
        if (null != getWebDriver()) {
            getWebDriver().close();
            setWebDriver(null);
        }
    }

    protected void waitFor(final By by) {
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.withTimeout(getTimeout(), TimeUnit.SECONDS);
        fluentWait.pollingEvery(1, TimeUnit.SECONDS);
        fluentWait.until(new Predicate<By>() {
            public boolean apply(By input) {
                try {
                    return getWebDriver().findElement(by).isDisplayed();
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        });
    }

    protected void setWebDriver(WebDriver webDriver) {
        myWebDriver = webDriver;
    }

    protected WebDriver getWebDriver() {
        return myWebDriver;
    }

    private WebDriver createWebDriver(BrowserType type) {
        switch (type) {
            case CHROME:
                return new ChromeDriver();
            case FIREFOX:
                return new FirefoxDriver();
            case IEXPLORER:
                return new InternetExplorerDriver();
            default:
                throw new SeleniumException("Unknown browser type");
        }
    }

    protected enum BrowserType {
        CHROME, FIREFOX, IEXPLORER, OPERA
    }

    private BrowserType getBrowserType() {
        if (null != System.getProperty("browser.type")) {
            return BrowserType.valueOf(System.getProperty("browser.type"));
        }
        return DEFAULT_BROWSER;
    }

    private long getTimeout() {
        if (null != System.getProperty("browser.timeout")) {
            return Long.parseLong(System.getProperty("browser.timeout"));
        }
        return DEFAULT_TIMEOUT;
    }

}
