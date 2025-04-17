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

    // render page that informs about current configuration of what to break
    public Result statusInfoDesign2(final Context context, @PathParam("urlLocale") String locale)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // load status info into data Map
        stsConf.getStatus(data);
        return Results.html().render(data).template(xcpConf.TEMPLATE_STATUS_INFO_D2);
    }

    // save changes made to configuration and then redirect to status info
    public  Result statusUpdate(final Context context, @PathParam("urlLocale") String locale)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // load current status info
        stsConf.getStatus(data);

        // get the choices made in the submitted form
        // static options
        String breakCategories = context.getParameter("breakCategories");
        String breakCartQuantities = context.getParameter("breakCartQuantities");
        String blockOrders = context.getParameter("blockOrders");
        String breakSearch = context.getParameter("breakSearch");
        String mixUpAddToCart = context.getParameter("mixUpAddToCart");
        String openLogin = context.getParameter("openLogin");
        String breakOrderHistory = context.getParameter("breakOrderHistory");
        String breakLocalization = context.getParameter("breakLocalization");
        // adjustable options
        String productBlock = context.getParameter("productBlock");
        String productBlockId = context.getParameter("productBlockId");
        String productOrderBlock = context.getParameter("productOrderBlock");
        String productOrderBlockId = context.getParameter("productOrderBlockId");
        String searchCounterWrong = context.getParameter("searchCounterWrong");
        String searchCounterWrongBy = context.getParameter("searchCounterWrongBy");
        String blockSearch = context.getParameter("blockSearch");
        String blockSearchPhrase = context.getParameter("blockSearchPhrase");
        String cartLimit = context.getParameter("cartLimit");
        String cartLimitNum = context.getParameter("cartLimitNum");
        String cartLimitTotal = context.getParameter("cartLimitTotal");
        String cartLimitTotalNum = context.getParameter("cartLimitTotalNum");

        // save the desired configuration
        // static options
        stsConf.setCategoriesBroken(isSet(breakCategories));
        stsConf.setCartQuantitiesChange(isSet(breakCartQuantities));
        stsConf.setOrdersBlocked(isSet(blockOrders));
        stsConf.setSearchResultsChanged(isSet(breakSearch));
        stsConf.setCartProductMixups(isSet(mixUpAddToCart));
        stsConf.setOpenLogin(isSet(openLogin));
        stsConf.setOrderHistoryMessy(isSet(breakOrderHistory));
        stsConf.setWrongLocale(isSet(breakLocalization));
        // adjustable options
        stsConf.setProductBlock(isSet(productBlock));
        stsConf.setBlockedId(getProductId(productBlockId, stsConf.getBlockedId()));
        stsConf.setProductOrderBlockWhen(isSet(productOrderBlock));
        stsConf.setIncludedId(getProductId(productOrderBlockId, stsConf.getIncludedId()));
        stsConf.setSearchCounterWrongBy(isSet(searchCounterWrong));
        stsConf.setCounterAdjustment(getAdjustmentValue(searchCounterWrongBy, stsConf.getCounterAdjustment()));
        stsConf.setBlockSearchWhen(isSet(blockSearch));
        stsConf.setBlockedTerm(evaluatePhrase(blockSearchPhrase, stsConf.getBlockedTerm()));
        stsConf.setCartLimit(isSet(cartLimit));
        stsConf.setLimitMax(getCartLimit(cartLimitNum, stsConf.getLimitMax()));
        stsConf.setCartLimitTotal(isSet(cartLimitTotal));
        stsConf.setLimitTotal(getCartLimit(cartLimitTotalNum, stsConf.getLimitTotal()));
        
        return Results.redirect(context.getContextPath() + "/" + locale + "/ok3ok2ru8udqx7gZGS9n/statusInfo");
    }

    // save changes made to configuration and then redirect to status info
    public  Result statusUpdateDesign2(final Context context, @PathParam("urlLocale") String locale)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // load current status info
        stsConf.getStatus(data);

        // get the choices made in the submitted form
        // static options
        String breakCategories = context.getParameter("breakCategories");
        String breakCartQuantities = context.getParameter("breakCartQuantities");
        String blockOrders = context.getParameter("blockOrders");
        String breakSearch = context.getParameter("breakSearch");
        String mixUpAddToCart = context.getParameter("mixUpAddToCart");
        String openLogin = context.getParameter("openLogin");
        String breakOrderHistory = context.getParameter("breakOrderHistory");
        String breakLocalization = context.getParameter("breakLocalization");
        // adjustable options
        String productBlock = context.getParameter("productBlock");
        String productBlockId = context.getParameter("productBlockId");
        String productOrderBlock = context.getParameter("productOrderBlock");
        String productOrderBlockId = context.getParameter("productOrderBlockId");
        String searchCounterWrong = context.getParameter("searchCounterWrong");
        String searchCounterWrongBy = context.getParameter("searchCounterWrongBy");
        String blockSearch = context.getParameter("blockSearch");
        String blockSearchPhrase = context.getParameter("blockSearchPhrase");
        String cartLimit = context.getParameter("cartLimit");
        String cartLimitNum = context.getParameter("cartLimitNum");
        String cartLimitTotal = context.getParameter("cartLimitTotal");
        String cartLimitTotalNum = context.getParameter("cartLimitTotalNum");

        // save the desired configuration
        // static options
        stsConf.setCategoriesBroken(isSet(breakCategories));
        stsConf.setCartQuantitiesChange(isSet(breakCartQuantities));
        stsConf.setOrdersBlocked(isSet(blockOrders));
        stsConf.setSearchResultsChanged(isSet(breakSearch));
        stsConf.setCartProductMixups(isSet(mixUpAddToCart));
        stsConf.setOpenLogin(isSet(openLogin));
        stsConf.setOrderHistoryMessy(isSet(breakOrderHistory));
        stsConf.setWrongLocale(isSet(breakLocalization));
        // adjustable options
        stsConf.setProductBlock(isSet(productBlock));
        stsConf.setBlockedId(getProductId(productBlockId, stsConf.getBlockedId()));
        stsConf.setProductOrderBlockWhen(isSet(productOrderBlock));
        stsConf.setIncludedId(getProductId(productOrderBlockId, stsConf.getIncludedId()));
        stsConf.setSearchCounterWrongBy(isSet(searchCounterWrong));
        stsConf.setCounterAdjustment(getAdjustmentValue(searchCounterWrongBy, stsConf.getCounterAdjustment()));
        stsConf.setBlockSearchWhen(isSet(blockSearch));
        stsConf.setBlockedTerm(evaluatePhrase(blockSearchPhrase, stsConf.getBlockedTerm()));
        stsConf.setCartLimit(isSet(cartLimit));
        stsConf.setLimitMax(getCartLimit(cartLimitNum, stsConf.getLimitMax()));
        stsConf.setCartLimitTotal(isSet(cartLimitTotal));
        stsConf.setLimitTotal(getCartLimit(cartLimitTotalNum, stsConf.getLimitTotal()));
        
        return Results.redirect(context.getContextPath() + "/" + locale + "/ok3ok2ru8udqx7gZGS9n/statusInfoDesign2");
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

    private int getProductId(String configurationChoiceParameter, int currentId)
    {
        int resultId = currentId;
        if (configurationChoiceParameter!=null && isInteger(configurationChoiceParameter))
        {
            resultId = Integer.parseInt(configurationChoiceParameter);
            if (resultId <= 0)
            {
                resultId = 1;
            } else if (resultId >= 125)
            {
                resultId = 124;
            }
        }
        return resultId;
    }

    private String evaluatePhrase(String userInput, String previousPhrase) {
        if (userInput == null || userInput.isBlank())
        {
            return previousPhrase;
        } else
        {
            return userInput;
        }
    }

    private int getCartLimit(String configurationChoiceParameter, int currentLimit)
    {
        int limit = currentLimit;
        if (configurationChoiceParameter!=null && isInteger(configurationChoiceParameter))
        {
            limit = Integer.parseInt(configurationChoiceParameter);
            if (limit < 0)
            {
                limit = 0;
            } else if (limit >= 125)
            {
                limit = 124;
            }
        }
        return limit;
    }

    private int getAdjustmentValue(String configurationChoiceParameter, int currentValue)
    {
        int value = currentValue;
        if (configurationChoiceParameter!=null && isInteger(configurationChoiceParameter))
        {
            value = Integer.parseInt(configurationChoiceParameter);
        }
        return value;
    }

    private boolean isInteger(String evaluationTarget)
    {
        try {  
            Integer.parseInt(evaluationTarget);  
            return true;
          } catch(NumberFormatException e)
          {  
            return false;  
          }
    }

}
