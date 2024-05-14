package com.mysite.minsoft.business;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BusinessController {
	 
	 @GetMapping("/businessdetails") //사업내용 controller
	    public String businessdetails() {
	        return "businessdetails"; 
	    }
	 

	 @GetMapping("/solutions") //솔루션 controller
	    public String solutions() {
	        return "solutions"; 
	    }	 

	 @GetMapping("/solution") //솔루션 controller
	    public String solution() {
	        return "solution"; 

	    }

		
	@GetMapping("/itoutsourcingpage") //IT 아웃소싱 controller 
		public String itoutsourcingpage() { 
		return "itoutsourcingpage";
		}
		 




	 @GetMapping("/consulting") //컨설팅 controller
	    public String consulting() {
	        return "consulting"; 
	    }



	 @GetMapping("/sism") //SI/SM controller
	    public String sism() {
	        return "sism"; 
	    }

}
