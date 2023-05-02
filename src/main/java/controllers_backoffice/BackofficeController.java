package controllers_backoffice;

import java.util.Optional;


import filters.SessionUserExistFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;


/**
 * Backoffice Controller, it controls the behaviour of the backoffice.
 * Must be signed in as a backoffice user in order to access any pages within the backoffice.
 * 
 * @author kennygozali
 */

public class BackofficeController {

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns the homepage of the Backoffice.
     * 
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result homepage(final Context context)
    {
        return Results.html();
    }
}
