package com.mysite.minsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .antMatchers("/","/certification","/greetingpage","/history","/businessdetails","/solutions","/itoutsourcingpage",
                    		"/consulting","/sism","/recruitmentinfomation","/welfare","/contact","/layout","/images/**","/assets/**","/docs/**","/pages/**","/sections/**","icon/**","/public/**").permitAll() // 접근 허용 경로 설정
                    .anyRequest().authenticated() // 나머지 경로는 인증 필요
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login") // 커스텀 로그인 페이지 설정
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
