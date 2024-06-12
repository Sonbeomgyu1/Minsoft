package com.mysite.minsoft.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileUploadService {

    @Autowired
    private FileSaveRepository fileSaveRepository;

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

 // 파일 업로드 메서드
    public String uploadFile(MultipartFile file, Board board) {
        // 원본 파일 이름 가져오기
        String originalFileName = file.getOriginalFilename();
        // 파일 이름에서 관련된 경로 문자 제거
        String fileName = StringUtils.cleanPath(originalFileName);
        // 파일 확장자 가져오기
        String fileExtension = getFileExtension(fileName);
        // 고유한 파일 이름 생성
        String newFileName = generateUniqueFileName(fileExtension);

        try {
            // 업로드 디렉토리의 절대 경로 가져오기
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            // 디렉토리가 존재하지 않으면 생성
            Files.createDirectories(uploadPath);

            // 파일을 저장할 대상 위치 설정
            Path targetLocation = uploadPath.resolve(newFileName);
            // 파일을 대상 위치로 전송
            file.transferTo(targetLocation);

            // 게시글이 ID를 가지고 있는지 확인
            if (board.getId() != null) {
                // 새 FileSave 객체 생성
                FileSave fileSave = new FileSave();
                // 파일 세부 정보 설정
                fileSave.setFileName(newFileName);
                fileSave.setFilePath(targetLocation.toString());
                fileSave.setOriginalFileName(originalFileName);
                // 파일을 게시글과 연결
                fileSave.setBoard(board);

                // 파일 저장 정보를 데이터베이스에 저장
                fileSaveRepository.save(fileSave);
            } else {
                // 게시글 ID가 null인 경우 예외 발생
                throw new RuntimeException("게시글을 저장하는 데 실패했습니다. 게시글 ID가 null입니다.");
            }

            // 새 파일 이름 반환
            return originalFileName;
        } catch (IOException ex) {
            // 파일 저장에 실패하면 런타임 예외 발생
            throw new RuntimeException("파일 저장에 실패했습니다.", ex);
        }
    }

    // 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    // 고유 파일 이름 생성 메서드
    private String generateUniqueFileName(String fileExtension) {
        return UUID.randomUUID().toString() + fileExtension;
    }

 // 파일 로드 메서드
    public Resource loadFileAsResource(Long boardId) {
        try {
            // 게시글 ID를 사용하여 파일 정보 목록 가져오기
            List<FileSave> fileSaves = fileSaveRepository.findAllByBoardId(boardId);
            
            if (fileSaves.isEmpty()) {
                // 파일 정보를 찾을 수 없으면 예외 발생
                throw new FileNotFoundException("게시글과 연관된 파일을 찾을 수 없습니다. 게시글 ID: " + boardId);
            }

            // 다수의 파일이 있는 경우, 첫 번째 파일을 선택
            FileSave fileSave = fileSaves.get(0);
            
            // 파일 경로를 기반으로 리소스 로드
            Path filePath = Paths.get(fileSave.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                // 파일이 존재하지 않으면 예외 발생
                throw new FileNotFoundException("파일을 찾을 수 없습니다: " + filePath);
            }
        } catch (MalformedURLException ex) {
            // URL이 잘못된 경우 예외 발생
            throw new RuntimeException("잘못된 URL 예외", ex);
        } catch (IOException ex) {
            // 파일 로드 중에 예외가 발생한 경우 예외 발생
            throw new RuntimeException("파일 로드 중 예외 발생", ex);
        }
    }

 // 파일 삭제 메서드
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // 파일 삭제 중에 예외가 발생한 경우 예외를 던집니다.
            throw new RuntimeException("파일 삭제 중 예외 발생", ex);
        }
    }


}
