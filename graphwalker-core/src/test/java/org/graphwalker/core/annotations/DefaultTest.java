package org.graphwalker.core.annotations;

import org.graphwalker.core.GraphWalkerExecutor;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.utils.Assert;
import org.junit.Test;

@GraphWalker
public class DefaultTest {

    @Test
    public void executeTest() {
        new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass()))).run();
    }

    @BeforeModel
    public void beforeModel(Model model) {
        Assert.assertEquals(model.getId(), getClass().getSimpleName());
    }

}
