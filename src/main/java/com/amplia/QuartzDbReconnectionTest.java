package com.amplia;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the creation of a Quartz Scheduler and of some jobs to execute.
 * 
 * Is intended to be run as an stand alone application. To execute use the command:
 * <p>
 *     java -jar ucp-reconnection-x.x.x.x-SNAPSHOT.jar
 *     or 
 *     java -jar ucp-reconnection-x.x.x.x-SNAPSHOT.jar --quartz.test.job.middleName={job middle name}
 * </p>
 * or from maven:
 * <p>
 *     mvn spring-boot:run
 *     or
 *     mvn spring-boot:run -Dspring-boot.run.arguments=--quartz.test.job.middleName={job middle name}
 * </p>
 * 
 * To change configuration properties create your own application.properties or quartz.properties in the same directory where is the jar file.
 * 
 * The name of the jobs are:
 * <p>
 *     HelloJob{quartz.test.job.middleName}{index in 0..quartz.test.job.number-1}
 * </p>
 * 
 */
@SpringBootApplication
@Slf4j
@ComponentScan("es.amplia")
public class QuartzDbReconnectionTest implements CommandLineRunner {
	private static final String QUARTZ_TEST_GROUP = "QuartzTest";
	private static final String QUARTZ_TEST_TRIGGER = "HelloWorldTrigger";
	
	public static void main(String[] args) {
		SpringApplication.run(QuartzDbReconnectionTest.class, args);
	}

	/**
	 * Duration of the test
	 */
	@Value("${quartz.test.duration}")
	public long duration;
	/**
	 * Cron expression to trigger the Quartz jobs
	 */
	@Value("${quartz.test.job.cronExpression}")
	public String cronExpression;
	/**
	 * If true and the Quartz job already exists then re-create the job 
	 */
	@Value("${quartz.test.job.forceUpdate}")
	public boolean forceUpdate;
	/**
	 * Middle element in the quartz job (and trigger) name
	 */
	@Value("${quartz.test.job.middleName}")
	public String middleName;
	/**
	 * Number of Quartz jobs to create and execute 
	 */
	@Value("${quartz.test.job.number}")
	public int jobNumber;
	
 	@Override
	public void run(String... args) throws Exception {
		log.info("Starting QuartzDbReconnectionTest");
		log.info("cronExpresion {}", cronExpression);
		log.info("middleName {}", middleName);
		if (args.length > 0){
			log.info("argument value: {}", args[0]);
		}
//		helloJobTest("_TEST");
//	    multiThreadTest(); un desastre y no tiene sentido
//	    multipleSchedulersTest();

	    multipleJobsSameSchedulerTest();
		System.exit(0);
	}
	

	public String helloJobTest(String jobNameSuffix) throws SchedulerException {
		log.info("helloJobTest with jobNameSuffix {}", jobNameSuffix); 
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		log.info("Scheduler summary: " + scheduler.getMetaData().getSummary());
		log.info("Scheduler pool class: " + scheduler.getMetaData().getThreadPoolClass().getName());
		String jobName = HelloJob.class.getSimpleName() + middleName + jobNameSuffix;
		String triggerName = "triggerFor" + jobName;
		JobDetail job = JobBuilder.newJob(HelloJob.class)
				.withIdentity(jobName, QUARTZ_TEST_GROUP)
				.build();
		
		JobKey jobKey = job.getKey();
		log.info("Preparing job {}/{} with key {}.", QUARTZ_TEST_GROUP, jobName, jobKey);
		if(scheduler.checkExists(jobKey)) {
			if(forceUpdate) {
				log.info("Job {}/{} already exists but force-update mode selected. Deleting it.", QUARTZ_TEST_GROUP, jobName);
				scheduler.deleteJob(jobKey);
			} else {
				log.info("Job {}/{} already exists.", QUARTZ_TEST_GROUP, jobName);
				return jobName;
			}
		}
		if (cronExpression != null && !cronExpression.isEmpty()) {
			log.info("Job {}/{} do not exists, scheduling at {}.", QUARTZ_TEST_GROUP, jobName, cronExpression);
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerName, QUARTZ_TEST_TRIGGER)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
					.build();
			scheduler.scheduleJob(job, trigger);
		}
		
		scheduler.start();
		
		//Sleep for 10s before delete trigger
		log.info("Waiting for the Quartz job");
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.info("Deleting the Quartz job");
		scheduler.deleteJob(jobKey);
//		scheduler.shutdown();
		return jobName;
	}

	public String helloJobTest(String jobNameSuffix, Scheduler scheduler) throws SchedulerException {
		log.info("helloJobTest with jobNameSuffix {}", jobNameSuffix); 
		log.info("Scheduler summary: " + scheduler.getMetaData().getSummary());
		String jobName = HelloJob.class.getSimpleName() + middleName + jobNameSuffix;
		String triggerName = "triggerFor" + jobName;
		JobDetail job = JobBuilder.newJob(HelloJob.class)
				.withIdentity(jobName, QUARTZ_TEST_GROUP)
				.build();
		
		JobKey jobKey = job.getKey();
		log.info("Preparing job {}/{} with key {}.", QUARTZ_TEST_GROUP, jobName, jobKey);
		if(scheduler.checkExists(jobKey)) {
			if(forceUpdate) {
				log.info("Job {}/{} already exists but force-update mode selected. Deleting it.", QUARTZ_TEST_GROUP, jobName);
				scheduler.deleteJob(jobKey);
			} else {
				log.info("Job {}/{} already exists.", QUARTZ_TEST_GROUP, jobName);
				return jobName;
			}
		}
		if (cronExpression != null && !cronExpression.isEmpty()) {
			log.info("Job {}/{} do not exists, scheduling at {}.", QUARTZ_TEST_GROUP, jobName, cronExpression);
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerName, QUARTZ_TEST_TRIGGER)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
					.build();
			scheduler.scheduleJob(job, trigger);
		}
		return jobName;
	}
	
	public void multipleJobsSameSchedulerTest() throws SchedulerException {
		log.info("Creating scheduler & jobs");
		Scheduler scheduler = new StdSchedulerFactory().getScheduler(); //RETURNS THE SAME SCHEDULER
	    List<String> jobNameList = new ArrayList<>();
		for (int i=0; i<jobNumber ; i++){
			String jobName = helloJobTest(""+i, scheduler);
			jobNameList.add(jobName);
			log.info("New Quartz job {}", jobName);
		}
		
	    log.info("Starting the scheduler");
		scheduler.start();
		
		//Sleep for 10s before delete trigger
		log.info("Waiting for the Quartz jobs");
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		log.info("Deleting the Quartz job and shutting down the scheduler");
		for (String jobName: jobNameList){
			JobKey jobKey = new JobKey(jobName, QUARTZ_TEST_GROUP);
			scheduler.deleteJob(jobKey);
		}
		scheduler.shutdown();
	}

}
