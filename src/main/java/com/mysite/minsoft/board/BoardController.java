package com.mysite.minsoft.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {

	
	 @GetMapping("/board") //공지사항 컨트롤러
	    public String board() {
	        return "board"; 
	    }
}
