package filters;

import java.util.HashMap;
import java.util.Map;

import ninja.Filter;
import ninja.Result;
import ninja.Results;
import ninja.FilterChain;
import ninja.Context;

import com.google.inject.Inject;

import conf.StatusConf;

public class AdjustStateFilter implements Filter
{
    @Inject
    StatusConf config;
    
    public Result filter(FilterChain chain, Context context)
    {
        Result result = chain.next(context);
        final Map<String, Object> status = new HashMap<String, Object>();
        String locale = context.getPathParameter("urlLocale");
        // check status Info
        config.getStatus(status);
        // react to status
        // category adjustment is active
        if (status.get("categoriesBroken").equals(true))
        {
            result = categoryState(context, locale, result);
        }
        
        
        return result;
    }

    private Result categoryState(Context context, String locale, Result result)
    {
        String categoryId = context.getParameter("categoryId");
            if (categoryId == null || categoryId.isBlank()) 
            {
                // not a category we have nothing to do here
            }
            else
            {
                int cat;
                try
                {
                    cat = Integer.parseInt(categoryId);
                }
                catch (Exception e)
                {
                    // if this fails it means it could not be resolved to a number if someone
                    // put something NaN as a category ID we send them back to the homepage
                    return Results.redirect("/"+locale);
                }
            
                String topcat = context.getPathParameter("topCategory");
                String subcat = context.getPathParameter("subCategory");
                if (topcat == null || topcat.isBlank())
                {
                    // we do not look at a topcategory
                    if (subcat == null || subcat.isBlank())
                    {
                        // there are no other categories beside top and sub
                    }
                    else 
                    {
                        cat++;
                        if (cat > 13)
                        {
                            cat = 1;
                        }
                        result = Results.redirect("/"+locale+"/category/?categoryId="+cat);
                    }
                } 
                else
                {
                    cat++;
                    if (cat > 4)
                    {
                        cat = 1;
                    }
                    result = Results.redirect("/"+locale+"/topCategory/?categoryId="+cat);
                }
                // TODO: adjust context if possible instead of firing redirect
            }    
            return result;
    }
    
}
