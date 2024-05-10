package com.mysite.minsoft.company;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyController {

	 @GetMapping("/index")
	    public String Company() {
	        return "index"; 
	    }

}
