# Posters Demo Store

Posters Demo Store, or just Posters, is a simple e-commerce application for demoing load testing as well as test automation.


## Features

Posters comes with the basic functionality that you would expect from a typical online shop. This includes:

* A product catalog with categories and products, in our case, posters.
* Customers may register with the shop.
* Customers can manage their profile including shipping and billing addresses as well as credit cards.
* There is a shopping cart.
* Customers may order posters as guest or as registered customer.

See these screenshots to get an impression what the Posters Demo Store looks like.

    WIP: Insert some screenshots here.

Please note that this application is for demo purposes only.


## Implementation Details

Posters is written in Java. It is built with the [Ninja framework](https://www.ninjaframework.org/). See their excellent documentation if you need more information on how the application is developed and configured. 

Posters uses [H2](https://www.h2database.com/) as a simple file-based database and [EBean](https://ebean.io/) to map Java model classes to relational tables in the database.


## Getting the Posters Application

The latest version of Posters can be downloaded from [Maven Central](https://search.maven.org/artifact/com.xceptance/posters-demo-store). Place the JAR file `posters-demo-store-<version>.jar` anywhere to your local disk.


## Building Posters

As an alternative to downloading a prepackaged JAR file, you can build the JAR yourself. Posters Demo Store is a regular Maven project, so you will need to have Maven installed on your machine. To build the project yourself, clone this repository to your local disk and run:

```
mvn clean package
```

If all went well, you will find several build artifacts in the `target` subdirectory, but the most important one is `posters-demo-store-<version>.jar`. This file contains the Posters code and also all required libraries (including a Web application server) in a single ready-to-run JAR file.


## Running Posters as a Console Application

If you have successfully downloaded or built the Posters JAR file, you can now run Posters with default settings as follows:  

```
java -jar posters-demo-store-<version>.jar          # download
java -jar target/posters-demo-store-<version>.jar   # built yourself
```

By default, the shop is available at [http://localhost:8080/](http://localhost:8080/) and [https://localhost:8443/](https://localhost:8443/). When opening the homepage via HTTPS, expect your browser to complain about the certificate since Posters comes with a self-signed certificate. See below for how to change that.

Posters stores its database and log files to the subdirectories `db` and `log`in the current directory.

When Posters is started the first time, it will populate its database with a basic product catalog and with a default customer (email: `john@doe.com`/ password: `topsecret`). With more and more customers registering with the shop and placing orders, the database will grow over time.

If you want to start over with a clean database, simply stop the app and delete the subdirectory `db`. On next start, Posters will recreate the directory and the database anew.


## Customizing Posters

If the default settings do not suit you, you can adjust them as needed. See below for the most important settings.

| Property | Default | Description |
| -------- | ------- | ----------- |
| ninja.mode | prod | The application mode, one of `prod`, `test`, and `dev`. |
| ninja.port | 8080 | The HTTP port. Use -1 to disable HTTP. |
| ninja.ssl.port | 8443 | The HTTPS port. Use -1 to disable HTTPS. |
| ninja.ssl.keystore.uri | classpath:/ninja/standalone/ninja-development.p12 | The URI to a key store with a custom server certificate. You will need to create and populate the key store. |
| ninja.ssl.keystore.password | password | The password to open the key store/the key. |
| ninja.context | (empty) | The context path where Posters will live, such as `/posters`. |
| logback.configurationFile | (none) | The path to a custom configuration file for the [Logback logging framework](https://logback.qos.ch/manual/configuration.html), such as `conf/logback.xml`. |

See [here](https://www.ninjaframework.org/documentation/configuration_and_modes.html) for more information what else can be configured. Also see [application.conf](./src/main/java/conf/application.conf) for all Posters-specific settings and their defaults.

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

