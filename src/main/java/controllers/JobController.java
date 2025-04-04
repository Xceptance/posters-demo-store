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
package controllers;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import models.Customer;
import models.Language;
import models.Product;
import models.TopCategory;
import models_backoffice.Backofficeuser;
import ninja.lifecycle.Start;
import javax.inject.Inject;

import org.h2.tools.RunScript;

import io.ebean.Ebean;
import com.google.inject.Singleton;

import conf.StarterConf;
import util.xml.ImportFromXMLToDB;

/**
 * Executes some actions, before the application starts.
 * 
 * @author sebastianloob
 */
@Singleton
public class JobController
{
    @Inject
    StarterConf config;

    /**
     * Prepares the database and, if necessary, imports some data to the database.
     * 
     * @throws Exception
     */
    @Start(order = 90)
    public void startActions() throws Exception
    {
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
            if (config.DROP_TABLES)
            {
                RunScript.execute(connection,
                                  new InputStreamReader(getClass().getClassLoader().getResourceAsStream("conf/default-drop.sql")));
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
                if (rs.getString(3).equals("BACKOFFICEUSER"))
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
        // import languages, if there are none in the DB
        if (Ebean.find(Language.class).findList().size() == 0)
        {
            ImportFromXMLToDB.importLanguage();
        }
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
        if (config.IMPORT_CUSTOMER && Ebean.find(Customer.class).findList().size() == 0)
        {
            ImportFromXMLToDB.importCustomer();
        }
        // import users, if there is no user in DB
        if (Ebean.find(Backofficeuser.class).findList().size() == 0)
        {
            ImportFromXMLToDB.importUser();
        }
    }
}
