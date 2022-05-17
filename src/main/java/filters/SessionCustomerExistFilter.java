package filters;

import java.util.Optional;

import com.google.inject.Inject;

import models.Customer;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import util.session.SessionHandling;

/**
 * This {@link Filter} breaks, if a customer is set in the session, but the customer doesn't exist in the database.
 * 
 * @author sebastianloob
 */
public class SessionCustomerExistFilter implements Filter
{

    @Inject
    Messages msg;

    private final Optional<String> language = Optional.of("de");

    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        // customer is logged in
        if (SessionHandling.isCustomerLogged(context))
        {
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // customer does not exist
            if (customer == null)
            {
                // remove customer from session
                SessionHandling.removeCustomerId(context);
                // show error message
                context.getFlashScope().error(msg.get("errorSessionTerminated", language).get());
                // show home page
                return Results.redirect(context.getContextPath() + "/");
            }
        }
        return chain.next(context);
    }
}
