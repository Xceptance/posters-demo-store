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
package models_backoffice;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.ebean.Ebean;

public class UserTest extends NinjaTest
{

    Backofficeuser backofficeuser;

    @Before
    public void setUp() throws Exception
    {
        // create new user
        backofficeuser = new Backofficeuser();
        // set some data
        backofficeuser.setEmail("email");
        backofficeuser.hashPasswd("password");
        // persist
        backofficeuser.save();
    }

    @Test
    public void testCreateUser()
    {
        final Backofficeuser backofficeuser = new Backofficeuser();
        backofficeuser.setEmail("email");
        backofficeuser.hashPasswd("password");
        backofficeuser.save();

        final Backofficeuser savedUser = Ebean.find(Backofficeuser.class, backofficeuser.getId());
        // verify, that the user is persistent
        Assert.assertNotNull(savedUser);
        // verify the email of the user
        Assert.assertEquals("email", savedUser.getEmail());
    }

    @Test
    public void testPasswordHash()
    {
        final Backofficeuser user = Ebean.find(Backofficeuser.class, this.backofficeuser.getId());
        // verify, that the password is not saved as plain text
        Assert.assertNotSame("password", user.getPassword());
        // verify the password
        Assert.assertTrue(user.checkPasswd("password"));
    }

}
