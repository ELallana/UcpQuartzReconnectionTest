import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender("STDOUT", ConsoleAppender) {
  withJansi = true		
  encoder(PatternLayoutEncoder) {
	  pattern = "%date %-5level [%thread] %logger{15} -> %msg %n"
  }
}

appender("FILE", RollingFileAppender) {
	file="./target/file.log"
	append = false
	rollingPolicy(SizeAndTimeBasedRollingPolicy) {
		FileNamePattern = "./target/file.log.%d{yyyy-MM-dd}.%i"
		MaxHistory = 10
		maxFileSize = "10MB"
	}
	encoder(PatternLayoutEncoder) {
		pattern = "%date %-5level [%thread] %logger{15} -> %msg %n"
	}
}


logger 'es.amplia', INFO
logger 'org.apache.http', INFO
logger 'org.codehaus.jackson', INFO
logger 'org.springframework.jdbc.core', INFO // sql queries
logger 'monit.es.amplia', INFO
logger 'org.mybatis', INFO
logger 'oracle', trace
logger 'org', INFO		

root(INFO, ["STDOUT", "FILE"])
scan("5 seconds")
