package it.course.myblog;

import java.util.List;

import org.apache.tomcat.util.net.AbstractEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
//import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
//@PropertySource("file:${myPath}/application.properties") // if application.properties is out of project + @Autowired Enviroment env;
//@PropertySource("classPath:application.properties") // if application.properties is into project + @Autowired Enviroment env;
public class MyblogApplication {
	
	

	@Value("${app.test.profile}")
	String x;
	
	@Autowired
	Environment env;
	
	
	public static void main(String[] args) {
		SpringApplication.run(MyblogApplication.class, args);
	}
	
	@Profile("dev")
	@Bean
	public String testProfile() {
		
		String y = x.toLowerCase();
		log.info(y);
		log.debug(y);
		log.error(y);
		log.trace(y);
		return y;
	}
	
	@Profile("dev")
	@Bean
	public String testProfile2() {
		
		String y = env.getProperty("app.test.profile"); 
		log.info(x);
		//System.out.println(y);
		return y;
	}

}
