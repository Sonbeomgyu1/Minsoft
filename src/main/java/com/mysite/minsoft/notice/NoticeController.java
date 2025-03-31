package com.mysite.minsoft.notice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ Spring의 Model 사용
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;

import com.mysite.minsoft.login.model.SiteUser;
import com.mysite.minsoft.login.repository.SiteUserRepository;
import com.mysite.minsoft.login.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeRepository noticeRepository;
	private final SiteUserRepository siteUserRepository;
	
	//글 목록 리스트페이지
	@GetMapping("/notice")
	public String list(@AuthenticationPrincipal User user, Model model) {
	    String username = user.getUsername();  // 로그인한 사용자의 username을 가져옴

	    List<Notice> notices;
	    if ("minsoft".equals(username)) {
	        // 관리자는 모든 글을 조회
	        notices = noticeRepository.findAll();
	    } else {
	        // 일반 사용자는 자신이 작성한 글만 조회
	        notices = noticeRepository.findByCreatedBy_Username(username);
	    }

	    model.addAttribute("notices", notices);
	    return "Notice"; 
	}
	
	



	
	//글쓰기 페이지
	@GetMapping("/noticewrite")
	public String noticeWrite(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName(); // 로그인한 사용자의 username

	    // username을 기반으로 SiteUser 객체를 조회
	    Optional<SiteUser> siteUser = siteUserRepository.findByUsername(username);
	    
	    // 사용자 이름을 모델에 추가
	    if (siteUser.isPresent()) {
	        model.addAttribute("userName", siteUser.get().getName()); // 예: '홍길동'
	    } else {
	        model.addAttribute("userName", "알 수 없음"); // 사용자가 없을 경우 기본값
	    }

	    return "Notice_writing"; 
	}

	
	//글 작성하고 저장
	@PostMapping("/noticeAdd")
	String noticeAdd(@RequestParam(name = "noticetitle") String noticetitle,
	                 @RequestParam(name = "noticecontent") String noticecontent) {

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String name = authentication.getName(); // 로그인한 사용자의 username

	    // 로그인한 사용자를 기반으로 SiteUser 객체를 가져옵니다.
	    Optional<SiteUser> siteUser = siteUserRepository.findByUsername(name);

	    if (siteUser.isPresent()) {
	        Notice notice = new Notice();
	        notice.setNoticetitle(noticetitle);
	        notice.setNoticecontent(noticecontent);
	        notice.setNoticedate(LocalDateTime.now()); // 작성일을 현재 시간으로 설정
	        notice.setViewCount(0); // 기본 조회수 0으로 설정

	        // SiteUser 객체를 createdBy에 설정
	        SiteUser user = siteUser.get();
	        notice.setCreatedBy(user); // SiteUser 객체를 set

	        noticeRepository.save(notice); // 공지사항 저장

	        return "redirect:/notice"; // 공지사항 목록 페이지로 리다이렉트
	    } else {
	        // 로그인되지 않은 경우 오류 처리
	        return "redirect:/login"; // 로그인 페이지로 리다이렉트
	    }
	}


	
//	@GetMapping("noticedetail/{id}")
//	public String noticedetail(@PathVariable(name = "id") Long id, Model model) {
//	    Optional<Notice> result = noticeRepository.findById(id);
//	    
//	    if (result.isPresent()) {
//	        Notice notice = result.get();
//	        model.addAttribute("notices", notice);
//	        
//	        // 공지사항 작성자 정보를 가져옵니다.
//	        SiteUser createdBy = notice.getCreatedBy();
//	        if (createdBy != null) {
//	            model.addAttribute("createdBy", createdBy.getUsername()); // 작성자 이름을 모델에 추가
//	        }
//
//	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
//	            String username = authentication.getName(); // 로그인한 사용자의 username을 가져옵니다.
//	            Optional<SiteUser> siteUser = siteUserRepository.findByUsername(username);
//	            siteUser.ifPresent(user -> model.addAttribute("currentUser", user)); // 사용자 정보가 있을 경우 모델에 추가
//	        } else {
//	            model.addAttribute("currentUser", null); // 로그인되지 않은 경우
//	            System.out.println("로그인되지 않은 사용자입니다.");
//	        }
//
//	        return "NoticeDetail"; // Thymeleaf 템플릿으로 이동
//	    } else {
//	        return "redirect:/notice"; // 공지사항 목록 페이지로 리다이렉트
//	    }
//	}
	// 링크 눌르면 그 상세페이지로 이동
    @GetMapping("/noticedetail/{id}")
    String boarddetail(
        @PathVariable(name = "id") Long id,                     // 게시글 ID
        Model model, 
        @RequestParam(name = "noticedate", required = false) LocalDateTime noticedate  // 게시글 날짜 (필요한 경우)
    ){
        Optional<Notice> result = noticeRepository.findById(id);
        if(result.isPresent()){
            model.addAttribute("notices", result.get());
            return "NoticeDetail";
        } else {
            return "redirect:/notice";
        }
    }
    
    //수정 페이지
    @GetMapping("/noticeedit/{id}")
    public String noticeEdit(@PathVariable Long id, Model model) {
        Optional<Notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            model.addAttribute("notices", notice.get()); // Optional에서 객체를 꺼내서 전달
        } else {
            // Notice가 없으면 다른 페이지로 리다이렉트 또는 오류 메시지 처리
            return "redirect:/notice"; // 예시: 리다이렉트
        }
        return "NoticeEdit";
    }

    
    @PostMapping("/noticeedit/{id}/update")
    public String updateNotice(@PathVariable Long id, 
                               @RequestParam(name = "noticetitle") String noticetitle,
                               @RequestParam(name = "noticecontent") String noticecontent) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            notice.setNoticetitle(noticetitle);
            notice.setNoticecontent(noticecontent);
            noticeRepository.save(notice);  // 수정된 내용 저장
        }
        return "redirect:/notice";  // 수정 후 공지사항 목록 페이지로 리다이렉트
    }
    
    @PostMapping("/noticedelete/{id}")
    public String deleteNotice(@PathVariable Long id) {
        // ID에 해당하는 Notice가 존재하는지 확인
        if (noticeRepository.existsById(id)) {
            noticeRepository.deleteById(id); // Notice 삭제
        }
        return "redirect:/notice"; // 삭제 후 목록 페이지로 이동
    }



}
