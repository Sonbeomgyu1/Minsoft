package com.mysite.minsoft.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	 @GetMapping("/loginform") //로그인 폼 컨트롤러 
	    public String loginform() {
	        return "loginform"; 
	    }
}
