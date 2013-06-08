package org.graphwalker.example;

import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.conditions.support.Length;
import org.graphwalker.core.generators.support.AStarPath;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.example.models.ShoppingCart;

@GraphWalker({
        @Execute(group = "shortest"
                , pathGenerator = AStarPath.class
                , stopCondition = EdgeCoverage.class
                , stopConditionValue = "100"),

        @Execute(group = "random"
                , pathGenerator = RandomPath.class
                , stopCondition = Length.class
                , stopConditionValue = "20")
})
public class Amazon implements ShoppingCart {

    @Override
    public void e_ShoppingCart(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void v_BrowserStarted(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void Start(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void v_OtherBoughtBooks(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void e_EnterBaseURL(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void e_AddBookToCart(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void e_SearchBook(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void e_StartBrowser(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void v_BookInformation(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void v_ShoppingCart(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void v_SearchResult(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void v_BaseURL(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }

    @Override
    public void e_ClickBook(ExecutionContext executionContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.         }
        }
    }
}