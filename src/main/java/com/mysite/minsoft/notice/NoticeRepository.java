package com.mysite.minsoft.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	
	    List<Notice> findByCreatedBy_Username(String username);  // SiteUser의 username을 기준으로 조회
	

}
