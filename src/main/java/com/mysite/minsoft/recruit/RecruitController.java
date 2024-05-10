package com.mysite.minsoft.recruit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecruitController {

	
	 
	 @GetMapping("/recruitmentinfomation") //채용안내 controller
	    public String certification() {
	        return "recruitmentinfomation"; 
	    }
}
