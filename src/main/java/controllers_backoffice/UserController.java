package controllers_backoffice;


import java.util.Map;
import java.util.HashMap;


import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;



import conf.PosterConstants;
import controllers.WebShopController;
import ninja.Context;
import ninja.i18n.Messages;
import ninja.Result;
import ninja.Results;


public class UserController {

    @Inject
    PosterConstants xcpConf;

    /**
     * Returns a page to log in to the user backend.
     * 
     * @param context
     * @return
     */

    public Result loginForm(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }
}
