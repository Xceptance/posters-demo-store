package util.xml;

public class ImportFromXMLToDB
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        importCategory();
        importProduct();
        importCustomer();
    }
    
    public static void importCategory()
    {
        // read categories from xml and push into database
        CategoryHandler contentHandler = new CategoryHandler();
        XmlUtils.readXmlFile(contentHandler, "src/main/java/assets/files/categories.xml");
    }
    
    public static void importProduct()
    {
        // read products from xml and push into database
        ProductHandler productHandler = new ProductHandler();
        XmlUtils.readXmlFile(productHandler, "src/main/java/assets/files/products.xml");
    }
    
    public static void importCustomer()
    {
        // read customers from xml and push into database
        CustomerHandler customerHandler = new CustomerHandler();
        XmlUtils.readXmlFile(customerHandler, "src/main/java/assets/files/customer.xml");
    }
}
