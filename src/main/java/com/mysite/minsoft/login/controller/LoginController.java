package com.mysite.minsoft.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.minsoft.login.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "loginform";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam(name = "error", required = false) String error,
                               Model model) {
        // 인증 처리
        UserDetails userDetails = userService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/"; // 로그인 성공 시 리다이렉트할 페이지로 변경
        } catch (Exception e) {
            model.addAttribute("loginError", true);
            return "loginform"; // 로그인 실패 시 다시 로그인 폼으로 이동
        }
    }
}
