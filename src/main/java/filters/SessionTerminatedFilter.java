package filters;

import java.util.Optional;

import com.google.inject.Inject;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

/**
 * This {@link Filter} breaks, if the session is terminated.
 * 
 * @author sebastianloob
 */
public class SessionTerminatedFilter implements Filter
{

    @Inject
    Messages msg;

    private final Optional<String> language = Optional.of("en");

    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        // break, if session is terminated
        if (context.getSession().isEmpty())
        {
            // show error message
            context.getFlashScope().error(msg.get("errorSessionTerminated", language).get());
            // show home page
            return Results.redirect(context.getContextPath() + "/");
        }
        // everything is fine
        else
        {
            return chain.next(context);
        }
    }
}
