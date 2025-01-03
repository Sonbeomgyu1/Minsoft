package com.mysite.minsoft.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("layout")  //상세페이지 틀 controller
    public String layout() {
        return "layout"; 
    }
	@GetMapping("header")  //hreader 틀 controller
    public String header() {
        return "header"; 
    }
	@GetMapping("footer")  //footer 틀 controller
    public String footer() {
        return "footer"; 
    }


	//메인페이지
	@GetMapping("/")
	public String Main() {
		return "main";
	}
	
	//템플릿 페이지
	@GetMapping("/presentation")
	public String Presentation() {
		return "presentation";
	}
}
