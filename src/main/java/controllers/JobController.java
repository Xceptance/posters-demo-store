package controllers;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import models.Customer;
import models.Product;
import models.TopCategory;
import models_backoffice.User;
import ninja.lifecycle.Start;

import org.h2.tools.RunScript;

import util.xml.ImportFromXMLToDB;

import com.avaje.ebean.Ebean;
import com.google.inject.Singleton;

import conf.StarterConf;

/**
 * Executes some actions, before the application starts.
 * 
 * @author sebastianloob
 */
@Singleton
public class JobController
{

    /**
     * Prepares the database and, if necessary, imports some data to the database.
     * 
     * @throws Exception
     */
    @Start(order = 90)
    public void startActions() throws Exception
    {
        // set some system properties
        System.setProperty("starter.home", "src/main/java");
        System.setProperty("ninja.external.configuration", "conf/application.conf");
        System.setProperty("ninja.mode", "prod");

        final StarterConf config = new StarterConf();
        // prepare database
        prepareDatabase(config);
        // import categories, products, dummy customer and user
        importData(config);
    }

    /**
     * Prepares the database. Drops all tables, if it is set. Creates a new database, if necessary.
     * 
     * @param config
     */
    private void prepareDatabase(final StarterConf config)
    {
        // handle the connection
        Connection connection;
        try
        {
            Class.forName(config.DB_DRIVER);
            // connect to database
            connection = DriverManager.getConnection(config.DB_URL, config.DB_USER, config.DB_PASS);
            boolean cartProductTable = false;
            boolean cartTable = false;
            boolean billingAddressTable = false;
            boolean creditCardTable = false;
            boolean customerTable = false;
            boolean shippingAddressTable = false;
            boolean orderProductTable = false;
            boolean orderTable = false;
            boolean productTable = false;
            boolean topCategoryTable = false;
            boolean subCategoryTable = false;
            boolean userTable = false;
            // if server has been started with the parameter "-Dstarter.droptables=true"
            // drop all tables
            if (config.DROP_TABLES != null)
            {
                if (config.DROP_TABLES.equals("true"))
                {
                    RunScript.execute(connection,
                                      new InputStreamReader(getClass().getClassLoader().getResourceAsStream("conf/default-drop.sql")));
                }
            }
            // get the DB-metadata
            final DatabaseMetaData md = connection.getMetaData();
            final ResultSet rs = md.getTables(null, null, "%", null);
            // check if all tables exist
            while (rs.next())
            {
                if (rs.getString(3).equals("CARTPRODUCT"))
                {
                    cartProductTable = true;
                }
                if (rs.getString(3).equals("CART"))
                {
                    cartTable = true;
                }
                if (rs.getString(3).equals("BILLINGADDRESS"))
                {
                    billingAddressTable = true;
                }
                if (rs.getString(3).equals("CREDITCARD"))
                {
                    creditCardTable = true;
                }
                if (rs.getString(3).equals("CUSTOMER"))
                {
                    customerTable = true;
                }
                if (rs.getString(3).equals("SHIPPINGADDRESS"))
                {
                    shippingAddressTable = true;
                }
                if (rs.getString(3).equals("ORDERPRODUCT"))
                {
                    orderProductTable = true;
                }
                if (rs.getString(3).equals("ORDERING"))
                {
                    orderTable = true;
                }
                if (rs.getString(3).equals("PRODUCT"))
                {
                    productTable = true;
                }
                if (rs.getString(3).equals("TOPCATEGORY"))
                {
                    topCategoryTable = true;
                }
                if (rs.getString(3).equals("SUBCATEGORY"))
                {
                    subCategoryTable = true;
                }
                if (rs.getString(3).equals("USER"))
                {
                    userTable = true;
                }
            }
            // create the tables if they not exist
            if (!(cartProductTable && cartTable && billingAddressTable && creditCardTable && customerTable && shippingAddressTable &&
                  orderProductTable && orderTable && productTable && topCategoryTable && subCategoryTable && userTable))
            {
                // simply execute the create-script
                RunScript.execute(connection,
                                  new InputStreamReader(getClass().getClassLoader().getResourceAsStream("conf/default-create.sql")));
            }
            connection.close();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Imports some data, if the database is empty.
     * 
     * @param config
     */
    private void importData(final StarterConf config)
    {
        // import categories, if there is no category in DB
        if (Ebean.find(TopCategory.class).findList().size() == 0)
        {
            ImportFromXMLToDB.importCategory();
        }
        // import products, if there is no product in DB
        if (Ebean.find(Product.class).findList().size() == 0)
        {
            ImportFromXMLToDB.importProduct();
        }
        // import dummy customer, if the parameter is set and no other customer is in DB
        if (config.IMPORT_CUSTOMER != null)
        {
            if (config.IMPORT_CUSTOMER.equals("true") && Ebean.find(Customer.class).findList().size() == 0)
            {
                ImportFromXMLToDB.importCustomer();
            }
        }
        // import users, if there is no user in DB
        if (Ebean.find(User.class).findList().size() == 0)
        {
            ImportFromXMLToDB.importUser();
        }
    }
}
