package com.mysite.minsoft.notice;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	// 공지사항 목록표시
	@GetMapping("/notice")
	public String getAllNotices(Model model) {
		List<Notice> notices = noticeService.getAllNotices();
		model.addAttribute("notices", notices);
		return "notice";
	}

	// 공지사항 생성 폼 표시
	@GetMapping("/notice/new")
	public String createNoticeForm(Model model) {
		model.addAttribute("notice", new Notice());
		return "create_notice";
	}

	// 새로운 공지사항 저장

	@PostMapping("/notice")
	public String saveNotice(@ModelAttribute Notice notice) {
		noticeService.saveNotice(notice);
		// 공지사항 목록 페이지로 리다이렉트
		return "redirect:/notice";
	}

	// 공지사항 삭제
	@GetMapping("/notices/delete/{id}")
	public String deleteNotice(@PathVariable Long id) { // NoticeService를 통해 주어진 ID의 공지사항을 삭제합니다.
		noticeService.deleteNotice(id); // 공지사항 목록 페이지로 리다이렉트합니다. return
		return "redirect:/notice";
	}

	// 유효성검사

	/*
	 * @PostMapping("/notice") public String saveNotice(@ModelAttribute @Valid
	 * Notice notice, BindingResult result, Model model) { if (result.hasErrors()) {
	 * return "create_notice"; } noticeService.saveNotice(notice); return
	 * "redirect:/notice"; }
	 */

}
