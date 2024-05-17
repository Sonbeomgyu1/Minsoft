package com.mysite.minsoft.company;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyController {

	 
	 
	 @GetMapping("/certification") //기술인증 controller
	    public String certification() {
	        return "certification"; 
	    }

	 @GetMapping("/greetingpage") //인사말 controller
	 	public String greeting() {
		 	return "greetingpage";
	 }
	 
	 @GetMapping("/history") //회사연혁 controller
	 	public String history() {
		 	return "history";
	 }
}
