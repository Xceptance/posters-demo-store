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
package models;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.ebean.Ebean;

public class CreditCardTest extends NinjaTest
{
    CreditCard card;

    @Before
    public void setUp() throws Exception
    {
        card = new CreditCard();
        card.setCardNumber("1234567890123456");
        card.save();
    }

    @Test
    public void testGetCardNumberCryptic()
    {
        final CreditCard creditCard = Ebean.find(CreditCard.class, card.getId());
        Assert.assertEquals("xxxx xxxx xxxx 3456", creditCard.getCardNumberCryptic());
    }

}
