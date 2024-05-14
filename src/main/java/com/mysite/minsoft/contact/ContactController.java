package com.mysite.minsoft.contact;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {

	
	 @GetMapping("/contact") //오시는길 controller
	    public String contact() {
	        return "contact"; 
	    }
}
