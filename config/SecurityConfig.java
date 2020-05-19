package it.course.myblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.course.myblog.security.CustomUserDetailsService;
import it.course.myblog.security.JwtAuthenticationEntryPoint;
import it.course.myblog.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
	securedEnabled = true, 	// @Secured("ROLE_ADMIN")
	jsr250Enabled = true,	// @RolesAllowed("ADMIN","EDITOR")
	prePostEnabled = true 	// @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_MANAGING_EDITOR')")
)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	JwtAuthenticationEntryPoint unauthorizedHandler;
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();	
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		
		authenticationManagerBuilder
		.userDetailsService(customUserDetailsService)
		.passwordEncoder(passwordEncoder());
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
		
	}
	/*
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(
	  ServerHttpSecurity http) {
	    return http.authorizeExchange()
	      .pathMatchers("/actuator/**").permitAll()
	      .anyExchange().authenticated()
	      .and().build();
	}
	*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		http
			.authorizeRequests()
				.antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
				.antMatchers("/post/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EDITOR")
				.anyRequest().authenticated();
		*/
		http
		.cors().and().csrf().disable()
		.exceptionHandling()
		.authenticationEntryPoint(unauthorizedHandler)
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()		
		.antMatchers(
			"/",
			"/**/*.css",
			"/**/*.js",
			"/**/*.html",
			"/**/*.jpg",
			"/favicon.ico",
			"/**/*.svg",
			"/**/*.png",
			"/**/*.gif",
			"/webjars/**"
			).permitAll()
		.antMatchers("/api/auth/**",
				"/posts/view-all-published-posts", 
				"/posts/view-single-post/**",
				"/posts/get-posts-by-tag/**",
				"/posts/get-posts-by-tags/**",
				"/posts/search",
				"/postviewed/set-post-viewer-end/**",
				"/posts/view-single-post-with-image/**",
				"/posts/view-all-published-posts-paged/**", "/actuator/**", "/custom-path/**")
			.permitAll()		
		.antMatchers("/v2/api-docs", 
				"/configuration/ui", 
				"/swagger-resources", 
				"/configuration/security", 
				"/swagger-resources/configuration/ui", 
				"/swagge‌​r-ui.html", 
				"/swagger-resources/configuration/security")
		.permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.formLogin().loginPage("/api/auth/login")
		.permitAll().and().logout()
		.invalidateHttpSession(true)
		.clearAuthentication(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
		.logoutSuccessUrl("/api/auth/login?logout")		
		.permitAll();
		
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	
	

}
