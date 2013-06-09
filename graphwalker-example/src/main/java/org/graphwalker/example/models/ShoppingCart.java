// Generated by GraphWalker (http://www.graphwalker.org)                                                                                                                                     
package org.graphwalker.example.models;

import org.graphwalker.core.annotations.Model;
import org.graphwalker.core.script.Context;

@Model(file = "org/graphwalker/example/models/ShoppingCart.graphml", type = "graphml")
public interface ShoppingCart {

    void e_ShoppingCart(Context context);

    void v_BrowserStarted(Context context);

    void v_OtherBoughtBooks(Context context);

    void e_EnterBaseURL(Context context);

    void e_AddBookToCart(Context context);

    void e_SearchBook(Context context);

    void e_StartBrowser(Context context);

    void v_BookInformation(Context context);

    void v_ShoppingCart(Context context);

    void v_SearchResult(Context context);

    void v_BaseURL(Context context);

    void e_ClickBook(Context context);

    void v_ClearShoppingCart(Context context);

    void e_ClearShoppingCart(Context context);
}
