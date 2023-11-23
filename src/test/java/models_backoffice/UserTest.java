package models_backoffice;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

public class UserTest extends NinjaTest
{

    User user;

    @Before
    public void setUp() throws Exception
    {
        // create new user
        user = new User();
        // set some data
        user.setEmail("email");
        user.hashPasswd("password");
        // persist
        user.save();
    }

    @Test
    public void testCreateUser()
    {
        final User user = new User();
        user.setEmail("email");
        user.hashPasswd("password");
        user.save();

        final User savedUser = Ebean.find(User.class, user.getId());
        // verify, that the user is persistent
        Assert.assertNotNull(savedUser);
        // verify the email of the user
        Assert.assertEquals("email", savedUser.getEmail());
    }

    @Test
    public void testPasswordHash()
    {
        final User user = Ebean.find(User.class, this.user.getId());
        // verify, that the password is not saved as plain text
        Assert.assertNotSame("password", user.getPassword());
        // verify the password
        Assert.assertTrue(user.checkPasswd("password"));
    }

}
