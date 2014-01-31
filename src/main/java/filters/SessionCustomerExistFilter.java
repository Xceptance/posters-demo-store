package filters;

import models.Customer;
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
 * This {@link Filter} breaks, if a customer is set in the session, but the customer doesn't exist in the database.
 * 
 * @author sebastianloob
 */
public class SessionCustomerExistFilter implements Filter
{

    @Inject
    Messages msg;

    private Optional<String> language = Optional.of("en");

    @Override
    public Result filter(FilterChain chain, Context context)
    {
        // customer is logged in
        if (SessionHandling.isCustomerLogged(context))
        {
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // customer does not exist
            if (customer == null)
            {
                // remove customer from session
                SessionHandling.removeCustomerId(context);
                // show error message
                context.getFlashCookie().error(msg.get("errorSessionTerminated", language).get());
                // show home page
                return Results.redirect(context.getContextPath() + "/");
            }
        }
        return chain.next(context);
    }
}
