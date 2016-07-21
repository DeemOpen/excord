## ExCord - Extension Cord 2.0

### Requirements

1. Java 7 to run.
2. Mysql DB

### Setup

1. mvn clean install
2. Setup Mysql DB instance. Refer DB setup Section
2. Copy the application.properties to the folder with the jar file. Modify it to point to the correct JDBC, server port, ldap etc.
3. Run the jar. ( nohup java -jar excord-1.0-SNAPSHOT.jar & )
4. http://localhost:9090


### Login Info
1. excord support LDAP integration.
2. excord supports Database Integration. (user: excord, pwd: excord)

### Technology Stack

1. Spring Boot
2. Freemarker template.
3. Bootstrap.
4. Jquery.
5. Flyway DB migration.


### Features

1. Lightweight application, Easy to Setup & Navigate.
2. Create Requirements.
3. Create Testcases.
4. Create Testplans.
5. Associate Testcases with Requirements.
5. Associate Testcases with Testplan for execution.
6. Reports
7. REST API's for integration with automation tools. Automation scripts can POST the test results against testcases.
8. History of changes tracked.
9. Import & Export to and from Excel.
10. Assign testcases to tester for execution.
11. View all test steps on single test run page.
12. View Execution history on a test plan.
13. Test plan overall progress, pass rate, time to run visible.
14. Cut & Paste test cases to different folders.
15. Requirement coverage analysis
16. Change in requirement triggers testcase for review and testcases for re-runs



### DB Setup

```sql
CREATE USER 'excord' IDENTIFIED BY 'excord';
CREATE DATABASE excord;
grant all on excord.* to 'excord'@'localhost' identified by 'excord';
flush privileges;
```

If you are using DB authentication insert this row after starting the server first time. The startup creates the necessary tables.

```sql
INSERT INTO `excord`.`ec_user` (`id`, `username`, `name`, `password`, `role`, `enabled`, `created_date`,`email`) 
VALUES ('1', 'excord', 'excord', 'excord', 'USER', '1', NOW(),'test@blackhole.com');
```

### Docker Setup

Change the properties file
spring.datasource.url=jdbc:mysql://db:3306/excord

To build docker image.

```make build```

To publish docker image.

```make publish```

Start the container - You will need docker-compose.

``make up``

Stop the container - You will need docker-compose.

``make down``


### Log Monitoring
tail -f excord.log
grep 'GET' access_log.*.log | egrep -v "(.gif|.jpg|.png|.swf|.ico|.txt|.xml|.css|.js|.rss)"

### Commands

To stop the server.
curl -X POST http://localhost:9095/manage/shutdown

Health Metrics:
http://localhost:9095/manage/health

Thread dump:
http://localhost:9095/manage/dump

Env:
http://localhost:9095/manage/env

Metrics:
http://localhost:9095/manage/metrics

### Screenshots

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-db.png"/>
<br/>


<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-1.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-2.png"/>
<br/>


<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-3.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-4.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-5.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-6.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-7.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-8.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-9.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-10.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-11.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-12.png"/>
<br/>

<br/>
<img src="https://raw.github.com/DeemOpen/excord/master/images/excord-13.png"/>
<br/>

### License & Contribution

ExCord is released under the Apache 2.0 license. Comments, bugs, pull requests, and other contributions are all welcomed!
Icon designed by Freepik
