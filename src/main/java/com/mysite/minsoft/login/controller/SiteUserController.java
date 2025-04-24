package com.mysite.minsoft.login.controller;

import com.mysite.minsoft.login.model.SiteUser;
import com.mysite.minsoft.login.repository.SiteUserRepository;
import com.mysite.minsoft.login.repository.UserRepository;
import com.mysite.minsoft.login.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class SiteUserController {

    @Autowired
    private SiteUserRepository siteUserRepository;
    
    @Autowired
    private UserService userService;

    // 모든 회원 목록 조회
    @GetMapping
    public List<SiteUser> getAllUsers() {
        return siteUserRepository.findAll();
    }

    @GetMapping("/users")
    public String userList(Model model, Authentication authentication) {
        String loginUser = authentication.getName();

        if (!"minsoft".equals(loginUser)) {
            return "redirect:/access-denied"; // 접근 차단 페이지로 리디렉트
        }

        List<SiteUser> users = userService.getAllUsers()
                                          .stream()
                                          .sorted(Comparator.comparing(SiteUser::getUsername)) // id 기준 정렬
                                          .collect(Collectors.toList());

        model.addAttribute("users", users);
        return "user_list";
    }

    @PostMapping("/update/{username}")
    public String updateUserName(@PathVariable String username, @RequestParam String name) {
        Optional<SiteUser> optionalUser = userService.getByUsername(username);
        if (optionalUser.isPresent()) {
            SiteUser user = optionalUser.get();
            user.setName(name);
            siteUserRepository.save(user);
        }
        // ✅ 변경되었다는 플래그를 URL 파라미터로 전달
        return "redirect:/admin/users?updated=true";
    }

    @PostMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username, Authentication authentication) {
        String loginUser = authentication.getName();

        if (!"minsoft".equals(loginUser)) {
            return "redirect:/access-denied";
        }

        userService.deleteUser(username);
        return "redirect:/admin/users";
    }


}
