package com.xceptance.posters.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Tracks application "break" status flags for intentional bug injection
 * (used for testing/demo purposes — e.g. breaking categories, cart, orders).
 */
@Component
public class StatusConf
{
    private boolean categoriesBroken = false;
    private boolean cartQuantitiesChange = false;
    private boolean ordersBlocked = false;
    private boolean searchResultsChanged = false;
    private boolean cartProductMixups = false;
    private boolean openLogin = false;
    private boolean orderHistoryMessy = false;
    private boolean wrongLocale = false;
    private boolean productBlock = false;
    private int blockedId = 0;
    private boolean productOrderBlockWhen = false;
    private int includedId = 0;
    private boolean searchCounterWrongBy = false;
    private int counterAdjustment = 0;
    private boolean blockSearchWhen = false;
    private String blockedTerm = "";
    private boolean cartLimit = false;
    private int limitMax = 100;
    private boolean cartLimitTotal = false;
    private int limitTotal = 100;

    public Map<String, Object> getStatus()
    {
        Map<String, Object> status = new HashMap<>();
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
        return status;
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

    // --- Getters and Setters ---

    public boolean isCategoriesBroken() { return categoriesBroken; }
    public void setCategoriesBroken(boolean v) { this.categoriesBroken = v; }

    public boolean isCartQuantitiesChange() { return cartQuantitiesChange; }
    public void setCartQuantitiesChange(boolean v) { this.cartQuantitiesChange = v; }

    public boolean isOrdersBlocked() { return ordersBlocked; }
    public void setOrdersBlocked(boolean v) { this.ordersBlocked = v; }

    public boolean isSearchResultsChanged() { return searchResultsChanged; }
    public void setSearchResultsChanged(boolean v) { this.searchResultsChanged = v; }

    public boolean isCartProductMixups() { return cartProductMixups; }
    public void setCartProductMixups(boolean v) { this.cartProductMixups = v; }

    public boolean isOpenLogin() { return openLogin; }
    public void setOpenLogin(boolean v) { this.openLogin = v; }

    public boolean isOrderHistoryMessy() { return orderHistoryMessy; }
    public void setOrderHistoryMessy(boolean v) { this.orderHistoryMessy = v; }

    public boolean isWrongLocale() { return wrongLocale; }
    public void setWrongLocale(boolean v) { this.wrongLocale = v; }

    public boolean isProductBlock() { return productBlock; }
    public void setProductBlock(boolean v) { this.productBlock = v; }

    public int getBlockedId() { return blockedId; }
    public void setBlockedId(int v) { this.blockedId = v; }

    public boolean isProductOrderBlockWhen() { return productOrderBlockWhen; }
    public void setProductOrderBlockWhen(boolean v) { this.productOrderBlockWhen = v; }

    public int getIncludedId() { return includedId; }
    public void setIncludedId(int v) { this.includedId = v; }

    public boolean isSearchCounterWrongBy() { return searchCounterWrongBy; }
    public void setSearchCounterWrongBy(boolean v) { this.searchCounterWrongBy = v; }

    public int getCounterAdjustment() { return counterAdjustment; }
    public void setCounterAdjustment(int v) { this.counterAdjustment = v; }

    public boolean isBlockSearchWhen() { return blockSearchWhen; }
    public void setBlockSearchWhen(boolean v) { this.blockSearchWhen = v; }

    public String getBlockedTerm() { return blockedTerm; }
    public void setBlockedTerm(String v) { this.blockedTerm = v; }

    public boolean isCartLimit() { return cartLimit; }
    public void setCartLimit(boolean v) { this.cartLimit = v; }

    public int getLimitMax() { return limitMax; }
    public void setLimitMax(int v) { this.limitMax = v; }

    public boolean isCartLimitTotal() { return cartLimitTotal; }
    public void setCartLimitTotal(boolean v) { this.cartLimitTotal = v; }

    public int getLimitTotal() { return limitTotal; }
    public void setLimitTotal(int v) { this.limitTotal = v; }
}
