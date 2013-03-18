package org.graphwalker.example;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.model.support.GraphMLModelFactory;
import org.graphwalker.core.model.support.ModelContext;
import org.graphwalker.core.reports.GraphWalkerReportType;
import org.graphwalker.core.utils.Assert;
import org.testng.annotations.Test;

public class AmazonTest {


    /*
    @Test
    public void a_star() throws InterruptedException, StopConditionException, URISyntaxException {
        ModelHandler modelhandler = new ModelHandler();

        // Get the model from resources
        URL url = AmazonTest.class.getResource("/model/ShoppingCart.graphml");
        File file = new File(url.toURI());

        // Connect the model to a java class, and add it to graphwalker's modelhandler.
        // The model is to be executed using the following criteria:
        // EFSM:           Extended finite state machine is set to true, which means we are using the data domain
        //                 in the model
        // Generator:      a_star, we want to walk through the model using shortest possible path.
        // Stop condition: Edge coverage 100%, we want to walk every edge in the model.
        modelhandler.add("Amazon", new Amazon(file, true, new A_StarPathGenerator(new EdgeCoverage(1.0)), false));

        // Start executing the test
        modelhandler.execute("Amazon");

        // Verify that the execution is complete, fulfilling the criteria from above.
        Assert.assertTrue(modelhandler.isAllModelsDone(), "Not all models are done");

        //Print the statistics from graphwalker
        String actualResult = modelhandler.getStatistics();
        System.out.println(actualResult);
    }
    */

    @Test
    public void random() {
        /*
        GraphMLModelFactory factory = new GraphMLModelFactory();
        ModelContext context = new ModelContext();
        context.addModel(factory.create("Amazon", ));

        GraphWalker graphWalker = new GraphWalker();
        */


        /*
        ModelHandler modelhandler = new ModelHandler();

        // Get the model from resources
        URL url = AmazonTest.class.getResource("/model/ShoppingCart.graphml");
        File file = new File(url.toURI());

        // Connect the model to a java class, and add it to graphwalker's modelhandler.
        // The model is to be executed using the following criteria:
        // EFSM:           Extended finite state machine is set to true, which means we are using the data domain
        //                 in the model
        // Generator:      random, walk through the model randomly
        // Stop condition: Let the sequence be 20 steps long (pairs of edges and vertices)
        modelhandler.add("Amazon", new Amazon(file, true, new RandomPathGenerator(new TestCaseLength(20)), false));

        // Start executing the test
        modelhandler.execute("Amazon");

        // Verify that the execution is complete, fulfilling the criteria from above.
        Assert.assertTrue(modelhandler.isAllModelsDone(), "Not all models are done");

        //Print the statistics from graphwalker
        String actualResult = modelhandler.getStatistics();
        System.out.println(actualResult);
        */
    }

}
