package com.mysite.minsoft.board;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.minsoft.login.model.SiteUser;
import com.mysite.minsoft.login.repository.UserRepository;

@Controller
public class BoardController {

	private final BoardService boardService;
	private final UserRepository userRepository; // UserRepository 필드 추가
	// 의존성 주입을 통해 BoardService 객체를 초기화

	public BoardController(BoardService boardService, UserRepository userRepository) {
		this.boardService = boardService;
		this.userRepository = userRepository; // UserRepository 초기화
	}

	// 공지사항 목록
	@GetMapping("/board")
	public String board(Model model){
		List<Board> boards = boardService.findAll();
		model.addAttribute("boards", boards);
		return "board";

	}

	// 공지사항 상세 페이지
	@GetMapping("/boarddetail/{id}")
	public String boardDetail(@PathVariable Long id, Model model) {
		Board board = boardService.findById(id);

		// 로그인시 비공개 게시물 볼수있게
		if (board.isPublic()) {
			model.addAttribute("board", board);
			// 공개된 게시글은 인증없이 사용가능
			return "board_detail";
		} else {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()
					&& !(authentication.getPrincipal() instanceof String
							&& authentication.getPrincipal().equals("anonymousUser"))) {
				model.addAttribute("board", board);
				// 인증된 사용자는 비공개 게시글 접근 가능
				return "board_detail";
			} else {
				// 비공개 게시글은 인증 필요
				return "redirect:/login";
			}
		}

	}

	// 공지사항 글쓰기 폼
	@GetMapping("/boardwriting")
	public String boardWritingForm(Model model) {
		model.addAttribute("board", new Board());
		return "board_writing";
	}

	// 공지사항 글쓰기 폼에서 전송된 데이터를 처리
	@PostMapping("/boardwriting")
	public String boardWriting(@ModelAttribute Board board, @RequestParam("isPublic") String isPublicParam) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			SiteUser currentUser = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("유효하지 않은 작성자입니다."));
			board.setAuthor(currentUser);
		} else {
			throw new RuntimeException("유효하지 않은 작성자입니다.");
		}

		// 디버깅용 로그 출력
		System.out.println("Title: " + board.getTitle());
		System.out.println("Content: " + board.getContent());
		System.out.println("isPublicParam: " + isPublicParam); // 추가된 디버깅 로그
		// System.out.println("Public: " + board.isPublic());
		System.out.println("Author: " + (board.getAuthor() != null ? board.getAuthor().getName() : "null"));

		// 추가 확인 단계
		if (board.isPublic()) {
			System.out.println("게시글이 비공개 상태로 설정되었습니다.");
		} else {
			System.out.println("게시글이 공개 상태로 설정되었습니다.");
		}

		// Form에서 넘어온 isPublic 값을 boolean으로 변환하여 설정
		// board.setIsPublicFromString(isPublic);
		boolean isPublic = "1".equals(isPublicParam); // "1"을 선택된 값으로 가정
		board.setPublic(isPublic); // `setIsPublicFromString` 대신 `setPublic` 사용

		// 저장 전 상태 확인
		System.out.println("Board before save: " + board);
		// 새로운 게시글 저장
		boardService.save(board);
		// 저장 후 상태 확인
		System.out.println("Board after save: " + board);

		// 저장 후 공지사항 목록 페이지로 리다이렉트
		return "redirect:/board";
	}

	// 공지사항 수정 폼
	@GetMapping("/boardedit/{id}")
	public String boardEditForm(@PathVariable Long id, Model model) {
		Board board = boardService.findById(id); // 게시글 ID를 통해 특정 게시글 조회
		model.addAttribute("board", board); // 조회된 게시글을 모델에 추가
		return "board_edit_delete"; // board_edit_delete.html 뷰 페이지를 반환
	}

	// 공지사항 수정 폼에서 전송된 데이터 처리
	@PostMapping("/boardedit/{id}")
	public String boardEdit(@PathVariable Long id, @ModelAttribute Board board,
			@AuthenticationPrincipal SiteUser author) {
		// 기존 게시글 조회
		Board existingBoard = boardService.findById(id);
		// 작성자를 현재 인증된 사용자로 설정
		existingBoard.setAuthor(author);
		// 게시글 제목 업데이트
		existingBoard.setTitle(board.getTitle());
		// 게시글 내용 업데이트
		existingBoard.setContent(board.getContent());
		// 공개여부 업데이트
		existingBoard.setPublic(board.isPublic());
		// 수정된 게시글 저장
		boardService.save(existingBoard);
		// 저장 후 공지사항목록 페이지로 리다이렉트
		return "redirect:/board";

	}

	// 공지사항 삭제
	@PostMapping("/boarddelete/{id}")
	public String boardDelete(@PathVariable Long id) {
		// 게시글 ID를 통해 특정 게시글 삭제
		boardService.deletedById(id);
		// 삭제후 공지사항목록 페이지로 리다이렉트
		return "redirect:/board";
	}

}





/*
 * @GetMapping("/board") //공지사항 main 컨트롤러 public String board() { return
 * "board"; }
 * 
 * @GetMapping("/boarddetail") //공지사항 상세페이지 컨트롤러 public String boardDetail() {
 * return "board_detail"; }
 * 
 * @GetMapping("/boardwriting") //공지사항 글쓰기 컨트롤러 public String boardwriting() {
 * return "board_writing"; }
 * 
 * @GetMapping("/boardedit") //공지사항 수정삭제 public String boardEdit() { return
 * "board_edit_delete"; }
 */


/*
 * @PostMapping("/boardwriting") public String boardWriting(@ModelAttribute
 * Board board , @AuthenticationPrincipal SiteUser currentUser) {
 * 
 * // 작성자 이름을 통해 SiteUser 객체를 찾아 설정 board.setAuthor(currentUser);
 * 
 * if (currentUser != null) { throw new RuntimeException("유효하지 않은 작성자입니다."); }
 * 
 * 
 * // 디버그용 로그 출력 // 디버그용 로그 출력 System.out.println("CurrentUser: " +
 * currentUser); System.out.println("Title: " + board.getTitle());
 * System.out.println("Content: " + board.getContent());
 * System.out.println("Public: " + board.isPublic());
 * System.out.println("Author: " + (board.getAuthor() != null ?
 * board.getAuthor().getUsername() : "null"));
 * 
 * boardService.save(board); // 새로운 게시글 저장 return "redirect:/board"; // 저장 후
 * 공지사항 목록 페이지로 리다이렉트 }
 */

/*
 * //공지사항 글쓰기 폼에서 전송된 데이터를 처리
 * 
 * @PostMapping("/boardwriting") public String boardWriting(@ModelAttribute
 * Board board, @AuthenticationPrincipal SiteUser author) {
 * board.setAuthor(author); // 작성자를 현재 인증된 사용자로 설정 // 제목이 제대로 전달되었는지 확인
 * System.out.println("Title: " + board.getTitle()); //내용 제대로 전달되었는지 확인
 * System.out.println("Content: " + board.getContent());
 * boardService.save(board); // 새로운 게시글 저장 return "redirect:/board"; // 저장 후
 * 공지사항 목록 페이지로 리다이렉트 }
 */
