package com.mysite.minsoft.recruit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecruitController {

	 @GetMapping("/")
	    public String Company() {
	        return "index"; 
	    }
	 
	 @GetMapping("/recruitmentinfomation") //채용안내 controller
	    public String certification() {
	        return "recruitmentinfomation"; 
	    }
}
