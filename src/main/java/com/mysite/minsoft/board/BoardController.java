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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import org.springframework.web.context.support.ServletContextResource;
import javax.servlet.ServletContext;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

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

		// 게시물 정보를 로드합니다. 조회수 증가와 저장을 BoardService에서 처리
		Board board = boardService.findById(id); // 주어진 ID에 해당하는 보드 정보를 가져옵니다.
		logger.debug("Board loaded: {}", board); // 디버그 로그: 가져온 보드 정보를 기록합니다.

		// 게시물에 대한 파일 정보를 로드합니다.
		List<FileSave> fileSaves = fileSaveRepository.findAllByBoardId(id); // 주어진 보드 ID에 해당하는 파일 정보를 가져옵니다.
		logger.debug("File saves loaded: {}", fileSaves); // 디버그 로그: 가져온 파일 정보를 기록합니다.
		
		//----------수정중-------------
		/*
		 * // 조회수를 증가시킵니다. board.incrementViewCount(); //변경된 게시글 저장
		 * boardService.save(board); // 로그 추가
		 * logger.debug("boardDetail 메서드 호출됨 - ID: {}", id);
		 */
		 // 조회수를 증가시킵니다.
	    boardService.incrementViewCount(id);
		

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

	@PostMapping("/boardwriting")
	public String boardWriting(@ModelAttribute Board board,
	                           @RequestParam("isPublic") String isPublicParam,
	                           @RequestParam("boardFile") MultipartFile file,
	                           Model model) {
	    // Get current user information
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (!(principal instanceof UserDetails)) {
	        throw new RuntimeException("Invalid user.");
	    }
	    
	    // Get the current user
	    String username = ((UserDetails) principal).getUsername();
	    SiteUser currentUser = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("Invalid user."));
	    
	    // Set author and public status
	    board.setAuthor(currentUser);
	    boolean isPublic = "1".equals(isPublicParam);
	    board.setPublic(isPublic);
	    
	    // Save board entity
	    boardService.save(board);
	    
	    // Process file upload if a file is uploaded
	    if (!file.isEmpty()) {
	        fileUploadService.uploadFile(file, board);
	        model.addAttribute("originalFileName", file.getOriginalFilename());
	    } else {
	        model.addAttribute("originalFileName", null);
	    }
	    
	    // Redirect to posts list page
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
	                        @RequestParam("isPublic") String isPublicParam,
	                        @RequestParam(value = "boardFile", required = false) MultipartFile file) {
	    // 주어진 ID로 기존 게시물을 찾습니다.
	    Board existingBoard = boardService.findById(id);

	    // 기존 게시물이 존재하는지 확인합니다.
	    if (existingBoard == null) {
	        throw new RuntimeException("ID에 해당하는 게시물을 찾을 수 없습니다: " + id);
	    }

	    // 현재 인증된 사용자 정보를 가져옵니다.
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (!(principal instanceof UserDetails)) {
	        throw new RuntimeException("유효하지 않은 사용자입니다.");
	    }

	    // 사용자명을 가져옵니다.
	    String username = ((UserDetails) principal).getUsername();
	    // 사용자명으로 현재 사용자를 찾습니다.
	    SiteUser currentUser = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

	    // 현재 사용자가 기존 게시물의 저자인지 확인합니다.
	    if (!existingBoard.getAuthor().equals(currentUser)) {
	        throw new RuntimeException("이 게시물을 수정할 권한이 없습니다.");
	    }

	    try {
	        // 새 파일이 제공된 경우 파일 삭제 로직을 먼저 처리합니다.
	        if (file != null && !file.isEmpty()) {
	            fileUploadService.uploadFile(file, existingBoard);
	        } else {
	            // 새 파일이 업로드되지 않은 경우, 기존 파일을 삭제합니다.
	            fileUploadService.deleteExistingFilesForBoard(id);
	        }

	        // 기존 게시물의 제목과 내용을 업데이트합니다.
	        existingBoard.setTitle(board.getTitle());
	        existingBoard.setContent(board.getContent());

	        // 공개 여부를 업데이트합니다.
	        boolean isPublic = "1".equals(isPublicParam);
	        existingBoard.setPublic(isPublic);

	        // 수정된 게시물을 데이터베이스에 저장합니다.
	        boardService.save(existingBoard);

	        // 게시물 목록 페이지로 리다이렉션합니다.
	        return "redirect:/board";
	    } catch (RuntimeException ex) {
	        // 예외 처리 및 에러 페이지로 리다이렉션합니다.
	        return "error";
	    }
	}





	
	
	

	 @GetMapping("/download/{boardId}")
	    public ResponseEntity<Resource> downloadFile(@PathVariable Long boardId) {
	        // Load file resource
	        Resource resource = fileUploadService.loadFileAsResource(boardId);

	        // Determine file content type
	        String contentType = null;
	        try {
	            contentType = Files.probeContentType(resource.getFile().toPath());
	        } catch (IOException e) {
	            contentType = "application/octet-stream";
	            e.printStackTrace();
	        }

	        // Set default content type if not determined
	        if (contentType == null) {
	            contentType = "application/octet-stream";
	        }

	        // Extract original filename from database or resource metadata
	        String originalFileName = fileSaveRepository.findByBoardId(boardId)
	                                                     .map(FileSave::getOriginalFileName)
	                                                     .orElse(resource.getFilename());

	        // Encode filename in UTF-8 to ensure proper handling of non-ASCII characters
	        String encodedFileName;
	        try {
	            encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString());
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to encode filename: " + originalFileName, e);
	        }

	        // Build and return ResponseEntity with correct headers
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
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
