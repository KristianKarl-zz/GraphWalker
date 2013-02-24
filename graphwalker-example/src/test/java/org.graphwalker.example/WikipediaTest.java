package org.graphwalker.example;

import org.graphwalker.core.annotations.BeforeModel;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.RequirementStatus;
import org.graphwalker.core.model.RequirementStatusListener;
import org.graphwalker.core.utils.Assert;
import org.graphwalker.example.base.SeleniumBasedTest;

@GraphWalker(id = "wikipediaSenario", model = "models/WikipediaModel.graphml")
public class WikipediaTest extends SeleniumBasedTest implements RequirementStatusListener {

    //private final String myStartPage = System.getProperty("wikipedia.start.page");

    @BeforeModel
    public void assertTestPreconditions() {
        //Assert.assertNotNull(myStartPage);
    }

    public void e_startpage() {
        //getWebDriver().navigate().to(myStartPage);
    }

    public void v_startpage() {
        //Assert.assertTrue(myStartPage.equals(getWebDriver().getCurrentUrl()));
    }

    public void requirementStatusChanged(Requirement requirement, RequirementStatus oldStatus, RequirementStatus newStatus) {
        //System.out.println("RequirementStatus for " + requirement.getId() + " has changed from " + oldStatus + " to " + newStatus);
    }
}
