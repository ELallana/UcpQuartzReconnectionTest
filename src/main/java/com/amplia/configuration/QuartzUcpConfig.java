package com.amplia.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

@Configuration
@Slf4j
public class QuartzUcpConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        log.info("Hello world from Spring...");
    }
	
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        log.info("Configuring Job factory: {}", jobFactory);

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		Properties properties = propertiesFactoryBean.getObject();
		log.info("Load quartz.properties: {}", properties);
		return properties; 
	}

}
