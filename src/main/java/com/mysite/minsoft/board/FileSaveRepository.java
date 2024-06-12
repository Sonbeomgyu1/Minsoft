package com.mysite.minsoft.board;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileSaveRepository extends JpaRepository<FileSave, Long> {
    List<FileSave> findByBoard(Board board);
    List<FileSave> findAllByBoardId(Long boardId); // 기존 메서드를 다중 결과 허용하도록 수정
}
