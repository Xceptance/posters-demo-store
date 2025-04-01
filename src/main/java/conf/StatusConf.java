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

    
}
