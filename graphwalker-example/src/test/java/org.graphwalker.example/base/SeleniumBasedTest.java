package org.graphwalker.example.base;

import com.thoughtworks.selenium.SeleniumException;
import org.graphwalker.core.annotations.After;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.concurrent.TimeUnit;

public abstract class SeleniumBasedTest {

    private static final long DEFAULT_TIMEOUT = 30;
    private static final BrowserType DEFAULT_BROWSER = BrowserType.FIREFOX;
    private static WebDriver myWebDriver;

    @After
    public void destroyWebDriver() {
        if (null != myWebDriver) {
            myWebDriver.close();
            myWebDriver = null;
        }
    }

    protected WebDriver getWebDriver() {
        if (null == myWebDriver) {
            myWebDriver = createWebDriver(getBrowserType());
            myWebDriver.manage().timeouts().implicitlyWait(getTimeout(), TimeUnit.SECONDS);
        }
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
