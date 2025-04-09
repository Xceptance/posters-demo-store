package conf;

import java.util.Map;
import com.google.inject.Singleton;

@Singleton
public class StatusConf {
    
    // enabling this introduces mixups between the categories
    private boolean categoriesBroken = false;

    // enabling this changes qauntities added to cart
    private boolean cartQuantitiesChange = false;

    // enabling this makes orders not go through
    private boolean ordersBlocked = false;

    // enabling this changes the results of searches
    private boolean searchResultsChanged = false;

    // enabling this changes products added to cart
    private boolean cartProductMixups = false;

    // enabling this makes login accept all passwords
    private boolean openLogin = false;

    // enabling this displays an inorrect order history
    private boolean orderHistoryMessy = false;

    // enabling this messes up localization
    private boolean wrongLocale = false;

    // enabling this blocks the product with a given ID from add to cart
    private boolean productBlock = false;
    private int blockedId = 0;

    // enabling this blocks orders that include a product with a given ID
    private boolean productOrderBlockWhen = false;
    private int includedId = 0;

    // enabling this adds the given value to the search result counter
    private boolean searchCounterWrongBy = false;
    private int counterAdjustment = 0;

    // enabling this blocks the product with a given ID from being returned by a search
    private boolean blockSearchWhen = false;
    private String blockedTerm = "";

    // enabling this makes the cart accept only additions of products up to the specified limit per product
    private boolean cartLimit = false;
    private int limitMax = 100;

    // enabling this makes the cart accept only additions of products up to the specified limit in total
    private boolean cartLimitTotal = false;
    private int limitTotal = 100;

    public void getStatus(final Map<String, Object> status)
    {
        status.put("categoriesBroken", categoriesBroken);
        status.put("cartQuantitiesChange", cartQuantitiesChange);
        status.put("ordersBlocked", ordersBlocked);
        status.put("searchResultsChanged", searchResultsChanged);
        status.put("cartProductMixups", cartProductMixups);
        status.put("openLogin", openLogin);
        status.put("orderHistoryMessy", orderHistoryMessy);
        status.put("wrongLocale", wrongLocale);
        status.put("productBlock", productBlock);
        status.put("blockedId", blockedId);
        status.put("productOrderBlock", productOrderBlockWhen);
        status.put("includedId", includedId);
        status.put("searchCounterWrong", searchCounterWrongBy);
        status.put("counterAdjustment", counterAdjustment);
        status.put("blockSearch", blockSearchWhen);
        status.put("blockedTerm", blockedTerm);
        status.put("cartLimit", cartLimit);
        status.put("limitMax", limitMax);
        status.put("cartLimitTotal", cartLimitTotal);
        status.put("limitTotal", limitTotal);
    }

    public void disableAll()
    {
        this.categoriesBroken = false;
        this.cartQuantitiesChange = false;
        this.ordersBlocked = false;
        this.searchResultsChanged = false;
        this.cartProductMixups = false;
        this.openLogin = false;
        this.orderHistoryMessy = false;
        this.wrongLocale = false;
        this.productBlock = false;
        this.productOrderBlockWhen = false;
        this.searchCounterWrongBy = false;
        this.blockSearchWhen = false;
        this.cartLimit = false;
        this.cartLimitTotal = false;
    }

    public void setCategoriesBroken(boolean breakCategories)
    {
        this.categoriesBroken = breakCategories;
    }

    public void setCartQuantitiesChange(boolean cartQuantitiesChange) {
        this.cartQuantitiesChange = cartQuantitiesChange;
    }

    public void setOrdersBlocked(boolean ordersBlocked) {
        this.ordersBlocked = ordersBlocked;
    }

    public void setSearchResultsChanged(boolean searchResultsChanged) {
        this.searchResultsChanged = searchResultsChanged;
    }

    public void setCartProductMixups(boolean cartProductMixups) {
        this.cartProductMixups = cartProductMixups;
    }

    public void setOpenLogin(boolean openLogin) {
        this.openLogin = openLogin;
    }

    public void setOrderHistoryMessy(boolean orderHistoryMessy) {
        this.orderHistoryMessy = orderHistoryMessy;
    }

    public void setWrongLocale(boolean wrongLocale) {
        this.wrongLocale = wrongLocale;
    }

    public void setProductBlock(boolean productBlock) {
        this.productBlock = productBlock;
    }

    public void setBlockedId(int blockedId) {
        this.blockedId = blockedId;
    }

    public void setProductOrderBlockWhen(boolean productOrderBlockWhen) {
        this.productOrderBlockWhen = productOrderBlockWhen;
    }

    public void setIncludedId(int includedId) {
        this.includedId = includedId;
    }

    public void setSearchCounterWrongBy(boolean searchCounterWrongBy) {
        this.searchCounterWrongBy = searchCounterWrongBy;
    }

    public void setCounterAdjustment(int counterAdjustment) {
        this.counterAdjustment = counterAdjustment;
    }

    public void setBlockSearchWhen(boolean blockSearchWhen) {
        this.blockSearchWhen = blockSearchWhen;
    }

    public void setBlockedTerm(String blockedTerm) {
        this.blockedTerm = blockedTerm;
    }

    public void setCartLimit(boolean cartLimit) {
        this.cartLimit = cartLimit;
    }

    public void setLimitMax(int limitMax) {
        this.limitMax = limitMax;
    }

    public void setCartLimitTotal(boolean cartLimitTotal) {
        this.cartLimitTotal = cartLimitTotal;
    }

    public void setLimitTotal(int limitTotal) {
        this.limitTotal = limitTotal;
    }

    
}
