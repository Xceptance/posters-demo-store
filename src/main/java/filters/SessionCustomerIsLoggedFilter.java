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

/**
 * This {@link Filter} breaks, if no customer is logged in the current session.
 * 
 * @author sebastianloob
 */
public class SessionCustomerIsLoggedFilter implements Filter
{

    @Inject
    Messages msg;

    private final Optional<String> language = Optional.of("en");

    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        // customer is logged, everything is fine
        if (SessionHandling.isCustomerLogged(context))
        {
            return chain.next(context);
        }
        // break, if no customer is logged
        else
        {
            // show error message
            context.getFlashScope().error(msg.get("errorNoLoggedCustomer", language).get());
            // show home page
            return Results.redirect(context.getContextPath() + "/");
        }
    }
}
