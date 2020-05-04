package it.course.myblog.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {                                   
    
   
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.securitySchemes(Collections.singletonList(new ApiKey("Bearer", "Authorization", "header")))
                .securityContexts(Collections.singletonList(
                		SecurityContext.builder()
                            .securityReferences(
                                Collections.singletonList(SecurityReference.builder()
                                    .reference("Bearer")
                                    .scopes(new AuthorizationScope[0])
                                    .build()
                                )
                            )
                            .build())
                )
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("it.course.myblog"))
                .paths(PathSelectors.regex("/.*"))
                .build().apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() {
        Contact contact = new Contact("Technical Support", "https://www.myblog.it/about", "tech@myblog.com");
        return new ApiInfoBuilder()
                .title("MyBlog API")
                .description("MyBlog API with Swagger 2")
                .termsOfServiceUrl("http://www.myblog.it/terms")
                .contact(contact)
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .version("1.0.0")
                .build();
    }
    
}
