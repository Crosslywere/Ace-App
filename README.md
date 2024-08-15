# Ace App

Go to [starter.spring.io](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.0-SNAPSHOT&packaging=jar&jvmVersion=21&groupId=com.ace&artifactId=app&name=AceExamPlatform&description=Ace%20Exam%20Platform%20project%20for%20Spring%20Boot&packageName=com.ace.app&dependencies=devtools,web,thymeleaf,postgresql,lombok,data-jpa,validation,mail) get the configuration.

Also go to [e-iceblue](https://www.e-iceblue.com/Download/doc-for-java-free.html) for Spire.Doc dependency

## TODO List

1. Once an examinee has signed in and started the exam they should be prevented from signing in again.
	> Implemented
2. There should be a way to have multiple exams papers under a singular exam.
	> Implemented
3. Each examinee should have a unique sequence of questions.
	> Implemented
4. Administrators should be able to set how examinees are identified for login.
	> Implemented
5. Answers should be able to be set in questions uploaded to the software.
	> Implemented
6. Exporting the results of the exam should be a CSV file.
	> Implemented
7. Exporting of the results should be customizable.
	> Implemented
8. Change the question numbers to be more visible on the exam page for examinees.
	> Implemented
9. Implement keyboard navigations for question and answer selections for examinees.
	> Implemented
10. Implement a way to specify login credentials for examinees, so that if there are a specific set of examinees to write the exam no new examinee will be allowed (make flexible for on the fly examinee addition).
	> Implemented
11. Allow exams to set an option for examinees to see their score as a percentile after they have concluded their exam.
	> Implemented
12. Allow examinees to review their experience with the software (exportable as a CSV)
13. Create UML diagrams that describes how the system works.
14. Candidates should not see weather they passed or not.
	> Implemented
15. Inform candidates of the exam via SMS and/or email.

## HOW TO USE

This application is a web based application, meaning you would be needing a browser to interact with it.
(The design is strictly for desktop.)

### INSTALL

This application relies on [Java-21(LTS)](https://www.oracle.com/java/technologies/downloads/#java21) and [Docker](https://www.docker.com) to be fully utilized.

Once Java has been installed, use the following command to compile the project for your particular operating system. (Note you will have to be in the projects directory in the shell program)

#### Linux
```bash
./mvnw clean install
```

#### Windows
```cmd
.\mvnw.cmd clean install
```

Once this is completed you will then need to have Docker installed to set up the application fully.

Do this by using the following command in your shell/command prompt application. (Note you will have to be in the projects directory in the shell program)

#### To start the application
```
docker compose -f ./docker-compose.yaml up -d
```

#### To stop the application
```
docker compose -f ./docker-compose.yaml down
```

For configuring the applications enviroment please see [Docker Configuration](#docker-configuration)

### Docker Configuration

The application's is customizable via the projects docker-compose.yaml file.
Here is a list of enviroment variable that can be customized before running the application

- __*POSTGRES_DB*__ Specifies the name of the Postgres database. This must be the same as the end of __DB_URL__ in the *app* side. eg. jdbc:postgresql://app_db:5432/<*POSTGRES_DB | DB_URL*>

- __*POSTGRES_USER*__ Specifies the username that the database should be created with. This must be the same as the __DB_USERNAME__ in the *app* side.

- __*POSTGRES_PASSWORD*__ Specifies the password that the database should be created with. This must be the same as the __DB_PASSWORD__ in the *app* side.

- __*DB_NAME*__ Specifies the database being used by the system. This should not be modified.

- __*DB_URL*__ Specifies where the database is located on the host system. This should not be modified (only as specified above.)

- __*DB_USERNAME*__ Specifies the username for the database so that the application can have access to the database. Must be the same as the __POSTGRES_USER__ in the *app_db* side.

- __*DB_PASSWORD*__ Specifies the password for the database so that the application can have access to the database. Must be the same as the __POSTGRES_PASSWORD__ in the *app_db* side.

- __*DB_DDL*__ Specifies how the database should handle entities in the application. This can be set to either 
	* __"create"__ which creates a new table in the database for every entity.
	* __"create-drop"__ creates a new table in the database for every entity but deletes the table upon exiting the application.
	* __"update"__ updates the tables as necessary to match those of the entities upon starting the application.
	* __"validate"__ validates that the database tables match the entities of the application. 

- __*DB_DRIVER*__ Specifies the driver class for the database. This should not be modified.

- __*DB_PLATFORM*__ Specifies the platform class for the database. This should not be modified.

- __*ports*__ Specifies the port number to map to the in application port number. This does not need to be modified unless it is conflicting with another applications port number.