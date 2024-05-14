package com.mysite.minsoft.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("layout")  //상세페이지 틀 controller
    public String layout() {
        return "layout"; 
    }



	//메인페이지
	@GetMapping("/")
	public String Main() {
		return "main";
	}
	

}
