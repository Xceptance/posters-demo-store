# Contributing to the Posters Demo Store Project

You want to be part of the Posters community? Report bugs or suggest improvements in the [issue tracker](https://github.com/Xceptance/posters-demo-store/issues).

If you are a developer and want to contribute fixes or extensions, create pull requests with your changes. See below for more information on what needs to be installed and how the code is developed.


## Prerequisites

Before you can start developing Posters, you will need to:

* Install JDK 17.
* Install the latest Maven.
* Clone this repository to your local disk (or fork the repository).
* Import the Posters Maven project into your favorite IDE.


## Documentation

Posters is built with the [Ninja Framework](https://www.ninjaframework.org/). See their excellent documentation if you need more information on how the application is developed and configured.


## Development Cycle

1. Open a terminal window in the root of the Posters repository on your disk.
1. Clean the project:
    ```
    mvn clean
    ```
1. Compile the project and enhance the database model classes:
    ```
    mvn process-classes
    ```
1. Run the Posters application in Ninja's "SuperDevMode" (hot-reloading of modified classes and templates):
    ```
    mvn ninja:run
    ```
1. Open http://localhost:8080/ in a Web browser.
1. Start changing classes or templates in your IDE. Watch the terminal to see the application being reloaded.
1. Test your changes to the application in the Web browser.

