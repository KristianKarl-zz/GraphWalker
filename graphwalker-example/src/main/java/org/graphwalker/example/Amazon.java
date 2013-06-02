package org.graphwalker.example;

import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.conditions.support.Length;
import org.graphwalker.core.generators.support.AStarPath;
import org.graphwalker.core.generators.support.RandomPath;

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
    public void e_ShoppingCart() {
        int i = 0;
    }

    @Override
    public void v_BrowserStarted() {
        int i = 0;
    }

    @Override
    public void Start() {
        int i = 0;
    }

    @Override
    public void v_OtherBoughtBooks() {
        int i = 0;
    }

    @Override
    public void e_EnterBaseURL() {
        int i = 0;
    }

    @Override
    public void e_AddBookToCart() {
        int i = 0;
    }

    @Override
    public void e_SearchBook() {
        int i = 0;
    }

    @Override
    public void e_StartBrowser() {
        int i = 0;
    }

    @Override
    public void v_BookInformation() {
        int i = 0;
    }

    @Override
    public void v_ShoppingCart() {
        int i = 0;
    }

    @Override
    public void v_SearchResult() {
        int i = 0;
    }

    @Override
    public void v_BaseURL() {
        int i = 0;
    }

    @Override
    public void e_ClickBook() {
        int i = 0;
    }
}
