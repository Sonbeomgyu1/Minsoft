package com.mysite.minsoft.login.controller;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.mysite.minsoft.login.model.UserCreateForm;
import com.mysite.minsoft.login.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "signup";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup";
        }
        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getPassword1(), userCreateForm.getName());
            // 회원가입 후 자동 로그인 처리
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userCreateForm.getUsername(), userCreateForm.getPassword1()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup";
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginRender() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        } else {
            return "redirect:/";
        }
    }

}
