package controllers_backoffice;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;
import filters.SessionUserExistFilter;
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
public class UserController
{

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
                return Results.redirect(context.getContextPath() + "/posters/backoffice");

            }
            // user exist, wrong password
            else if (emailExist && !correctPassowrd)
            {
                // error message
                return Results.redirect(context.getContextPath() + "/posters/backoffice/login");

            }
            // wrong email
            else
            {
                // error message
                return Results.redirect(context.getContextPath() + "/posters/backoffice/login");

            }
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show entered email address
        data.put("email", email);
        // show page to log-in again
        return Results.html().render(data).template(xcpConf.TEMPLATE_LOGIN_FORM);
    }

    /**
     * Returns the page to create a new account.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result registration(final Context context)
    {   
        Result result = Results.html();
        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Creates a new customer account.
     * 
     * @param name
     * @param firstName
     * @param email
     * @param password
     * @param passwordAgain
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result registrationCompleted(@Param("lastName") final String name, @Param("firstName") final String firstName,
                                        @Param("eMail") final String email, @Param("password") final String password, final Context context)
    {
        boolean failure = false;
        // account with this email already exist
        if (!Ebean.find(User.class).where().eq("email", email).findList().isEmpty())
        {
            // show error message
            failure = true;
        }
        // email is not valid
        else if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            failure = true;
        }
        if (failure)
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice/registration");
        }
        // all input fields might be correct
        else
        {
            // create new user
            final User user = new User();
            user.setName(name);
            user.setFirstName(firstName);
            user.setEmail(email);
            user.hashPasswd(password);
            // save user
            Ebean.save(user);
            // show success message
            context.getFlashScope().success(msg.get("successCreateAccount", language).get());
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }
    }

    /**
     * Logs off from the system. Returns the home page.
     * 
     * @param context
     * @return
     */
    public Result logout(final Context context)
    {
        // remove user from session
        SessionHandling.removeUserId(context);
        // show home page
        context.getFlashScope().success(msg.get("successLogOut", language).get());
        return Results.redirect(context.getContextPath() + "/");
    }
}
