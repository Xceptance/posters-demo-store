package controllers;

import models.Product;
import models.TopCategory;
import ninja.lifecycle.Start;
import util.xml.ImportFromXMLToDB;

import com.avaje.ebean.Ebean;
import com.google.inject.Singleton;

@Singleton
public class JobController
{

    @Start(order = 90)
    public void startActions()
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
        // import dummy customer, if the parameter is set
        if (System.getProperty("starter.importCustomer") != null)
        {
            ImportFromXMLToDB.importCustomer();
        }
    }
}
