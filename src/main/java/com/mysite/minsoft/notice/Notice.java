package com.mysite.minsoft.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import com.mysite.minsoft.login.model.SiteUser;

@Entity
@ToString
@Getter
@Setter
public class Notice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //자동으로 1씩 증가
    public Long id;

    private String noticetitle;
    private String noticecontent;
    
    @Column
    private LocalDateTime noticedate; // 날짜 수정 가능하도록 변경

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "NAME")  // 'username'을 참조
    private SiteUser createdBy;

 // 작성자 정보
    public SiteUser getCreatedBy() {
        return createdBy;
    }
 // 작성자 정보
    public void setCreatedBy(SiteUser createdBy) {
        this.createdBy = createdBy;
    }
    
    private int viewCount = 0; 

    @PrePersist
    protected void onCreate() {
        this.noticedate = LocalDateTime.now(); // 자동으로 현재 시간 저장
    }
    //조회수증가
    public void incrementViewCount() {
        this.viewCount++;
    }
}