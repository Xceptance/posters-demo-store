package util.xml;


public class PushCategAndProdIntoDb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// read categories from xml and push into database
		CategoryHandler contentHandler = new CategoryHandler();
		XmlUtils.readXmlFile(contentHandler, "src/main/java/assets/files/categories.xml");
		// read products from xml and push into database
		ProductHandler productHandler = new ProductHandler();
		XmlUtils.readXmlFile(productHandler, "src/main/java/assets/files/products.xml");
	}
}
