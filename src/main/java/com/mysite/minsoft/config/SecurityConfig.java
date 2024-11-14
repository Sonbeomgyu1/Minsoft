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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers("/boardwriting").authenticated() // 글쓰기 페이지는 인증된 사용자만 접근 가능
                .antMatchers("/", "/certification", "/login", "/signup", "/greetingpage", "/history", "/downloadView","/error",
                        "/businessdetails", "/solutions", "/itoutsourcingpage",
                        "/consulting", "/sism", "/recruitmentinfomation", "/welfare", "/contact", "/board", "/boarddetail/**", "/presentation", "/boardedit/**",
                        "/check-db-connection", "/layout", "/images/**", "/assets/**",
                        "/docs/**", "/pages/**", "/sections/**", "/icon/**", "/public/**")
                .permitAll() // 인증 없이 접근 가능한 경로 설정
                .anyRequest().authenticated()) // 나머지 요청은 인증 필요
            .formLogin(formLogin -> formLogin
                .loginPage("/login") // 커스텀 로그인 페이지 지정
                .successHandler(authenticationSuccessHandler()) // 로그인 성공 후 핸들러 설정
                .permitAll()) // 모든 사용자가 로그인 페이지 접근 가능
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)) // 세션 무효화
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (필요에 따라 설정)
            .sessionManagement(session -> session
                .invalidSessionUrl("/")) // 세션 만료 시 메인 페이지로 리다이렉트 설정
            .requestCache(requestCache -> requestCache
                .requestCache(new HttpSessionRequestCache())) // HttpSessionRequestCache 설정
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint())); // 커스텀 엔트리 포인트 설정
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                org.springframework.security.core.Authentication authentication)
                    throws IOException, ServletException {
                HttpSession session = request.getSession();
                String redirectUrl = (String) session.getAttribute("redirectUrl");
                System.out.println("Redirecting to URL: " + redirectUrl); // 로그 추가
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("redirectUrl"); // 사용한 후 세션에서 제거
                    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                } else {
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        };
    }

    @Bean
    public CustomLoginUrlAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomLoginUrlAuthenticationEntryPoint("/login");
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication)
                    throws IOException, ServletException {
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
