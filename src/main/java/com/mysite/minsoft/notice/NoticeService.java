package com.mysite.minsoft.notice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepository;
	
	//모든공지사항 조회
	//noticeRepository의 findAll() 메서드를 호출하여 모든 공지사항을 리스트로 반환
	public List<Notice> getAllNotices(){
		return noticeRepository.findAll();
	}
	
	//공지사항 저장
	// noticeRepository의 save() 메서드를 호출하여 공지사항을 저장하고 저장된 공지사항을 반환
	public Notice saveNotice(Notice notice) {
		return noticeRepository.save(notice);
	}
	
	//공지사항 삭제
	// noticeRepository의 save() 메서드를 호출하여 공지사항을 저장하고 저장된 공지사항을 반환
	public void deleteNotice(Long id) {
		noticeRepository.deleteById(id);
	}
	
}
