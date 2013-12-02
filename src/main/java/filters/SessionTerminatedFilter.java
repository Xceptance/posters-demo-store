package filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import util.session.SessionHandling;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SessionTerminatedFilter implements Filter
{

    @Inject
    Messages msg;

    private Optional language = Optional.of("en");

    @Override
    public Result filter(FilterChain chain, Context context)
    {
        // break, if session is terminated
        if (context.getSessionCookie().isEmpty())
        {
            // error message
            context.getFlashCookie().error(msg.get("errorSessionTerminated", language).get());
            // show home page
            return Results.redirect(context.getContextPath() + "/");
        }
        else
        {
            return chain.next(context);
        }
    }
}
