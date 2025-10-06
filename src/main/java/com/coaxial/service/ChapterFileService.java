package com.coaxial.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.coaxial.entity.Chapter;
import com.coaxial.entity.ChapterUploadedFile;
import com.coaxial.repository.ChapterRepository;
import com.coaxial.repository.ChapterUploadedFileRepository;

@Service
public class ChapterFileService {

    @Value("${file.upload.base-dir:uploads}")
    private String baseDir;

    private final ChapterRepository chapterRepository;
    private final ChapterUploadedFileRepository fileRepo;

    public ChapterFileService(ChapterRepository chapterRepository, ChapterUploadedFileRepository fileRepo) {
        this.chapterRepository = chapterRepository;
        this.fileRepo = fileRepo;
    }

    @Transactional
    public List<ChapterUploadedFile> uploadChapterFiles(Long chapterId, List<MultipartFile> files) throws IOException {
        return uploadChapterFiles(chapterId, files, null);
    }

    @Transactional
    public List<ChapterUploadedFile> uploadChapterFiles(Long chapterId, List<MultipartFile> files, List<String> fileTitles) throws IOException {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + chapterId));

        Path chapterDir = Paths.get(baseDir, "chapters", String.valueOf(chapter.getId())).normalize();
        Files.createDirectories(chapterDir);

        List<ChapterUploadedFile> saved = new ArrayList<>();
        int order = chapter.getUploadedFiles() != null ? chapter.getUploadedFiles().size() : 0;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile mf = files.get(i);
            if (mf == null || mf.isEmpty()) continue;

            String original = Paths.get(mf.getOriginalFilename()).getFileName().toString();
            Path target = chapterDir.resolve(original).normalize();

            if (Files.exists(target)) {
                String name = original;
                String ext = "";
                int dot = original.lastIndexOf('.');
                if (dot > 0) { name = original.substring(0, dot); ext = original.substring(dot); }
                int counter = 1;
                while (Files.exists(target)) {
                    target = chapterDir.resolve(name + " (" + counter++ + ")" + ext).normalize();
                }
            }

            Files.copy(mf.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            ChapterUploadedFile rec = new ChapterUploadedFile();
            rec.setChapter(chapter);
            rec.setFileName(target.getFileName().toString());
            rec.setFilePath(target.toString().replace("\\", "/"));
            rec.setFileType(mf.getContentType());
            rec.setFileSize(mf.getSize());
            rec.setDisplayOrder(order++);
            
            // Set document title if provided
            if (fileTitles != null && i < fileTitles.size() && fileTitles.get(i) != null) {
                rec.setDocumentTitle(fileTitles.get(i));
            }

            saved.add(fileRepo.save(rec));
        }
        return saved;
    }
}


