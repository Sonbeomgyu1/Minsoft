package com.mysite.minsoft.board;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.minsoft.login.model.SiteUser;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "board")
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "AUTHOR", referencedColumnName = "NAME")
    private SiteUser author;
    
    //@Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "ISPUBLIC", nullable = false)
    private boolean isPublic;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;
    
  
    //조회수 컬럼
    @Column(name = "VIEW_COUNT", nullable = false)
    private int viewCount = 0;


    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    //번호필드 추가
    @Transient
    private Long number;
    
    //조회수증가
    public void incrementViewCount() {
        this.viewCount++;
    }
    
}
