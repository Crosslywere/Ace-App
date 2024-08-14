# Ace App

Go to [starter.spring.io](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.0-SNAPSHOT&packaging=jar&jvmVersion=21&groupId=com.ace&artifactId=app&name=AceExamPlatform&description=Ace%20Exam%20Platform%20project%20for%20Spring%20Boot&packageName=com.ace.app&dependencies=devtools,web,thymeleaf,postgresql,lombok,data-jpa,validation,mail) get the configuration.

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
	> Inprogress
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
