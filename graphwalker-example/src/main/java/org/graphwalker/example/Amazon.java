package org.graphwalker.example;

import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.conditions.support.Length;
import org.graphwalker.core.generators.support.AStarPath;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.machine.Context;
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
    public void e_ShoppingCart(Context context) {
        int i = 0;
    }

    @Override
    public void v_BrowserStarted(Context context) {
        int i = 0;
    }

    @Override
    public void Start(Context context) {
        int i = 0;
    }

    @Override
    public void v_OtherBoughtBooks(Context context) {
        int i = 0;
    }

    @Override
    public void e_EnterBaseURL(Context context) {
        int i = 0;
    }

    @Override
    public void e_AddBookToCart(Context context) {
        int i = 0;
    }

    @Override
    public void e_SearchBook(Context context) {
        int i = 0;
    }

    @Override
    public void e_StartBrowser(Context context) {
        int i = 0;
    }

    @Override
    public void v_BookInformation(Context context) {
        int i = 0;
    }

    @Override
    public void v_ShoppingCart(Context context) {
        int i = 0;
    }

    @Override
    public void v_SearchResult(Context context) {
        int i = 0;
    }

    @Override
    public void v_BaseURL(Context context) {
        int i = 0;
    }

    @Override
    public void e_ClickBook(Context context) {
        int i = 0;
    }
}
