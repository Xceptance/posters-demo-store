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

public class SessionCustomerFilter implements Filter
{

    @Inject
    Messages msg;

    private Optional language = Optional.of("en");

    @Override
    public Result filter(FilterChain chain, Context context)
    {
        // break, if no customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            return chain.next(context);
        }
        else
        {
            // error message
            context.getFlashCookie().error(msg.get("errorNoLoggedCustomer", language).get());
            // show home page
            return Results.redirect(context.getContextPath() + "/");
        }
    }
}
