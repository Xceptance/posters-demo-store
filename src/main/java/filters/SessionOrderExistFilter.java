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

import models.Order;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import util.session.SessionHandling;

/**
 * This {@link Filter} breaks, if an order-ID is set in the session, but the order doesn't exist in the database.
 * 
 * @author sebastianloob
 */
public class SessionOrderExistFilter implements Filter
{

    @Inject
    Messages msg;

    private final Optional<String> language = Optional.of("en");

    @Override
    public Result filter(final FilterChain chain, final Context context)
    {
        // an order-id is set
        if (SessionHandling.isOrderIdSet(context))
        {
            final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
            // order does not exist
            if (order == null)
            {
                // remove order from session
                SessionHandling.removeOrderId(context);
                // show error message
                context.getFlashScope().error(msg.get("errorSessionTerminated", language).get());
                // show home page
                return Results.redirect(context.getContextPath() + "/");
            }
        }
        //Return to Homepage if Order ID UnSet, Prevents User from going back after placing orders.
        if (!SessionHandling.isOrderIdSet(context))
        {
            context.getFlashScope().error(msg.get("errorOrderSessionTerminated", language).get());
            // show home page
            return Results.redirect(context.getContextPath() + "/");
        }

        return chain.next(context);
    }
}
