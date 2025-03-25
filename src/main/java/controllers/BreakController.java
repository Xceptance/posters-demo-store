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
import ninja.params.Param;

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
    public  Result statusUpdate(final Context context, @PathParam("urlLocale") String locale, @Param("breakCategories") String breakCategories)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);

        if (breakCategories!=null && breakCategories.equals("on"))
        {
            stsConf.setCategoriesBroken(true);
        }
        else
        {
            stsConf.setCategoriesBroken(false);
        }

        return Results.redirect(context.getContextPath() + "/" + locale + "/ok3ok2ru8udqx7gZGS9n/statusInfo");
    }
}
