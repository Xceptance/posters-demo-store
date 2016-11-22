- get current version from git
- go to src/main/java/conf and adjust the application.conf if needed
- open a terminal inside of the folder that contains the project: type 'mvn jetty:run'
- open a web browser and go to http://localhost:8080/
- happy testing

Trouble shooting:
- delete the data folder in case you have an old installation and you find the message "Oops...". Changes in the database could lead to this behaviour.