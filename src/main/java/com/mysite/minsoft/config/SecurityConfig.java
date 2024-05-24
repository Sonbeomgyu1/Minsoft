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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers("/", "/certification", "/login", "/signup", "/greetingpage", "/history", "/businessdetails", "/solutions", "/itoutsourcingpage",
                        "/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact","/notice/new","/notice","/check-db-connection" ,"/layout", "/images/**", "/assets/**", 
                        "/docs/**", "/pages/**", "/sections/**", "/icon/**", "/public/**").permitAll() // Allow access to these paths without authentication
                .anyRequest().authenticated()) // Require authentication for any other paths
            .formLogin(formLogin -> formLogin
                .loginPage("/login") // Custom login page
                .defaultSuccessUrl("/")) // Redirect to "/" after successful login
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)) // Invalidate session on logout
            .sessionManagement(session -> session
                .invalidSessionUrl("/")); // Redirect to main page when the session is invalid
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
