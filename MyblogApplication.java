package it.course.myblog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;


@Slf4j
//@PropertySource("file:${myPath}/application.properties") // if application.properties is out of project + @Autowired Enviroment env;
//@PropertySource("classPath:application.properties") // if application.properties is into project + @Autowired Enviroment env;
@SpringBootApplication
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
		//System.out.println(y);
		log.info(y);
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
