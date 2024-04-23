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
package filters;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Filter;
import ninja.FilterChain;
import util.session.SessionHandling;

/**
 * This {@link Filter} breaks, if the user doesn't exist within the session.
 * 
 * @author kennygozali
 */
public class SessionUserExistFilter implements Filter
{
    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        if (!SessionHandling.isUserLogged(context)) {
            // return Results.redirect(context.getContextPath() + "/"); // redirects to homepage
            return Results.forbidden().html().template("/views_backoffice/UserController/loginForm.ftl.html");
        }
        return chain.next(context);
    }
}
