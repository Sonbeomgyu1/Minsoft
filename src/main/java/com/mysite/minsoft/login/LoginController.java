package com.mysite.minsoft.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	 @GetMapping("/admin")
	    public String admin() {
	        return "admin";
	    }

	    @GetMapping("/login")
	    public String login() {
	        return "loginform";
	    }
	
}
