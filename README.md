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
2. excord supports Database Integration. (user: admin, pwd: manager)

## Technology Stack

1. Spring Boot
2. Freemarker template.
3. Bootstrap.
4. Jquery.
5. Flyway DB migration.


### Features

1. Lightweight application
2. REST API's for integration with automation tools
3. Document testcases.
4. Associate testcases with testplan and run the test plans.
5. Metric Reports


### DB Setup

```sql
CREATE USER 'excord' IDENTIFIED BY 'excord';
CREATE DATABASE excord;
grant all on excord.* to 'excord'@'localhost' identified by 'excord';
flush privileges;
```

If you are using DB authentication insert this row.

```sql
INSERT INTO `excord`.`ec_user` (`id`, `username`, `name`, `password`, `role`, `enabled`, `created_date`,`email`) 
VALUES ('1', 'admin', 'admin', 'manager', 'USER', '1', NOW(),'test@blackhole.com');
```

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



### License & Contribution

ExCord is released under the Apache 2.0 license. Comments, bugs, pull requests, and other contributions are all welcomed!
Icon designed by Freepik