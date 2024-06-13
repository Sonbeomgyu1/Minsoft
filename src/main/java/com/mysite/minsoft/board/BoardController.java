package com.mysite.minsoft.board;

import java.io.FileNotFoundException;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

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
import org.springframework.web.multipart.MultipartFile;

import com.mysite.minsoft.login.model.SiteUser;
import com.mysite.minsoft.login.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import org.springframework.web.context.support.ServletContextResource;
import javax.servlet.ServletContext;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class BoardController {

	private final BoardService boardService;
	private final UserRepository userRepository;
	private final FileUploadService fileUploadService;
	private final FileSaveRepository fileSaveRepository;

	@Autowired
	public BoardController(BoardService boardService, UserRepository userRepository,
			FileUploadService fileUploadService, FileSaveRepository fileSaveRepository) {
		this.boardService = boardService;
		this.userRepository = userRepository;
		this.fileUploadService = fileUploadService;
		this.fileSaveRepository = fileSaveRepository;
	}

	// 공지사항 목록
	@GetMapping("/board")
	public String board(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<Board> boardPage = boardService.findAll(pageable);

		// 디버깅용 로그 출력
		System.out.println("Current Page: " + page);
		System.out.println("Page Size: " + size);
		boardPage.getContent().forEach(board -> System.out.println("Board ID: " + board.getId()));

		// 게시글 번호 설정
		// 게시글의 총 개수
		/*
		 * long totalElements = boardPage.getTotalElements(); // 현재 페이지에서의 시작 번호 final
		 * long startNumber = totalElements - (page * size) ;
		 */

		// 각 페이지의 첫 번째 게시글 번호는 1부터 시작
		final long startNumber = page * size + 1;

		// 디버깅용 로그 출력
		/* System.out.println("Total Elements: " + totalElements); */
		System.out.println("Start Number: " + startNumber);

		// 모델에 데이터 추가
		model.addAttribute("boards", boardPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", boardPage.getTotalPages());
		model.addAttribute("pageSize", size);
		// 시작 번호 추가
		model.addAttribute("startNumber", startNumber);

		// 각 게시글에 번호 설정
		for (int i = 0; i < boardPage.getContent().size(); i++) {
			Board board = boardPage.getContent().get(i);
			long number = startNumber + i;
			board.setNumber(number);
		}

		// 각 게시글의 번호 디버깅 로그 출력
		for (int i = 0; i < boardPage.getContent().size(); i++) {
			Board board = boardPage.getContent().get(i);
			/* long number = totalElements - (page * size) - i; */
			long number = startNumber + i;
			board.setNumber(number);
			System.out.println("Board ID: " + board.getId() + " - Number: " + number);
		}

		return "board";
	}

	@GetMapping("/boarddetail/{id}") // 보드 디테일 페이지에 해당하는 URL 매핑
	public String boardDetail(@PathVariable Long id, Model model) { // 보드 디테일 컨트롤러 메서드
		Logger logger = LoggerFactory.getLogger(getClass()); // Logger 객체 생성

		// 게시물 정보를 로드합니다.
		Board board = boardService.findById(id); // 주어진 ID에 해당하는 보드 정보를 가져옵니다.
		logger.debug("Board loaded: {}", board); // 디버그 로그: 가져온 보드 정보를 기록합니다.

		// 게시물에 대한 파일 정보를 로드합니다.
		List<FileSave> fileSaves = fileSaveRepository.findAllByBoardId(id); // 주어진 보드 ID에 해당하는 파일 정보를 가져옵니다.
		logger.debug("File saves loaded: {}", fileSaves); // 디버그 로그: 가져온 파일 정보를 기록합니다.

		// 모델에 게시물 정보와 파일 이름을 추가합니다.
		model.addAttribute("board", board); // 모델에 보드 정보를 추가합니다.
		if (fileSaves != null && !fileSaves.isEmpty()) {
			model.addAttribute("originalFileName", fileSaves.get(0).getOriginalFileName()); // 파일이 존재하는 경우, 첫 번째 파일 이름을
																							// 모델에 추가합니다.
		} else {
			model.addAttribute("originalFileName", null); // 파일이 없는 경우, null 값을 모델에 추가합니다.
		}
		model.addAttribute("boardId", id); // 보드 ID를 모델에 추가합니다.

		if (board.isPublic()) { // 만약 보드가 공개되어 있는 경우,
			return "board_detail"; // 보드 디테일 페이지를 반환합니다.
		} else { // 보드가 비공개되어 있는 경우,
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 현재 사용자의 인증 정보를
																									// 가져옵니다.
			if (authentication != null && authentication.isAuthenticated()
					&& !(authentication.getPrincipal() instanceof String
							&& authentication.getPrincipal().equals("anonymousUser"))) {
				return "board_detail"; // 사용자가 인증되어 있고, 익명 사용자가 아닌 경우, 보드 디테일 페이지를 반환합니다.
			} else {
				return "redirect:/login"; // 그 외의 경우, 로그인 페이지로 리다이렉트합니다.
			}
		}
	}

	@GetMapping("/boardwriting")
	public String boardWritingForm(Model model) {
		model.addAttribute("board", new Board());
		return "board_writing";
	}

	// 게시물 생성을 처리하는 메서드(글작성페이지)
	@PostMapping("/boardwriting")
	public String boardWriting(@ModelAttribute Board board, @RequestParam("isPublic") String isPublicParam,
			@RequestParam("boardFile") MultipartFile file) {
		// 현재 사용자 정보 가져오기
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// UserDetails를 구현했는지 확인
		if (principal instanceof UserDetails) {
			// 사용자 이름 가져오기
			String username = ((UserDetails) principal).getUsername();
			// 사용자 이름으로 현재 사용자 찾기
			SiteUser currentUser = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));
			// 작성자 설정
			board.setAuthor(currentUser);
		} else {
			// 사용자가 유효하지 않은 경우 예외 발생
			throw new RuntimeException("유효하지 않은 사용자입니다.");
		}

		// 공개 여부 설정
		boolean isPublic = "1".equals(isPublicParam);
		board.setPublic(isPublic);

		// 새로운 게시물 저장
		boardService.save(board);

		// 파일 업로드 처리
		if (!file.isEmpty()) {
			fileUploadService.uploadFile(file, board);
		}

		// 게시물 목록 페이지로 리디렉션
		return "redirect:/board";
	}

	// 공지사항 수정 폼에서 전송된 데이터 처리
	/*
	 * @PostMapping("/boardedit/{id}/update") public String boardEdit(@PathVariable
	 * Long id, @ModelAttribute Board board,
	 * 
	 * @RequestParam("isPublic") String isPublicParam) { Board existingBoard =
	 * boardService.findById(id);
	 * 
	 * Object principal =
	 * SecurityContextHolder.getContext().getAuthentication().getPrincipal(); if
	 * (principal instanceof UserDetails) { String username = ((UserDetails)
	 * principal).getUsername(); SiteUser currentUser =
	 * userRepository.findByUsername(username) .orElseThrow(() -> new
	 * RuntimeException("유효하지 않은 작성자입니다.")); existingBoard.setAuthor(currentUser); }
	 * else { throw new RuntimeException("유효하지 않은 작성자입니다."); }
	 * 
	 * existingBoard.setTitle(board.getTitle());
	 * existingBoard.setContent(board.getContent());
	 * 
	 * boolean isPublic = "1".equals(isPublicParam);
	 * existingBoard.setPublic(isPublic);
	 * 
	 * boardService.save(existingBoard); return "redirect:/board"; }
	 */

	@GetMapping("/boardedit/{id}")
	public String boardEditForm(@PathVariable Long id, Model model) {
		Board board = boardService.findById(id);
		model.addAttribute("board", board);
		
		// 파일 정보를 가져와 모델에 추가
	    List<FileSave> fileSaves = fileSaveRepository.findAllByBoardId(id);
	    if (fileSaves != null && !fileSaves.isEmpty()) {
	        model.addAttribute("originalFileName", fileSaves.get(0).getOriginalFileName()); // 첫 번째 파일 이름을 사용
	    } else {
	        model.addAttribute("originalFileName", null); // 파일이 없는 경우 null
	    }

		
		return "board_edit_delete";
	}

	// 게시물 수정 처리 메서드
	/*
	 * @PostMapping("/boardedit/{id}/files") public String
	 * boardEditFile(@PathVariable Long id, @ModelAttribute Board board,
	 * 
	 * @AuthenticationPrincipal SiteUser author, @RequestParam("boardFile")
	 * MultipartFile file) { // 편집할 게시물 가져오기 Board existingBoard =
	 * boardService.findById(id); // 작성자 설정 existingBoard.setAuthor(author); // 제목
	 * 설정 existingBoard.setTitle(board.getTitle()); // 내용 설정
	 * existingBoard.setContent(board.getContent()); // 공개 여부 설정
	 * existingBoard.setPublic(board.isPublic());
	 * 
	 * // 파일이 비어 있지 않은 경우 파일 업로드 if (!file.isEmpty()) { // 파일 업로드 및 파일 이름 가져오기
	 * String fileName = fileUploadService.uploadFile(file, existingBoard); // 파일 저장
	 * 정보 생성 FileSave fileSave = new FileSave(); // 파일 이름 설정
	 * fileSave.setFileName(fileName); // 원본 파일 이름 설정
	 * fileSave.setOriginalFileName(file.getOriginalFilename()); // 파일 경로 설정
	 * fileSave.setFilePath("/file-storage/" + fileName); // 게시물에 속한 파일 설정
	 * fileSave.setBoard(existingBoard); // 파일 저장 정보 저장
	 * fileSaveRepository.save(fileSave); }
	 * 
	 * // 수정된 게시물 저장 boardService.save(existingBoard); // 게시물 목록 페이지로 리디렉션 return
	 * "redirect:/board"; }
	 */
	
	@PostMapping("/boardedit/{id}/update")
	public String boardEdit(@PathVariable Long id, @ModelAttribute Board board,
	        @RequestParam("isPublic") String isPublicParam, @RequestParam(value = "boardFile", required = false) MultipartFile file) {
	    // 주어진 ID로 기존 게시물을 찾습니다.
	    Board existingBoard = boardService.findById(id);

	    // 현재 인증된 사용자 정보를 가져옵니다.
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (principal instanceof UserDetails) {
	        // 사용자 이름을 가져옵니다.
	        String username = ((UserDetails) principal).getUsername();
	        // 사용자 이름으로 현재 사용자를 찾습니다.
	        SiteUser currentUser = userRepository.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("유효하지 않은 작성자입니다."));
	        // 게시물의 작성자를 현재 사용자로 설정합니다.
	        existingBoard.setAuthor(currentUser);
	    } else {
	        // 사용자가 유효하지 않은 경우 예외를 발생시킵니다.
	        throw new RuntimeException("유효하지 않은 작성자입니다.");
	    }

	    // 게시물의 제목과 내용을 업데이트합니다.
	    existingBoard.setTitle(board.getTitle());
	    existingBoard.setContent(board.getContent());

	    // 공개 여부를 업데이트합니다.
	    boolean isPublic = "1".equals(isPublicParam);
	    existingBoard.setPublic(isPublic);

	    // 파일이 비어 있지 않은 경우 파일을 업로드합니다.
	    if (file != null && !file.isEmpty()) {
	    	
	    	//청 수정코드
	    	 // 기존 파일 삭제 로직 추가
	        List<FileSave> existingFiles = fileSaveRepository.findAllByBoardId(id);
	        for (FileSave existingFile : existingFiles) {
				/* fileUploadService.deleteFile(existingFile.getFileName()); */
	        	fileUploadService.deleteFile(existingFile.getFilePath());
	            fileSaveRepository.delete(existingFile);
	        }
	    	
	    	
	        // 파일을 업로드하고 파일 이름을 가져옵니다.
	        String filePath = fileUploadService.uploadFile(file, existingBoard);
			/* String fileName = fileUploadService.uploadFile(file, existingBoard); */
	        // 파일 저장 정보를 생성합니다.
	        FileSave fileSave = new FileSave();
	        // 파일 이름을 설정합니다.
	        fileSave.setFileName(file.getOriginalFilename());
			/* fileSave.setFileName(fileName); */
	        // 원본 파일 이름을 설정합니다.
	        fileSave.setOriginalFileName(file.getOriginalFilename());
	        
	     // 파일 경로를 설정합니다.
	        fileSave.setFilePath(filePath);
	        
	        // 게시물에 속한 파일을 설정합니다.
	        fileSave.setBoard(existingBoard);
	        // 파일 저장 정보를 데이터베이스에 저장합니다.
	        fileSaveRepository.save(fileSave);
	    }

	    
	    
	    
	    
	    
	    
	    // 수정된 게시물을 데이터베이스에 저장합니다.
	    boardService.save(existingBoard);
	    // 게시물 목록 페이지로 리디렉션합니다.
	    return "redirect:/board";
	}

	
	
	

	@GetMapping("/download/{boardId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long boardId) {
		// 파일을 리소스로 로드합니다.
		Resource resource = fileUploadService.loadFileAsResource(boardId);

		// 파일의 콘텐츠 유형을 결정합니다.
		String contentType = null;
		try {
			contentType = Files.probeContentType(resource.getFile().toPath());
		} catch (IOException e) {
			// 파일의 콘텐츠 유형을 결정하는 동안 예외가 발생한 경우 기본 콘텐츠 유형으로 설정합니다.
			contentType = "application/octet-stream";
			e.printStackTrace();
		}

		// 파일 유형이 결정되지 않으면 기본 콘텐츠 유형으로 설정합니다.
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/download/{boardId}/view")
	public String downloadFileView(@PathVariable Long boardId, Model model) {
		List<FileSave> fileSaves = fileSaveRepository.findAllByBoardId(boardId); // 메서드 이름 변경

		if (fileSaves == null || fileSaves.isEmpty()) {
			throw new RuntimeException("boardId: " + boardId + "에 대한 리소스를 찾을 수 없습니다.");
		}

		Resource resource = fileUploadService.loadFileAsResource(boardId);
		if (resource == null) {
			throw new RuntimeException("boardId: " + boardId + "에 대한 리소스를 찾을 수 없습니다.");
		}

		String contentType = null;
		try {
			contentType = Files.probeContentType(resource.getFile().toPath());
		} catch (IOException e) {
			contentType = "application/octet-stream";
			e.printStackTrace();
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		String originalFileName = fileSaves.get(0).getOriginalFileName(); // 첫 번째 파일 이름 사용

		model.addAttribute("originalFileName", originalFileName);
		model.addAttribute("boardId", boardId);

		return "downloadView";
	}

	@PostMapping("/boarddelete/{id}")
	public String boardDelete(@PathVariable Long id) {
		Board board = boardService.findById(id);
		List<FileSave> files = fileSaveRepository.findByBoard(board);

		for (FileSave file : files) {
			fileUploadService.deleteFile(file.getFileName());
			fileSaveRepository.delete(file);
		}

		boardService.deleteById(id);
		return "redirect:/board";
	}
}
