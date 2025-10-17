package com.coaxial.service;

import com.coaxial.entity.ChapterUploadedFile;
import com.coaxial.repository.ChapterUploadedFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to migrate chapter uploaded file paths from absolute to relative paths
 * This fixes the PDF loading issue where absolute paths were stored instead of relative paths
 */
@Service
public class ChapterFilePathMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(ChapterFilePathMigrationService.class);
    
    private final ChapterUploadedFileRepository fileRepository;

    public ChapterFilePathMigrationService(ChapterUploadedFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * Migrates all chapter uploaded file paths from absolute to relative format
     * Example: D:/path/to/uploads/chapters/1/file.pdf -> uploads/chapters/1/file.pdf
     * 
     * @return Number of records updated
     */
    @Transactional
    public int migrateAllFilePaths() {
        logger.info("Starting chapter file path migration...");
        
        List<ChapterUploadedFile> allFiles = fileRepository.findAll();
        int updatedCount = 0;
        
        for (ChapterUploadedFile file : allFiles) {
            String originalPath = file.getFilePath();
            
            if (originalPath == null || originalPath.trim().isEmpty()) {
                // If path is null or empty, generate it from chapter ID and filename
                if (file.getChapter() != null && file.getFileName() != null) {
                    String newPath = "uploads/chapters/" + file.getChapter().getId() + "/" + file.getFileName();
                    file.setFilePath(newPath);
                    fileRepository.save(file);
                    updatedCount++;
                    logger.info("Generated path for file ID {}: {} -> {}", file.getId(), originalPath, newPath);
                }
                continue;
            }
            
            // Check if path is already in correct format
            if (originalPath.startsWith("uploads/chapters/")) {
                continue; // Already in correct format
            }
            
            // Try to extract relative path from absolute path
            String newPath = extractRelativePath(originalPath, file);
            
            if (newPath != null && !newPath.equals(originalPath)) {
                file.setFilePath(newPath);
                fileRepository.save(file);
                updatedCount++;
                logger.info("Migrated file ID {}: {} -> {}", file.getId(), originalPath, newPath);
            }
        }
        
        logger.info("Migration completed. Updated {} file paths.", updatedCount);
        return updatedCount;
    }

    /**
     * Extracts relative path from absolute path
     * Handles both Windows and Unix style paths
     */
    private String extractRelativePath(String absolutePath, ChapterUploadedFile file) {
        // Pattern to extract chapters/X/filename from absolute path
        Pattern pattern = Pattern.compile("chapters[/\\\\](\\d+)[/\\\\](.+)$");
        Matcher matcher = pattern.matcher(absolutePath);
        
        if (matcher.find()) {
            String chapterId = matcher.group(1);
            String filename = matcher.group(2);
            return "uploads/chapters/" + chapterId + "/" + filename;
        }
        
        // If pattern doesn't match, try to construct from file metadata
        if (file.getChapter() != null && file.getFileName() != null) {
            return "uploads/chapters/" + file.getChapter().getId() + "/" + file.getFileName();
        }
        
        return null;
    }

    /**
     * Migrates a single file path
     * Useful for testing or fixing individual records
     */
    @Transactional
    public boolean migrateFilePath(Long fileId) {
        ChapterUploadedFile file = fileRepository.findById(fileId).orElse(null);
        
        if (file == null) {
            logger.warn("File with ID {} not found", fileId);
            return false;
        }
        
        String originalPath = file.getFilePath();
        String newPath = extractRelativePath(originalPath, file);
        
        if (newPath != null && !newPath.equals(originalPath)) {
            file.setFilePath(newPath);
            fileRepository.save(file);
            logger.info("Migrated file ID {}: {} -> {}", file.getId(), originalPath, newPath);
            return true;
        }
        
        return false;
    }
}

