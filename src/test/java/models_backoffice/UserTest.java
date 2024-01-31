package models_backoffice;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

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
