package org.graphwalker.example;

import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.utils.Assert;
import org.graphwalker.example.base.SeleniumBasedTest;

@GraphWalker(id = "wikipediaSenario", model = "models/WikipediaModel.graphml", group = "wikipedia")
public class WikipediaTest extends SeleniumBasedTest {

    public void e_startpage() {
        getWebDriver().navigate().to(System.getProperty("wikipedia.start.page"));
    }

    public void v_startpage() {
        Assert.assertTrue(System.getProperty("wikipedia.start.page").equals(getWebDriver().getCurrentUrl()));
    }
}
