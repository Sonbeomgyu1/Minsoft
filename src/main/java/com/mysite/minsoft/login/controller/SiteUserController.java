package com.mysite.minsoft.login.controller;

import com.mysite.minsoft.login.model.SiteUser;
import com.mysite.minsoft.login.repository.SiteUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class SiteUserController {

    @Autowired
    private SiteUserRepository siteUserRepository;

    @GetMapping
    public List<SiteUser> getAllUsers() {
        return siteUserRepository.findAll();
    }
}
