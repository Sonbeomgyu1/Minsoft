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
	 
	 //모든 게시글 목록 조회
	 public List<Board> findAll(){
		 return boardRepository.findAll();
	 }
	 
	 
	 //ID를 통해 특정 게시글을 조회
	 public Board findById(Long id) {
		 Optional<Board> board = boardRepository.findById(id);
		 if(board.isPresent()) {
			 return board.get();
		 }else {
			 //게시글이 없는 경우
			 throw new RuntimeException("게시글을 찾을수 없습니다.");
		 }
	 }
	 	 
	 //새로운 게시글 저장
	 public Board save(Board board) {
		 return boardRepository.save(board);
	 }
	 
	 //ID를 통해 특정 게시글을 삭제
	 public void deletedById(Long id) {
		 boardRepository.deleteById(id);
	 }
	 
	 
	 //모든게시글 목록 조회(페이징 처리)
	 public Page<Board> findAll(Pageable pageable){
	        return boardRepository.findAll(pageable);
	    }
}
