package controllers;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import models.Customer;
import models.Product;
import models.TopCategory;
import ninja.lifecycle.Start;

import org.h2.tools.RunScript;

import util.starter.StarterConf;
import util.xml.ImportFromXMLToDB;

import com.avaje.ebean.Ebean;
import com.google.inject.Singleton;

@Singleton
public class JobController
{

    @Start(order = 90)
    public void startActions() throws Exception
    {
        // set some system properties
        System.setProperty("starter.home", "src/main/java");
        System.setProperty("ninja.external.configuration", "conf/application.conf");
        System.setProperty("ninja.mode", "prod");

        StarterConf config = new StarterConf();
        prepareDatabase(config);
        // import categories, products and dummy customer
        importData(config);
    }

    private void prepareDatabase(StarterConf config)
    {
        // handle the connection
        Connection connection;
        try
        {
            Class.forName(config.DB_DRIVER);
            // connect to database
            connection = DriverManager.getConnection(config.DB_URL, config.DB_USER, config.DB_PASS);
            boolean basketProductTable = false;
            boolean basketTable = false;
            boolean billingAddressTable = false;
            boolean creditCardTable = false;
            boolean customerTable = false;
            boolean deliveryAddressTable = false;
            boolean orderProductTable = false;
            boolean orderTable = false;
            boolean productTable = false;
            boolean topCategoryTable = false;
            boolean subCategoryTable = false;
            // if server has been started with the parameter "-Dstarter.droptables=true"
            // drop all tables
            if (config.DROP_TABLES != null)
            {
                if (config.DROP_TABLES.equals("true"))
                {
                    RunScript.execute(connection, new FileReader("default-drop.sql"));
                }
            }
            // get the DB-metadata
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            // check if all tables exist
            while (rs.next())
            {
                if (rs.getString(3).equals("CARTPRODUCT"))
                {
                    basketProductTable = true;
                }
                if (rs.getString(3).equals("CART"))
                {
                    basketTable = true;
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
                    deliveryAddressTable = true;
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
            }
            // create the tables if they not exist
            if (!(basketProductTable && basketTable && billingAddressTable && creditCardTable && customerTable
                  && deliveryAddressTable && orderProductTable && orderTable && productTable && topCategoryTable && subCategoryTable))
            {
                // simply execute the create-script
                RunScript.execute(connection, new FileReader("default-create.sql"));
            }
            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void importData(StarterConf config)
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
    }
}
