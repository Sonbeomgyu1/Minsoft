package com.mysite.minsoft.board;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FILESAVE")
public class FileSave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARDID")
    private Board board;

    @Column(name = "FILENAME")
    private String fileName;

    @Column(name = "FILEPATH")
    private String filePath;

    @Column(name = "ORIGINAL_FILENAME") 
    private String originalFileName;
}
