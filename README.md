# The Poster Demo Store

*The Poster Demo Store*, or just *Posters*, is a simple e-commerce application for demoing load testing as well as test automation.


## Features

Posters comes with the basic functionality that you would expect from a typical online shop. This includes:

* A product catalog with categories and products, in our case, posters.
* A search function in the shop using lucene
* Customers may register with the shop.
* Customers can manage their account, including shipping and billing addresses as well as credit cards.
* There is a shopping cart.
* Customers may place orders as guests or as registered customers.
* A selection of different languages for the shop
* A selection of incorrect behavior can be switched on and off at will

See these screenshots to get an impression of what the Posters Demo Store looks like.

    WIP: Insert some screenshots here.

Please note that this application is for demo purposes only.


## Implementation Details

Posters is written in Java. It is built with the [Ninja Framework](https://www.ninjaframework.org/). See their excellent documentation if you need more information on how the application is developed and configured. 

Posters uses [H2](https://www.h2database.com/) as a simple file-based database and [EBean](https://ebean.io/) to map Java model classes to relational tables in the database.


## Getting the Posters Application

The latest version of Posters can be downloaded at the [Releases](https://github.com/Xceptance/posters-demo-store/releases) page. Place the JAR file `posters-demo-store-<version>.jar` anywhere on your local disk.


## Building Posters

As an alternative to downloading a prepackaged JAR file, you can build the JAR yourself. You will need to have Maven and JDK 17 installed on your machine.

To build the project, clone this repository to your local disk and run:

```
mvn clean package
```

If all went well, you will find several build artifacts in the `target` subdirectory, but the most important one is `posters-demo-store-<version>.jar`. This file contains the Posters code and all required libraries (including a Web application server) in a single, ready-to-run JAR file.


## Running Posters as a Console Application

If you have successfully downloaded or built the Posters JAR file, you can now run Posters with the default settings as follows:  

```
java -jar posters-demo-store-<version>.jar          # downloaded
java -jar target/posters-demo-store-<version>.jar   # built yourself
```

By default, the shop is available at [https://localhost:8443/](https://localhost:8443/). When opening the homepage via HTTPS, expect your browser to complain about the certificate since Posters comes with a self-signed certificate. See below for how to change that.

Posters stores its database and log files to the subdirectories `db` and `log`in the current directory.

When Posters is started for the first time, it will populate its database with a basic product catalog and a default storefront customer (email: `johndoe@example.com` / password: `topsecret`). With more and more customers registering with the shop and placing orders, the database will grow over time.

If you want to start over with a clean database, simply stop the app and delete the subdirectory `db`. On the next start, Posters will recreate the directory and the database.


## Customizing Posters

If the default settings do not suit you, you can adjust them as needed. See below for the most important settings.

| Property | Default | Description |
| -------- | ------- | ----------- |
| ninja.mode | prod | The application mode, one of `prod`, `test`, and `dev`. |
| ninja.port | -1 | The HTTP port. Use -1 to disable HTTP. |
| ninja.ssl.port | 8443 | The HTTPS port. Use -1 to disable HTTPS. |
| ninja.ssl.keystore.uri | classpath:/ninja/standalone/ninja-development.p12 | The URI to a key store with a custom server certificate. You will need to create and populate the key store. |
| ninja.ssl.keystore.password | password | The password to open the key store/the key. |
| ninja.context | (empty) | The context path where Posters will live, such as `/posters`. |
| logback.configurationFile | (none) | The path to a custom configuration file for the [Logback logging framework](https://logback.qos.ch/manual/configuration.html), such as `conf/logback.xml`. |

See [here](https://www.ninjaframework.org/documentation/configuration_and_modes.html) for more information on what else can be configured. Also see [application.conf](./src/main/java/conf/application.conf) for all Posters-specific settings and their defaults.

To run Posters with some custom properties: 

```
java                                            \
  -Dninja.port=-1                               \
  -Dninja.ssl.port=443                          \
  -Dninja.ssl.keystore.uri=conf/keystore.jks    \
  -Dninja.ssl.keystore.password=topsecret       \
  -jar posters-demo-store-<version>.jar
```

Alternatively, you can put all these properties in a file, for example `conf/posters.conf`:

```
ninja.port = -1
ninja.ssl.port = 443
ninja.ssl.keystore.uri = conf/keystore.jks
ninja.ssl.keystore.password = topsecret
```

To run Posters with this custom configuration file:

```
java                                                \
  -Dninja.external.configuration=conf/posters.conf  \
  -jar posters-demo-store-<version>.jar
```


## Extending Posters

If you wish to extend posters here are a few points that are good to know:

* Database:

To extend the database's existing tables add new entries in the .xml files in the same format as the current entries

To add new tables to the database you must:
1 - create a model for the represented entity in the src/main/java/models package (look at exiting ones for references)
2 - add the model to the ebean.models in application.conf in the form package.classname
3 - compile and run the aplication
4 - copy the content of the generated default-create-all.sql and default-drop-all.sql files in the 'target' folder into the default-create.sql and default-drop.sql files (or copy the file there and rename it) that you can find in src/main/java/conf
5 - delete everything in the 'db' folder
6 - compile and run again

Remember that you likely also should fill that table, so create a handler to do that for you. If you follow the rest of the design you do that by:
using xml (in src/main/java/assets/files) for the data itself and adding another handler for it in src/main/java/util/xml (look at existing ones for reference) 
or by:
extending existing ones if that makes more sense
If you create a new handler and xml file you need to add a new method to call them to the class 'ImportFromXMLToDB' and then call that method in the JobController.importData

* Languages / Locales:

To add a new language to posters you need to:
1 - add the language (and possibly dialect(s)) to languages.xml in the same style as the ones already there
2 - add a new messages_xx-YY.properties file where xx-YY is the ISO code for your locale and add all the messages specified in the existing messages files in your localized version
3 - add localized names for categories and subcategories in categories.xml in the same way as the ones already there. For every name add a new line to the block of its versions and give it the your locales code as xml-lang attribute
4 - add texts for products in the products.xml file in the same way as the ones already there. For every name, short description or long description you have to add a new line to the block of exiting ones for that tag and give it the code corresponding to your language as xml-lang attribute
5 - add your language as application language in application.conf by adding its code (separated by a ",") in the corresponding line
6 - delete the database (everything in the 'db' folder)
7 - compile and run the application

You can use step 6 and 7 earlier if you want to test during development. You need to do those two steps whenever you want your changes to be applied.

Search for languages / locales:
to also add search functionality for your language you need a new search engine setup. To get it you should:
1 - add a new stemming analyzer in util.standalone that is named after your language. You can copy an exiting one and rename it
2 - replace the stem filter with one for your language and leave the rest alone. You can have a look in the content of the org/apache/lucene/analysis package of lucene and check whether a default version for language exists. if it does not exist unfortunately you have to use a community created one or create one yourself (Stemming means reducing words to their base form and this filter is how this is handled in this case)
3 - in the LuceneSearch class add an Analyzer Variable in the "setup other language analyzers" section and give it an instance of your analyzer from step 1
3a - you probably want your search to match only that specific language while it is the selected locale. In that case copy the initialization of the directory and change the name of your copy to one indicating your language. This will ensure your index is stored seperately in its own space in memory
3b - if you wish for your language to be searchable in any locale (e.g. searching the english word "moose" in french and getting the french page for the poster that contains a moose even though the word is only there in the english version) you need only the standard directory
4 - copy the indexData method and change your copies' name to to indexData[YourLanguage]
5 - adjust the name of the Analyzer the index will be bound to to match your Analyzer from step 3
5a - if you added a language specific directory for the index in 3a replace the directory the index is written to with that directory
5b - if you do not want your index stored sperately leave the directory the index is written to as the default directory
6 - adjust the setup of the fields within this method to load data for your langauge e.g. "product.getName("yourlanguagecode-YOURLOCALECODE")"
7 - add a call of the method from step 4 into the "setup()" method
8 - in the "search" method with the loce parameter expand the selection of analyzers with at least one case for your langauge and locale
8a - if you have added a language specific directory in 3a expand the directory selection in the same way as the analyzer selection


## Viewing the database

If you want to view the database you need to run the H2 database engine and use it to inspect the database. You should use (variable content is given in {}):
- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:{your path to the poster demo store}/posters-demo-store/db/posters;AUTO_SERVER=TRUE
- user name: sa

## Enabling incorrect behavior

To activate or deactivate incorrect behavior or just to view the current status of this functionality go to one of two pages.
The content on both pages is the same. The difference between the two designs is the sorting and style of the options:

The first design is compact and shows non-customizable options on the left and customizable options on the right. To access it go to:
- [baseURL]/[YourLocale]/ok3ok2ru8udqx7gZGS9n/statusInfo

The second design groups options below eachother based on the site functionality they alter. To access it go to:
- [baseURL]/[YourLocale]/ok3ok2ru8udqx7gZGS9n/statusInfoDesign2

An overview of the options that are available:
Non-customizable:
- Category shift - Attempting to call a category will call a different category. The resulting category will be the category with the ID of the original one increased by 1 or the first category in case of the last category's ID
- Random quantities - Products will be added to cart in incorrect quantities. The quantity is randomized
- Order block - Orders will not go through. Carts will not be cleared. ATTENTION: The rest of the ordering process will still flow normally!
- Search mixup - Search will deliver results that belong to a different search locale (currently swaps between EN and DE)
- Cart randomization - Adding to cart will add a random product. All products have an equal chance (including the one originally attempted)
- Open login - Account logins accept anything as a valid password
- Random history - Order history in a logged in users account will show a different random history every time it is accessed

Customizable:
- Product block - block a single product from being added to the cart. The product is specified by its ID.
- Product blocking order - block all orders that contain a single specified product. The product is specified by its ID.
- Falsify result conter - the number entered in the input (negatives possible) will be added to the counter of search results found. NOTE: It only falsifies the counter. It does NOT affect the actual result amount
- Block search - Specify a search term. Searches for this term will return no results.
- Quantity limit - Specify a maximum quantity for products in the cart. Adding to the cart will only be possible up to this limit per product.
- Total cart limit - Specify a maximum amount of total items the cart is allowed to hold. Adding will only be possible up to this limit. Individual items are counted so multiple of the same product will count as the corresponding amount of items not just as "one product"

