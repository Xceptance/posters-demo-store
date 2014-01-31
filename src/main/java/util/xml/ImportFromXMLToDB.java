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
        CategoryHandler contentHandler = new CategoryHandler();
        XmlUtils.readXmlFile(contentHandler, "src/main/java/assets/files/categories.xml");
    }

    /**
     * Reads products from xml and pushes into database.
     */
    public static void importProduct()
    {
        ProductHandler productHandler = new ProductHandler();
        XmlUtils.readXmlFile(productHandler, "src/main/java/assets/files/products.xml");
    }

    /**
     * Reads customers from xml and pushes into database.
     */
    public static void importCustomer()
    {
        CustomerHandler customerHandler = new CustomerHandler();
        XmlUtils.readXmlFile(customerHandler, "src/main/java/assets/files/customer.xml");
    }
}
