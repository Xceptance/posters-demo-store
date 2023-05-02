package controllers_backoffice;


import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;


import com.google.inject.Inject;




import conf.PosterConstants;
import filters.SessionCustomerExistFilter;
import controllers.WebShopController;
import models_backoffice.User;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import util.session.SessionHandling;


/**
 * Controller class, that provides the user functionality.
 * 
 * @author kennygozali
 */
public class UserController {

    @Inject
    Messages msg;
    
    @Inject
    PosterConstants xcpConf;

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns a page to log in to the user backend.
     * 
     * @param context
     * @return
     */

    public Result loginForm(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Logs in to the system with email and password. Returns the home page, if the email and the password are correct,
     * otherwise the page to log-in again.
     * 
     * @param email
     * @param password
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result login(@Param("email") final String email, @Param("password") final String password, final Context context)
    {
        // email is not valid
        if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorValidEmail", language).get());
        }
        else
        {
            // exists the given email in the database
            final boolean emailExist = User.emailExist(email);
            // get user by the given email
            final User user = User.getUserByEmail(email);
            // is the password correct
            boolean correctPassowrd = false;
            // check password, if the email exist
            if (emailExist)
            {
                correctPassowrd = user.checkPasswd(password);
            }
            // email and password are correct
            if (emailExist && correctPassowrd)
            {
                // put user id to session
                SessionHandling.setUserId(context, user.getId());
                // show backoffice
                context.getFlashScope().success(msg.get("successLogIn", language).get());
                return Results.redirect(context.getContextPath() + "/backoffice/");

            }
            // user exist, wrong password
            else if (emailExist && !correctPassowrd)
            {
                // error message
                context.getFlashScope().error(msg.get("errorIncorrectPassword", language).get());
            }
            // wrong email
            else
            {
                // error message
                context.getFlashScope().error(msg.get("errorEmailExist", language).get());
            }
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show entered email address
        data.put("email", email);
        // show page to log-in again
        return Results.html().render(data).template(xcpConf.TEMPLATE_LOGIN_FORM);
    }
}
