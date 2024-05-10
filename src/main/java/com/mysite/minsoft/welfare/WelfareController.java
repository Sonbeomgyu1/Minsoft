package com.mysite.minsoft.welfare;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelfareController {

	@GetMapping("/welfare")
		public String Welfare() {
		return "welfare";
	}
	
}
