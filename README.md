UcpQuartzReconnectionTest
=========================

This application creates a Quartz Scheduler and some Quartz jobs that are executed continuously. After a configured time interval no more jobs are executed and the application exits. All the jobs are identical: when executing they just print the trace "Hello Quartz! with context [...]" and then exit.

Requirements
------------
This application requires access to an Oracle database with a proper set of Quartz tables. A script for the creation of these tables as expected by the default quartz.properties configuration is provided with the name *tables_oracle_test.sql*. The Quartz project sources provide a broad set of base scripts for different types of databases (https://github.com/quartz-scheduler/quartz/tree/master/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstore).

Execution  
---------
To execute the application just go to the folder where is the jar file and write on the command line: 

	java -jar ucp-reconnection-x.x.x.x-SNAPSHOT.jar

The application will create some quartz jobs in DB and then execute periodically until the end of the application where they are close and deleted from DB. 

Each job has a name, that appears in the traces, with the format:

	HelloJob{quartz.test.job.middleName}{index in 0..quartz.test.job.number-1}

Configuration files
--------------------
They are *application.properties*, that controls the application behaviour, the *quartz.properties* with the quartz configuration like the DB connection credentials, and the *logback.groovy* that controls the trace appearance. 
All these are inside the jar file but they can be overwritten just by replicating them in the same directory as this though for the logback.groovy is also needed to add the line 

    logging.config=logback.groovy

to the application.properties.

The properties in application.properties can also be overwritten from the command line when starting the application:

	java -jar ucp-reconnection-x.x.x.x-SNAPSHOT.jar --{propiedad}={valor}

This could be useful for starting several application instances without duplicating the files. Example for starting two applications with different quartz job names each:

Console 1:

	java -jar ucp-reconnection-x.x.x.x-SNAPSHOT.jar --quartz.test.job.middleName=_alpha_

Console 2:

	java -jar ucp-reconnection-x.x.x.x-SNAPSHOT.jar --quartz.test.job.middleName=_beta_

Configuration properties
------------------------
The *application.properties* has de properties:

- quartz.test.duration: Test duration. After that the application closes.
- quartz.test.job.cronExpression: Cron expression for the execution of every Quartz job
- quartz.test.job.forceUpdate: If the Quartz jobs already exist in DB when a new application instance starts a value false means it will use then, else the jobs are deleted and new ones are created. This property is useful in multi node environments.
- quartz.test.job.middleName: Its string value is used to modify the job name which is formed by the concatenation of "HelloJob" plus the value of this property plus the job index.
- quartz.test.job.number: Number of jobs for the test. The job index is a number in the range 0 to this value minus 1.

The *quartz.properties* is a normal Quartz configuration file (http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/) that uses a JDBC-Jobstore. In our case the jdbc driver is an OracleDriver over UCP and optionally also with ONS. The properties of type _org.quartz.dataSource.platformDS_ are mapped to that of the UCP pool data source (https://docs.oracle.com/cd/E11882_01/java.112/e12265/intro.htm#BABHFGCA).

The *logback.groovy* file contains the configuration for the Logback tracing tool in groovy format (http://logback.qos.ch/manual/configuration.html).   
