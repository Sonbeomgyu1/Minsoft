package com.mysite.minsoft.board;

import org.springframework.web.bind.annotation.GetMapping;

public class BoardController {

	
	 @GetMapping("/bo") //공지사항 컨트롤러
	    public String board() {
	        return "bo"; 
	    }
}
