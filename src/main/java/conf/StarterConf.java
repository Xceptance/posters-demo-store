package conf;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Collection of some constants, which are used to start the application.
 * 
 * @author sebastianloob
 */
public class StarterConf
{

    /**
     * The server file-location.
     */
    public final String HOME;

    /**
     * The http port, the application listens on.
     */
    public final Integer PORT;

    /**
     * The host address of the application.
     */
    public final String HOST;

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
     * The base-directory-path on the server.
     */
    public final String CONTEXT_PATH;

    /**
     * Import a dummy customer, if it is set true.
     */
    public final String IMPORT_CUSTOMER;

    /**
     * Drop all tables, if it is set true.
     */
    public final String DROP_TABLES;

    private final Configuration conf;

    public StarterConf() throws Exception
    {
        // get the server file-location
        HOME = System.getProperty("starter.home");
        // get the config file-location
        final String confFile = System.getProperty("ninja.external.configuration");
        final PropertiesConfiguration cfg = new PropertiesConfiguration();
        cfg.setEncoding("utf-8");
        cfg.setDelimiterParsingDisabled(true);
        final String confPath = confFile;
        // try to load the config
        cfg.load(confPath);
        conf = cfg;
        DB_URL = conf.getString("ebean.datasource.databaseUrl");
        DB_USER = conf.getString("ebean.datasource.username");
        DB_PASS = conf.getString("ebean.datasource.password");
        DB_DRIVER = conf.getString("ebean.datasource.databaseDriver");
        HOST = conf.getString("application.url");
        PORT = conf.getInt("application.port.http");
        CONTEXT_PATH = conf.getString("application.basedir");
        IMPORT_CUSTOMER = conf.getString("starter.importCustomer");
        DROP_TABLES = conf.getString("starter.droptables");
    }
}
