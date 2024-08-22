# Ace App

Go to [starter.spring.io](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.0-SNAPSHOT&packaging=jar&jvmVersion=21&groupId=com.ace&artifactId=app&name=AceExamPlatform&description=Ace%20Exam%20Platform%20project%20for%20Spring%20Boot&packageName=com.ace.app&dependencies=devtools,web,thymeleaf,postgresql,lombok,data-jpa,validation,mail) get the configuration.

Also go to [e-iceblue](https://www.e-iceblue.com/Download/doc-for-java-free.html) for Spire.Doc dependency for reading .docx/.doc files.

And go to [twilio](https://www.twilio.com) for Twilio SMS messaging.

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
	> In progress
13. Create UML diagrams that describes how the system works.
14. Candidates should not see weather they passed or not.
	> Implemented
15. Inform candidates of the exam via SMS and/or email.
	> Implemented

## HOW TO USE

This application is a web based application, meaning you would be needing a browser to interact with it.
(The design is strictly for desktop.)

### INSTALL

This application relies on [Java-21(LTS)](https://www.oracle.com/java/technologies/downloads/#java21) and [Docker](https://www.docker.com) to be fully utilized.

Once Java has been installed, use the following command to compile the project for your particular operating system. (Note you will have to be in the projects directory in the shell program)

__Linux__
```bash
./mvnw clean install
```

__Windows__
```cmd
.\mvnw.cmd clean install
```

Once this is completed you will then need to have Docker installed to set up the application fully.

Do this by using the following command in your shell/command prompt application. (Note you will have to be in the projects directory in the shell program)

__To start the application__
```
docker compose -f ./docker-compose.yaml up -d
```

__To stop the application__
```
docker compose -f ./docker-compose.yaml down
```

For configuring the applications enviroment please see [Docker Configuration](#docker-configuration)

### CREATING AN EXAM

Once the application has been successfully installed the [create](localhost:8081/create) page is where to go to create an exam.

From here you can create an exam by setting;

1. __Title__ The title of the exam as the candidate should see it.

2. __Start Time__ The time when the exam should be opened for candidates to begin writing. Defaults to 9am.

3. __End Time__ The time when the exam should be closed for candidates and recorded. Defaults to 5pm.

4. __Scheduled Date__ The date the exam should be held. Defaults to the next day.

5. __Duration__ The duration(in minutes) that a candidate is allowed to write the paper for. Defaults to 60 minutes.

6. __Set Cutoff Mark__ Weather or not there should be a cutoff should be set.

7. __Cutoff Mark__ Depending on if set cutoff mark is allowed, allows the cutoff mark to be set as a percentage. Defaults to 50% and steps by 5%.

8. __Upload Exam Papers__ Allows the exam paper to be uploaded as a text or word document. Papers should follow a strict set of rules that can be found [here](#how-to-write-a-paper).

***	
Once the paper has be uploaded some more options become available.
***

9. __Papers Per Candidate__ The number of papers a candidate must select to begin the exam. Defaults to the number of papers extracted divided by 2 plus 1. Eg 6 papers extracted means 6 / 2 + 1 = 4 papers per candidate by default. 

10. __Show Results__ Allows the candidate see their result as a percentage. Defaults to true if all questions have answers.

11. __Papers__ The extracted papers details

	* __Name of Paper__ Sets the name of the paper.

	* __Questions Per Candidate__ Sets the number of the questions each candidate will receive when they select a paper. Defaults to the number of questions in the paper.

	* __Manditory__ Sets weather the paper is meant to be taken by every candidate. Defaults to false.

	* __Answer__ Sets the answer for the question. Defaults to the answer, if any, set in the uploaded paper.

12. __Field 1 & Field 2__ Sets the type of data expected for each field. ie *None*, *Text*, *Email*, *Telephone*, *Number*, and *Password*

13. __Field Description__ Sets the displayed label for the associated field. 

14. __Upload Candidates__ Allows the candidates login details to be uploaded as a .csv file. The rules for the organization of data for the candidate import is [here](#how-to-organize-candidate-csv)

> __NOTE__ Upon creating the exam an email/SMS is sent to all participating candidates depending on if there is a column for their phone number/email.

The route to write an exam is the `/exam` route. If an exam is not avaiable at the time of loading the page, the candidate can simply refresh the page until an exam becomes available.

### Docker Configuration

The application's is customizable via the projects docker-compose.yaml file.
Here is a list of enviroment variable that can be customized before running the application

- __*POSTGRES_DB*__ Specifies the name of the Postgres database. This must be the same as the end of *DB_URL* in the *app* side. eg. jdbc:postgresql://app_db:5432/<*POSTGRES_DB | DB_URL*>

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

- __*ports*__ Specifies the port number to map to the in application port number. This does not need to be modified unless it is conflicting with another applications port number. To change the exposed port number(the port number that you use to connect to the service outside of Docker) you would change the number on the right hand side of the column. eg 1234:<*EXPOSED_PORT_NUMBER*>
	* __Database Port__ The database's port is 5432 exposed as port 5432.

	* __Application Port__ The application's port is 8081 exposed as port 8081. 

### How To Write A Paper

To write a paper to be correctly ingested by the software the following rules should be followed:

1. A paper's name at the start of the paper would signify a new paper and all questions that are after that paper name would be added to that paper. A paper name is writen as:
"Math:" ending with a newline.

2. Each under a paper should have its own unique number followed by a fullstop and then a space. Eg. "1. Calculate the ..." with the question ending in a new line.

3. The option for each question should be labelled from *"a"* to *"e"* followed by a fullstop and then a space. Eg. "A. 53.333" with the option ending in a new line.

4. If an answer is provided it should come after the question. The answer should be labeled with *"Ans"* and should have the letter of the option afterwards. Eg. "Ans: d" with a newline ending.

A full representation of what a paper and a question should look like is the following:
```
Accounting:
1. A __________ balance is a list of account balances extracted from the ledger accounts at a given date.
a. Ledger
b. Contra
c. Cash
d. Trial
e. Journal
Ans: D
2. Which of the following accounting equations is NOT correct?
a. A = E
b. A = L + E
c. E = A - L
d. L = A - E
e. A = E - L
Ans: e

Physics:
1. In a resonance tube experiment, a tube of fixed length in closed at one end and several turning forks of increasing frequency used to obtain resonance at the open end. If the turning fork with the lowest frequency which gave resonance had a frequency ƒ1 and the next turning fork to give resonance had a frequency ƒ2, find the ratio ƒ2/ ƒ1.
a. 12
b. 2
c. 6
d. 22
e. 80
Ans: b
```

### How To Organize Candidate CSV

The software extracts the first 2 columns of the CSV file and uses them as the 2 fields of the candidates login credentials. The first field cannot be blank/None.

The first row is used as the field descriptions. These field descriptions are used to determine the automatically generated field types. These are as follows:

- __None__ (For the second column only) selected if there is no data or if there should be no data.

- __Text__ Selected for the first column if the column does not match any of the other options.

- __Email__ Selected if the column's description contains the word *"mail"* (case insensitive).

- __Telephone__ Selected if the column's description contains the word *"tel"* or *"phone"* (case insensitive).

- __Number__ Selected if the column's description contains the word *"num"* (case insensitive).

- __Password__ Selected for the second column if the column's description does not match any of the other options.

When a list of candidates is attached to an exam the exam prevents new candidates from writing 

> __NOTE__ The candidate also has hidden fields for their email and phone number used to notify the candidate about their exam details. These are gotten from the CSV file as well by using a matching method for the fields. ie *Telephone* for the phone number and *Email* for the email.

> __NOTE__ The emailing service does not seem to work with MTN service provider. -- (Futher investigation required)