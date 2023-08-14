package conf;

import javax.inject.Inject;

import ninja.utils.NinjaProperties;

/**
 * Collection of some constants, which are used to start the application.
 * 
 * @author sebastianloob
 */
public class StarterConf
{
    /**
     * The URL of the database.
     */
    public final String DB_URL;

    /**
     * The username of the database.
     */
    public final String DB_USER;

    /**
     * The user's password.
     */
    public final String DB_PASS;

    /**
     * The driver to connect to the database.
     */
    public final String DB_DRIVER;

    /**
     * Import a dummy customer, if it is set true.
     */
    public final boolean IMPORT_CUSTOMER;

    /**
     * Drop all tables, if it is set true.
     */
    public final boolean DROP_TABLES;

    @Inject
    public StarterConf(final NinjaProperties ninjaProperties)
    {
        DB_URL = ninjaProperties.getOrDie("ebean.datasource.databaseUrl");
        DB_USER = ninjaProperties.getOrDie("ebean.datasource.username");
        DB_PASS = ninjaProperties.getOrDie("ebean.datasource.password");
        DB_DRIVER = ninjaProperties.getOrDie("ebean.datasource.databaseDriver");
        IMPORT_CUSTOMER = ninjaProperties.getBooleanOrDie("starter.importCustomer");
        DROP_TABLES = ninjaProperties.getBooleanOrDie("starter.droptables");
    }
}
