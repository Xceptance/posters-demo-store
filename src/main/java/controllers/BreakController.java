package controllers;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import conf.StatusConf;
import conf.PosterConstants;
import ninja.Result;
import ninja.Results;
import ninja.Context;
import ninja.i18n.Messages;
import ninja.params.PathParam;

/*
 * A controller handling the ability to demonstrate wrong site behaviour.
 * It connects the 'secret' page to configure how the shop should behave to the class storing that information.
 * The actual breaking of functionalities is realized by AdjustStateFilter.
 */
public class BreakController
{
        
    @Inject
    Messages msg;

    @Inject
    PosterConstants xcpConf;

    @Inject
    StatusConf stsConf;

    // render page that informs about current configuration of what to break
    public Result statusInfo(final Context context, @PathParam("urlLocale") String locale)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // load status info into data Map
        stsConf.getStatus(data);
        return Results.html().render(data).template(xcpConf.TEMPLATE_STATUS_INFO);
    }

    // save changes made to configuration and then redirect to status info
    public  Result statusUpdate(final Context context, @PathParam("urlLocale") String locale)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);

        // get the choices made in the submitted form
        String breakCategories = context.getParameter("breakCategories");
        String breakCartQuantities = context.getParameter("breakCartQuantities");
        String blockOrders = context.getParameter("blockOrders");
        String breakSearch = context.getParameter("breakSearch");
        String mixUpAddToCart = context.getParameter("mixUpAddToCart");
        String openLogin = context.getParameter("openLogin");
        String breakOrderHistory = context.getParameter("breakOrderHistory");
        String breakLocalization = context.getParameter("breakLocalization");

        // save the desired configuration
        stsConf.setCategoriesBroken(isSet(breakCategories));
        stsConf.setCartQuantitiesChange(isSet(breakCartQuantities));
        stsConf.setOrdersBlocked(isSet(blockOrders));
        stsConf.setSearchResultsChanged(isSet(breakSearch));
        stsConf.setCartProductMixups(isSet(mixUpAddToCart));
        stsConf.setOpenLogin(isSet(openLogin));
        stsConf.setOrderHistoryMessy(isSet(breakOrderHistory));
        stsConf.setWrongLocale(isSet(breakLocalization));
        
        return Results.redirect(context.getContextPath() + "/" + locale + "/ok3ok2ru8udqx7gZGS9n/statusInfo");
    }

    private boolean isSet(String configurationChoiceParameter)
    {
        if (configurationChoiceParameter!=null && configurationChoiceParameter.equals("on"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
