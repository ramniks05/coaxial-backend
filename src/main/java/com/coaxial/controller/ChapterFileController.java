package com.coaxial.controller;

import com.coaxial.entity.ChapterUploadedFile;
import com.coaxial.service.ChapterFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/master-data/chapters")
@PreAuthorize("hasRole('ADMIN')")
public class ChapterFileController {

    private final ChapterFileService chapterFileService;

    public ChapterFileController(ChapterFileService chapterFileService) {
        this.chapterFileService = chapterFileService;
    }

    @PostMapping("/{chapterId}/files")
    public ResponseEntity<List<ChapterUploadedFile>> uploadFiles(
            @PathVariable Long chapterId,
            @RequestParam("files") MultipartFile[] files) throws IOException {
        List<ChapterUploadedFile> saved = chapterFileService.uploadChapterFiles(chapterId, Arrays.asList(files));
        return ResponseEntity.ok(saved);
    }
}


