package util.session;

import java.util.UUID;


import ninja.Context;

/**
 * Provides methods for the session handling in the backoffice.
 * 
 * @author kennygozali
 */
public class SessionHandling_Backoffice {

    // The keys of session cookie values.

    private static String USER = "backoffice user";

    /**
     * Adds user id to the session.
     * 
     * @param context
     * @param userId
     */
    public static void setUserId(final Context context, final UUID userId)
    {
        context.getSession().put(USER, userId.toString());
    }
}
