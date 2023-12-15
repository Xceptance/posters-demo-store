package models_backoffice;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Ebean;

/**
 * This {@link Entity} provides a user of the poster store. Each user must have a unique email address and a
 * password to log in. A user has the access to the backoffice of the poster store.
 * 
 * @author kennygozali
 */
@Entity
@Table(name = "user")
public class User
{

    /*
     * The {@link UUID} of the entity.
     */
    @Id
    private UUID id;

    /**
     * The email address to log in.
     */
    private String email;

    /**
     * The password to log in.
     */
    private String password;

    /**
     * The last name of the user.
     */
    private String name;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * Constructor.
     */
    public User()
    {
    }

    /**
     * Returns the {@link UUID} of the entity.
     * 
     * @return the {@link UUID} of the entity
     */
    public UUID getId()
    {
        return id;
    }

    /**
     * Sets the {@link UUID} of the entity.
     * 
     * @param userId
     *            the {@link UUID} of the entity
     */
    public void setId(final UUID userId)
    {
        id = userId;
    }

    /**
     * Returns the user's email address.
     * 
     * @return the user's email address
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the user's email address.
     * 
     * @param email
     *                  the user's email address
     */
    public void setEmail(final String email)
    {
        this.email = email;
    }

    /**
     * Returns the user's hashed password.
     * 
     * @return the user's hashed password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the user's password. Only use this, if the password is hashed before.
     * 
     * @param password
     *                     the user's password
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /**
     * Returns the user's last name.
     * 
     * @return the user's last name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the user's last name.
     * 
     * @param name
     *                 the user's last name
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the user's first name.
     * 
     * @return the user's first name
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Sets the user's first name.
     * 
     * @param firstName
     *                      the user's first name
     */
    public void setFirstName(final String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * Hashes the given password and sets to the current password.
     * 
     * @param password
     *                     the password to hash
     */
    public void hashPasswd(final String password)
    {
        setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    }

    /**
     * Returns {@code true}, if the given password is equal to the user's password; otherwise {@code false}.
     * 
     * @param password
     *                     the password to check
     * @return {@code true} if the password matches, otherwise {@code false}
     */
    public boolean checkPasswd(final String password)
    {
        return BCrypt.checkpw(password, this.password);
    }

    /**
     * Updates the entity in the database.
     */
    public void update()
    {
        Ebean.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Deletes the entity from the database.
     */
    public void delete()
    {
        Ebean.delete(this);
    }

    /**
     * Returns {@code true}, if there is a user with the given email address, otherwise {@code false}.
     * 
     * @param email
     *              the email address to check
     * @return {@code true} if the email address exist, otherwise {@code false}
     */
    public static boolean emailExist(final String email)
    {
        boolean exist = true;
        final User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        // no user has this email address
        if (user == null)
        {
            exist = false;
        }
        return exist;
    }

    /**
     * Returns the {@link User} by the user's email address.
     * 
     * @param email
     *            the user's email address
     * @return the {@link User} with the given email address
     */
    public static User getUserByEmail(final String email)
    {
        // get user by email address
        return Ebean.find(User.class).where().eq("email", email).findUnique();
    }

    /**
     * Returns the {@link User} by the user's id.
     * 
     * @param userId
     *            the {@link UUID} of the user
     * @return the {@link User} with the given {@link UUID}
     */
    public static User getUserById(final UUID userId)
    {
        // get user by id
        return Ebean.find(User.class, userId);
    }
}
