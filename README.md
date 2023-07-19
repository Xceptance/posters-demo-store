# Posters Demo Store
>A simple e-commerce application for demoing load testing as well as test automation

## Software Requirements
- [Apache Maven 3.6.3+](https://maven.apache.org/download.cgi)
- [Java JDK 11](https://adoptium.net/de/temurin/archive/?version=11)

## How to build the project
1. Clone the current repository of posters-demo-store
2. Add the "bin" directory of both software requirements (Java and Maven) to your "Path" enviroment variable
3. Open a new terminal in your cloned repository (posters-demo-store)
* Make sure the classes have not been compiled with a different Java version
	```
	mvn clean process-classes
	```
* Run the website locally
	```
	mvn ninja:run
	```
4. Open http://localhost:8080 in your browser
