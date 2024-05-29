package com.mysite.minsoft.notice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long noticeNo;

	@Column(nullable = false)
	private String noticeTitle;

	@Column(nullable = false, length = 4000)
	private String noticeContent;

	private String name;

	public Notice(String noticeTitle, String noticeContent, String name) {
		this.noticeTitle = noticeTitle;
		this.noticeContent = noticeContent;
		this.name = name;
	}

}

//엔티티 유효성검사
/*
 * @Id
 * 
 * @GeneratedValue(strategy = GenerationType.IDENTITY) private Long noticeNo;
 * 
 * @Column(nullable = false)
 * 
 * @NotBlank(message = "제목은 필수 항목입니다.") private String noticeTitle;
 * 
 * @Column(nullable = false, length = 4000)
 * 
 * @NotBlank(message = "내용은 필수 항목입니다.")
 * 
 * @Size(max = 4000, message = "내용은 4000자를 넘을 수 없습니다.") private String
 * noticeContent;
 * 
 * private String name;
 * 
 * public Notice(String noticeTitle, String noticeContent, String name) {
 * this.noticeTitle = noticeTitle; this.noticeContent = noticeContent; this.name
 * = name; }
 */
