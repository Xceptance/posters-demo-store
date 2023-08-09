package util.xml;

/**
 * Provides methods to import data from a XML file into the database.
 * 
 * @author sebastianloob
 */
public class ImportFromXMLToDB
{

    /**
     * Reads categories from xml and pushes into database.
     */
    public static void importCategory()
    {
        final CategoryHandler contentHandler = new CategoryHandler();
        XmlUtils.readXmlFile(contentHandler, "assets/files/categories.xml");
    }

    /**
     * Reads products from xml and pushes into database.
     */
    public static void importProduct()
    {
        final ProductHandler productHandler = new ProductHandler();
        XmlUtils.readXmlFile(productHandler, "assets/files/products.xml");
    }

    /**
     * Reads customers from xml and pushes into database.
     */
    public static void importCustomer()
    {
        final CustomerHandler customerHandler = new CustomerHandler();
        XmlUtils.readXmlFile(customerHandler, "assets/files/customer.xml");
    }

    /**
     * Reads users from xml and pushes into database.
     */
    public static void importUser()
    {
        final UserHandler userHandler = new UserHandler();
        XmlUtils.readXmlFile(userHandler, "assets/files/user.xml");
    }
}
