# The Poster Demo Store

*The Poster Demo Store*, or just *Posters*, is a simple e-commerce application for demoing load testing as well as test automation.

## Features

Posters comes with the basic functionality that you would expect from a typical online shop. This includes:

* A product catalog with categories and products, in our case, posters
* A search function in the shop using Lucene
* Customers may register with the shop
* Customers can manage their account, including shipping and billing addresses as well as credit cards
* There is a shopping cart
* Customers may place orders as guests or as registered customers
* A selection of different languages for the shop (EN-US, EN-GB, DE-DE, SV-SE)
* A selection of incorrect behavior can be switched on and off at will

Please note that this application is for demo purposes only.

## Technology Stack

Posters is built with the following technologies:

| Component | Technology |
| --------- | ---------- |
| Framework | [Spring Boot](https://spring.io/projects/spring-boot) 3.4.3 |
| Language | Java 21 |
| Template Engine | [Thymeleaf](https://www.thymeleaf.org/) with Layout Dialect |
| Database | [H2](https://www.h2database.com/) (file-based) |
| ORM | JPA / Hibernate |
| Search | [Apache Lucene](https://lucene.apache.org/) 9.1 |
| Build Tool | [Maven](https://maven.apache.org/) |
| Password Hashing | jBCrypt |

## Prerequisites

* **JDK 21** or later
* **Maven** 3.9+ (if building from source)

## Getting the Posters Application

The latest version of Posters can be downloaded at the [Releases](https://github.com/Xceptance/posters-demo-store/releases) page.

## Building Posters

Clone this repository to your local disk and run:

```bash
mvn clean package
```

If all went well, you will find the build artifact in the `target` subdirectory: `posters-demo-store-3.0.0-SNAPSHOT.jar`. This file contains the Posters code and all required libraries (including an embedded Tomcat server) in a single, ready-to-run JAR file.

To only compile (without running tests):

```bash
mvn clean compile
```

## Running Posters

### From a Built JAR

```bash
java -jar target/posters-demo-store-3.0.0-SNAPSHOT.jar
```

### Using the Spring Boot Maven Plugin

For development, you can run Posters directly with the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

### Accessing the Application

By default, the shop is available at [http://localhost:8080/](http://localhost:8080/).

### Default Customer

When Posters is started for the first time, it will populate its database with a basic product catalog and a default storefront customer:

* **Email**: `johndoe@example.com`
* **Password**: `topsecret`

### Data Storage

Posters stores its data in:

| Directory | Purpose |
| --------- | ------- |
| `./db/` | H2 database files |
| `./db/lucene-index/` | Lucene search index |
| `./log/` | Application log files |

If you want to start over with a clean database, simply stop the app and delete the `db` subdirectory. On the next start, Posters will recreate the directory and the database.

## Customizing Posters

Configuration is managed via `src/main/resources/application.yml`. You can override any property using standard Spring Boot mechanisms.

### Common Properties

| Property | Default | Description |
| -------- | ------- | ----------- |
| `server.port` | `8080` | The HTTP port |
| `spring.datasource.url` | `jdbc:h2:file:./db/posters` | JDBC URL for the H2 database |
| `spring.h2.console.enabled` | `true` | Enable the H2 web console |
| `spring.h2.console.path` | `/h2-console` | Path to the H2 web console |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema management strategy |
| `posters.currency` | `$` | Currency symbol |
| `posters.shipping-costs` | `7.00` | Shipping costs |
| `posters.tax` | `0.06` | Tax rate |
| `posters.page-size` | `8` | Products per page |
| `posters.languages` | `en-US,en-GB,de-DE,sv-SE` | Available languages |

### Overriding Properties

You can override properties via command-line arguments:

```bash
java -jar target/posters-demo-store-3.0.0-SNAPSHOT.jar \
  --server.port=9090 \
  --posters.currency="€"
```

Or via an external configuration file:

```bash
java -jar target/posters-demo-store-3.0.0-SNAPSHOT.jar \
  --spring.config.additional-location=file:./conf/posters.yml
```

## Viewing the Database

Posters ships with the H2 web console enabled. While the application is running, navigate to [http://localhost:8080/h2-console](http://localhost:8080/h2-console) and use the following settings:

| Setting | Value |
| ------- | ----- |
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:file:./db/posters` |
| User Name | `sa` |
| Password | *(empty)* |

## Running Tests

```bash
mvn test
```

## Enabling Incorrect Behavior

To activate or deactivate incorrect behavior or just to view the current status of this functionality go to one of two pages. The content on both pages is the same. The difference between the two designs is the sorting and style of the options:

The first design is compact and shows non-customizable options on the left and customizable options on the right. To access it go to:
- `[baseURL]/[YourLocale]/ok3ok2ru8udqx7gZGS9n/statusInfo`

The second design groups options below each other based on the site functionality they alter. To access it go to:
- `[baseURL]/[YourLocale]/ok3ok2ru8udqx7gZGS9n/statusInfoDesign2`

### Non-customizable Options

| Option | Description |
| ------ | ----------- |
| Category shift | Calling a category will call a different category (ID + 1, or wraps to first) |
| Random quantities | Products are added to cart in incorrect, randomized quantities |
| Order block | Orders will not go through; carts will not be cleared |
| Search mixup | Search delivers results from a different locale (swaps EN and DE) |
| Cart randomization | Adding to cart adds a random product instead |
| Open login | Account logins accept anything as a valid password |
| Random history | Order history shows a different random history on each access |

### Customizable Options

| Option | Description |
| ------ | ----------- |
| Product block | Block a single product from being added to cart (by product ID) |
| Product blocking order | Block all orders containing a specified product (by product ID) |
| Falsify result counter | Add/subtract from the search result count display |
| Block search | Block searches for a specific term (returns no results) |
| Quantity limit | Set a maximum quantity per product in the cart |
| Total cart limit | Set a maximum total item count for the cart |

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

Copyright (c) 2013-2024 [Xceptance Software Technologies GmbH](https://www.xceptance.com/)
