package com.mysite.minsoft.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

 // 파일 업로드 메서드
    public String uploadFile(MultipartFile file, Board board) {
        String originalFileName = file.getOriginalFilename();
        String fileName = StringUtils.cleanPath(originalFileName);

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(fileName); // Use original file name as target
            file.transferTo(targetLocation);

            // 파일 정보를 데이터베이스에 저장하기 전에 기존 파일 삭제
            deleteExistingFilesForBoard(board.getId());

            // 파일 정보를 데이터베이스에 저장
            FileSave fileSave = new FileSave();
            fileSave.setFileName(fileName); // Use original file name as file name
            fileSave.setOriginalFileName(originalFileName); // 원본 파일 이름 저장
            fileSave.setFilePath(targetLocation.toString());
            fileSave.setBoard(board);
            fileSaveRepository.save(fileSave);

            return originalFileName;
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장에 실패했습니다.", ex);
        }
    }

    // 기존 게시물의 파일 삭제 메서드
    @Transactional
    public void deleteExistingFilesForBoard(Long boardId) {
        try {
            List<FileSave> existingFiles = fileSaveRepository.findAllByBoardId(boardId);
            for (FileSave existingFile : existingFiles) {
                deleteFile(existingFile.getFileName());
                fileSaveRepository.delete(existingFile);
            }
        } catch (Exception ex) {
            throw new RuntimeException("게시글에 연관된 파일 삭제 중 오류가 발생했습니다.", ex);
        }
    }

    // 파일 삭제 메서드
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("파일 삭제 중 예외 발생", ex);
        }
    }

    // 나머지 메서드는 이전과 동일하게 유지
    // getFileExtension, generateUniqueFileName, loadFileAsResource 등...

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
}
