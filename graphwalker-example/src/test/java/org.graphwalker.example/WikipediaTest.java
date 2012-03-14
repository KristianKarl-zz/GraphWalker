package org.graphwalker.example;

import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.RequirementStatus;
import org.graphwalker.core.model.RequirementStatusListener;
import org.graphwalker.core.utils.Assert;
import org.graphwalker.example.base.SeleniumBasedTest;

@GraphWalker(id = "wikipediaSenario", model = "models/WikipediaModel.graphml", group = "wikipedia")
public class WikipediaTest extends SeleniumBasedTest implements RequirementStatusListener {

    public void e_startpage() {
        getWebDriver().navigate().to(System.getProperty("wikipedia.start.page"));
    }

    public void v_startpage() {
        Assert.assertTrue(System.getProperty("wikipedia.start.page").equals(getWebDriver().getCurrentUrl()));
    }

    public void requirementStatusChanged(Requirement requirement, RequirementStatus oldStatus, RequirementStatus newStatus) {
        System.out.println("RequirementStatus for "+requirement.getId()+" has changed from "+oldStatus+" to "+newStatus);
    }
}
