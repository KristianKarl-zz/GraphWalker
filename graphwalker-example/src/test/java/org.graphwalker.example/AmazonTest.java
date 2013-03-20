package org.graphwalker.example;

import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.generators.support.AStarPath;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.java.GraphWalker;
import org.graphwalker.core.conditions.support.Length;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.support.GraphMLModelFactory;
import org.graphwalker.core.utils.Assert;
import org.testng.annotations.Test;

public class AmazonTest {

    @Test
    public void a_star() {

        // Get the model from resources
        ModelFactory factory = new GraphMLModelFactory();
        Model model = factory.create("Amazon", "/models/ShoppingCart.graphml");

        GraphWalker graphWalker = new GraphWalker();
        graphWalker.addModel(model, new AStarPath(new EdgeCoverage(100)), new Amazon());

        // Start executing the test
        graphWalker.execute(model);

        // Verify that the execution is complete, fulfilling the criteria from above.
        Assert.assertFalse(graphWalker.isAllModelsDone(), "Not all models are done");

        //Print the statistics from graphwalker
        //String actualResult = modelhandler.getStatistics();
        //System.out.println(actualResult);




        /*
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
        */
    }

    @Test
    public void random() {

        // Get the model from resources
        GraphMLModelFactory factory = new GraphMLModelFactory();
        Model model = factory.create("Amazon", "/models/ShoppingCart.graphml");

        GraphWalker graphWalker = new GraphWalker();
        graphWalker.addModel(model, new RandomPath(new Length(20)), new Amazon());

        // Start executing the test
        graphWalker.execute(model);

        // Verify that the execution is complete, fulfilling the criteria from above.
        Assert.assertFalse(graphWalker.isAllModelsDone(), "Not all models are done");

        //Print the statistics from graphwalker
        //String actualResult = modelhandler.getStatistics();
        //System.out.println(actualResult);




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
