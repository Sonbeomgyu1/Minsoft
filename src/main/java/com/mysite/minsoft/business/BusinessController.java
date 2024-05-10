package com.mysite.minsoft.business;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BusinessController {
	 
	 @GetMapping("/businessdetails") //사업내용 controller
	    public String businessdetails() {
	        return "businessdetails"; 
	    }
	 
	 @GetMapping("/solution") //사업내용 controller
	    public String solution() {
	        return "solution"; 
	    }
	 
	
	 
}
