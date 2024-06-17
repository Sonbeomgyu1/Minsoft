package com.mysite.minsoft.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 현재 요청 URL을 세션에 저장
        String redirectUrl = request.getRequestURI();
        request.getSession().setAttribute("redirectUrl", redirectUrl);
        System.out.println("Storing URL in session: " + redirectUrl); // 시스아웃으로 데이터 들어오나 확인
        super.commence(request, response, authException);
    }
}
