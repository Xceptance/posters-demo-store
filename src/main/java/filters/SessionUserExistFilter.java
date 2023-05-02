package filters;

import java.util.Optional;

import com.google.inject.Inject;


import models_backoffice.User;
import conf.PosterConstants;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Filter;
import ninja.FilterChain;
import util.session.SessionHandling;

/**
 * This {@link Filter} breaks, if the user doesn't exist within the session.
 * 
 * @author kennygozali
 */
public class SessionUserExistFilter implements Filter
{
    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        if (!SessionHandling.isUserLogged(context)) {
            // return Results.redirect(context.getContextPath() + "/"); // redirects to homepage
            return Results.forbidden().html().template("/views/system/403forbidden.ftl.html");
        }
        return chain.next(context);
    }
}
