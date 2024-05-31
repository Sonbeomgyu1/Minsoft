package com.mysite.minsoft.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {

	
	 @GetMapping("/board") //공지사항 main 컨트롤러
	    public String board() {
	        return "board"; 
	    }
	 
	 @GetMapping("/boarddetail") //공지사항 상세페이지 컨트롤러
	    public String boardDetail() {
	        return "board_detail"; 
	    }	
	 
	 @GetMapping("/boardwriting") //공지사항 글쓰기 컨트롤러
	    public String boardwriting() {
	        return "board_writing"; 
	    }
	 
	 @GetMapping("/boardedit") //공지사항 수정삭제
	    public String boardEdit() {
	        return "board_edit_delete"; 
	    }
	 
}
