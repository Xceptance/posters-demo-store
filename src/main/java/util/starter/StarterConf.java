package util.starter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

public class StarterConf
{

    public final String HOME;

    public final Integer PORT;

    public final String HOST;

    public final String DB_URL;

    public final String DB_USER;

    public final String DB_PASS;

    public final String DB_DRIVER;

    public final String CONTEXT_PATH;

    public final String IMPORT_CUSTOMER;

    public final String DROP_TABLES;

    private Configuration conf;

    public StarterConf() throws Exception
    {
        // get the server file-location
        HOME = System.getProperty("starter.home");

        // get the config file-location
        String confFile = System.getProperty("ninja.external.configuration");
        PropertiesConfiguration cfg = new PropertiesConfiguration();
        cfg.setEncoding("utf-8");
        cfg.setDelimiterParsingDisabled(true);
        String confPath = HOME + "/" + confFile;
        // try to load the config
        cfg.load(confPath);
        conf = (Configuration) cfg;
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
