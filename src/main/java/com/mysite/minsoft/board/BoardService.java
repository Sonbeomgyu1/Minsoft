package com.mysite.minsoft.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

	private final BoardRepository boardRepository;

	public BoardService(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;

	}

	// 모든 게시글 목록 조회
	/*
	 * public List<Board> findAll(){ return boardRepository.findAll(); }
	 */

	// 모든 게시글 목록 조회(페이징 처리)
	public Page<Board> findAll(Pageable pageable) {
		return boardRepository.findAll(pageable);
	}

	// ID를 통해 특정 게시글을 조회 및 조회수 증가
	/*
	 * public Board findById(Long id) { Optional<Board> board =
	 * boardRepository.findById(id); if(board.isPresent()) { return board.get();
	 * Board existingBoard = board.get(); //조회수 증가
	 * existingBoard.incrementViewCount(); boardRepository.save(existingBoard);
	 * return existingBoard; }else { //게시글이 없는 경우 throw new
	 * RuntimeException("게시글을 찾을수 없습니다."); } }
	 */

	// ID를 통해 특정 게시글을 조회
    public Board findById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isPresent()) {
            return board.get();
        } else {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
    }

    // 조회수 증가
    //조회수 증가를 별도의 메서드로 분리하고, 게시글 상세 페이지를 조회할 때만 해당 메서드를 호출하도록 변경
    public void incrementViewCount(Long id) {
        Board board = findById(id);
        board.incrementViewCount();
        boardRepository.save(board);
    }
		
	
	// 새로운 게시글 저장
	public Board save(Board board) {
		return boardRepository.save(board);
	}

	// ID를 통해 특정 게시글을 삭제
	public void deleteById(Long id) {
		boardRepository.deleteById(id);
	}

}
