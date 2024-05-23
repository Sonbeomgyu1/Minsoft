package com.mysite.minsoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.mysite.minsoft.login.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/certification", "/greetingpage", "/history", "/businessdetails", "/solutions", "/itoutsourcingpage",
                        "/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact","/check-db-connection","/login", "/layout", "/images/**", "/assets/**", 
                        "/docs/**", "/pages/**", "/sections/**", "/icon/**", "/public/**").permitAll() // 접근 허용 경로 설정
                .anyRequest().authenticated() // 나머지 경로는 인증 필요
                .and()
            .formLogin()
                .loginPage("/login") // 커스텀 로그인 페이지 설정
                .defaultSuccessUrl("/", true) // 로그인 성공 시 이동할 페이지 설정
                .successHandler(authenticationSuccessHandler()) // 로그인 성공 핸들러 설정
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/"); // 로그인 성공 시 이동할 URL 설정
    }
}
