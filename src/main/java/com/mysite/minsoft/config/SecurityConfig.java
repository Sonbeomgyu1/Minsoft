package com.mysite.minsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/certification", "/login", "/greetingpage", "/history", "/businessdetails", "/solutions", "/itoutsourcingpage",
                        "/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact", "/notice/new", "/notice","/presentation", "/check-db-connection", "/layout", "/images/**", "/assets/**", 
                        "/docs/**", "/pages/**", "/sections/**", "/icon/**", "/public/**").permitAll() // Allow access to these paths without authentication
                .anyRequest().authenticated() // Require authentication for any other paths
                .and()
            .formLogin()
                .loginPage("/login") // Custom login page
                .defaultSuccessUrl("/") // Redirect to "/" after successful login
                .permitAll() // Allow access to login page without authentication
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }

    @Bean
    @Override
    @Primary
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
        userDetailsService.setDataSource(dataSource);
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
