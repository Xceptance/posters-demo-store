/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers_backoffice;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

import io.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;
import filters.SessionUserExistFilter;
import controllers.WebShopController;
import models_backoffice.Backofficeuser;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.session.FlashScope;
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

    // Variable to store dark mode state (a simple example; might want to use a database or cache)
    private static final String DARK_MODE_SESSION_KEY = "darkMode";
    private static boolean isDarkModeEnabled = false;
    
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
            final boolean emailExist = Backofficeuser.emailExist(email);
            // get user by the given email
            final Backofficeuser user = Backofficeuser.getUserByEmail(email);
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
                context.getFlashScope().error(msg.get("errorIncorrectPassword", language).get());

                //return Results.redirect(context.getContextPath() + "/posters/backoffice/login");

            }
            // wrong email
            else
            {
                // error message
                context.getFlashScope().error(msg.get("errorEmailExist", language).get());

                //return Results.redirect(context.getContextPath() + "/posters/backoffice/login");

            }
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show entered email address
        data.put("email", email);
        // show page to log-in again
        return Results.redirect(context.getContextPath() + "/posters/backoffice/login");
        //return Results.html().render(data).template(xcpConf.TEMPLATE_LOGIN_FORM_BO);
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        // Retrieve the dark mode state from the session and parse it to a boolean
        String darkModeString = context.getSession().get(DARK_MODE_SESSION_KEY);
        isDarkModeEnabled = Boolean.parseBoolean(darkModeString);

        // Pass the dark mode state to the template
        result.render("isDarkModeEnabled", isDarkModeEnabled);
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
                                        @Param("eMail") final String email, @Param("password") final String password, final Context context, FlashScope flashScope)
    {
        boolean failure = false;
        // account with this email already exist
        if (!Ebean.find(Backofficeuser.class).where().eq("email", email).findList().isEmpty())
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
            flashScope.error("Invalid Email, or Email exists already");
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user/create");
        }
        // all input fields might be correct
        else
        {
            // create new user
            final Backofficeuser user = new Backofficeuser();
            user.setName(name);
            user.setFirstName(firstName);
            user.setEmail(email);
            user.hashPasswd(password);
            // save user
            Ebean.save(user);
            // show success message
            context.getFlashScope().success(msg.get("successCreateAccount", language).get());
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user");
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
        return Results.html().template("views_backoffice/UserController/loginForm.ftl.html");
    }
}
