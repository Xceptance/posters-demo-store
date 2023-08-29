/*
 * Copyright (c) 2013-2023 Xceptance Software Technologies GmbH
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
