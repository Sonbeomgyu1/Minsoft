package com.mysite.minsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests(authorizeRequests -> authorizeRequests
						.antMatchers("/boardwriting").authenticated()// 글쓰기 페이지는 인증된 사용자만 접근 가능
						.antMatchers("/", "/certification", "/login", "/signup", "/greetingpage", "/history",
								"/businessdetails", "/solutions", "/itoutsourcingpage",

								"/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact", "/board","/boarddetail/**","/presentation","/boardedit",

								"/check-db-connection", "/layout", "/images/**", "/assets/**",
								"/docs/**", "/pages/**", "/sections/**", "/icon/**", "/public/**")
						.permitAll() // Allow access to these paths without authentication
						.anyRequest().authenticated()) // Require authentication for any other paths
				.formLogin(formLogin -> formLogin
						.loginPage("/login") // Custom login page
						 
						.successHandler(savedRequestAwareAuthenticationSuccessHandler()) // Custom success handler
						.permitAll()) // Allow everyone to see the login page
				.logout(logout -> logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)) // Invalidate session on logout
				.csrf(csrf -> csrf.disable()) // Disable CSRF protection if necessary
				.sessionManagement(session -> session.invalidSessionUrl("/")); // Redirect to main page when the session
																				// is invalid
		return http.build();
	}

	@Bean
	SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		successHandler.setUseReferer(true); // 현재 페이지로 리다이렉트하기 위해 Referer를 사용합니다.
		return successHandler;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
