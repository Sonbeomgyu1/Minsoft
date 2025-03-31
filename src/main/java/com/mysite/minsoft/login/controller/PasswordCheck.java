package com.mysite.minsoft.login.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "refresh11"; // 확인할 비밀번호 입력
        String hashedPassword = "$2a$10$4FwQNSBkq1zDCWuHZVuOEe9eaOEYmr7fEt8lXNa.2w6QrJsrio98a"; // 저장된 해시

        boolean isMatch = encoder.matches(rawPassword, hashedPassword);
        System.out.println("비밀번호 일치 여부: " + isMatch);
    }
}
