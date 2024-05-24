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
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
        		.antMatchers("/", "/certification", "/login", "/greetingpage", "/history", "/businessdetails", "/solutions", "/itoutsourcingpage",
                        "/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact", "/layout", "/images/**", "/assets/**", 
                        "/docs/**", "/pages/**", "/sections/**", "/icon/**", "/public/**").permitAll() // 접근 허용 경로 설정
        		.anyRequest().authenticated())
        .formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/"))
        .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true))
        .sessionManagement((session) -> session
        	.invalidSessionUrl("/")); // 세션이 유효하지 않을 때 메인 페이지로 리다이렉트
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/certification", "/greetingpage", "/history", "/businessdetails", "/solutions", "/itoutsourcingpage",
                        "/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact", "/layout","/notice","/presentation", "/notice/new", "/images/**", "/assets/**", 
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
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
