package filters;

import ninja.Filter;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import ninja.FilterChain;
import io.ebean.Ebean;
import models.Language;
import ninja.Context;
import ninja.Cookie;
import ninja.i18n.*;

import java.util.Arrays;

import com.google.inject.Inject;

public class LocaleFilter implements Filter {

    @Inject
    Lang lang;

    public Result filter(FilterChain chain, Context context)
    {
        Result result = chain.next(context);
        // A Check URL for urlLocale variable
        String urlLoc = context.getPathParameter("urlLocale");
        // B Check cookies for NINJA_LANG
        Cookie langCookie = context.getCookie("NINJA_LANG");
        String cookieLoc;
        if(langCookie != null) {
            cookieLoc = langCookie.getValue();
        }
        else {
            cookieLoc = "en-US";
        }
        
        // Compare A and B if present, prefer A if different
        if (cookieLoc.equals(urlLoc)) 
        {
            // continue without action if same 
            // (if cookie is null something is wrong anyways and we would almost certainly get an error no matter what we do here
            // since (even during first load) it is auto-filled from the accept header before we arrive here if unset
            // we can leave that to ninja error handling)
        }
        else
        {
            if (urlLoc == null) 
            {
                // continue without action, we must trust ninja here since we have no other info
            }
            else
            {
                if (checkExistance(urlLoc))
                {
                    // set locale to A
                    lang.setLanguage(urlLoc, result);
                }
                else
                {
                    String url = context.getRoute().getUri();
                    // modify url
                    String defaultUrl = url.replace(urlLoc, "en-US");
                    // redirect to default (en-US)
                    result = Results.redirect(defaultUrl);
                }
                
            }
        }
        
        return result;
    }

    @Inject
    NinjaProperties ninjaProperties;

    private boolean checkExistance(String locale)
    {
        // Check if locale is supported
        String rawProperty = ninjaProperties.get("application.languages");
        String[] supportedLocs = rawProperty.split(",");
        boolean supported = Arrays.stream(supportedLocs).anyMatch(locale::equals);
        if(supported == false)
        {
            return false;
        }
        else
        {
            // Check if locale exists in database
            Language language = Ebean.find(Language.class).where().eq("code", locale).findOne();
            if(language == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }        
    }
}
