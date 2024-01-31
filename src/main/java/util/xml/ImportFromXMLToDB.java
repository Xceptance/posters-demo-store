/*
 * Copyright (c) 2013-2023 Xceptance Software Technologies GmbH
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
        XmlUtils.readXmlFile(userHandler, "assets/files/backofficeuser.xml");
    }
}
