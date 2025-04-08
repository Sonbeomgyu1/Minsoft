package com.mysite.minsoft.esg;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EsgController {
	
	@GetMapping("/esgcompliance") //준법경영 페이지
    public String esgcompliance() {
        return "esgcompliance"; 
    }

	@GetMapping("/esgprivacypolicy") //민소프트 윤리헌장 페이지
    public String esgprivacypolicy() {
        return "esgprivacypolicy"; 
    }
}
