package com.mysite.minsoft.company;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyController {

	 @GetMapping("/")
	    public String Company() {
	        return "index"; 
	    }
	 
	 @GetMapping("/certification")
	    public String certification() {
	        return "certification"; 
	    }
	 @GetMapping("/presentation")
	    public String presentation() {
	        return "presentation"; 
	    }
}
