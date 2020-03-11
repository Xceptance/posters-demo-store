package filters;

import models.Order;
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
 * This {@link Filter} breaks, if an order-ID is set in the session, but the order doesn't exist in the database.
 * 
 * @author sebastianloob
 */
public class SessionOrderExistFilter implements Filter
{

    @Inject
    Messages msg;

    private final Optional<String> language = Optional.of("en");

    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        // an order-id is set
        if (SessionHandling.isOrderIdSet(context))
        {
            final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
            // order does not exist
            if (order == null)
            {
                // remove order from session
                SessionHandling.removeOrderId(context);
                // show error message
                context.getFlashScope().error(msg.get("errorSessionTerminated", language).get());
                // show home page
                return Results.redirect(context.getContextPath() + "/");
            }
        }
        return chain.next(context);
    }
}
