package com.coaxial.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coaxial.dto.ChapterRequestDTO;
import com.coaxial.dto.ChapterResponseDTO;
import com.coaxial.dto.ChapterVideoDTO;
import com.coaxial.service.ChapterFileService;
import com.coaxial.service.ChapterService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/chapters")
@PreAuthorize("hasRole('ADMIN')")
public class ChapterController {
    
    private final ChapterService chapterService;
    private final ChapterFileService chapterFileService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChapterController(ChapterService chapterService, ChapterFileService chapterFileService, ObjectMapper objectMapper) {
        this.chapterService = chapterService;
        this.chapterFileService = chapterFileService;
        this.objectMapper = objectMapper;
    }
    
    // Get all chapters
    @GetMapping
    public ResponseEntity<List<ChapterResponseDTO>> getAllChapters() {
        List<ChapterResponseDTO> chapters = chapterService.getAllChapters();
        return ResponseEntity.ok(chapters);
    }
    
    // Get chapters by module ID
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<ChapterResponseDTO>> getChaptersByModuleId(@PathVariable Long moduleId) {
        List<ChapterResponseDTO> chapters = chapterService.getChaptersByModuleId(moduleId);
        return ResponseEntity.ok(chapters);
    }
    
    // Get chapters with filters and pagination
    @GetMapping("/filtered")
    public ResponseEntity<Page<ChapterResponseDTO>> getChaptersWithFilters(
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ChapterResponseDTO> chapters = chapterService.getChaptersWithFilters(moduleId, isActive, search, pageable);
        return ResponseEntity.ok(chapters);
    }
    
    // Standardized combined filter endpoint with pagination
    @GetMapping("/combined-filter")
    public ResponseEntity<Page<ChapterResponseDTO>> getChaptersCombinedFilter(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ChapterResponseDTO> chapters = chapterService.getChaptersCombinedFilter(
            courseTypeId, courseId, classId, examId, subjectId, topicId, moduleId, active, search, pageable);
        return ResponseEntity.ok(chapters);
    }
    
    // Get chapters with advanced filters
    @GetMapping("/advanced-filtered")
    public ResponseEntity<List<ChapterResponseDTO>> getChaptersWithAdvancedFilters(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) String createdAfter) {
        
        LocalDateTime createdAfterDate = null;
        if (createdAfter != null && !createdAfter.trim().isEmpty()) {
            try {
                createdAfterDate = LocalDateTime.parse(createdAfter);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<ChapterResponseDTO> chapters = chapterService.getChaptersWithFilters(active, name, moduleId, topicId, subjectId, courseTypeId, createdAfterDate);
        return ResponseEntity.ok(chapters);
    }

    
    // Get paginated chapters
    @GetMapping("/paginated")
    public ResponseEntity<Page<ChapterResponseDTO>> getChaptersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ChapterResponseDTO> chapters = chapterService.getChaptersPaginated(pageable);
        return ResponseEntity.ok(chapters);
    }
    
    // Get chapter by ID
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ChapterResponseDTO> getChapterById(@PathVariable Long id) {
        Optional<ChapterResponseDTO> chapter = chapterService.getChapterById(id);
        return chapter.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new chapter
    @PostMapping
    public ResponseEntity<ChapterResponseDTO> createChapter(@Valid @RequestBody ChapterRequestDTO chapterRequestDTO) {
        try {
            ChapterResponseDTO createdChapter = chapterService.createChapter(chapterRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Create new chapter with multipart data (supports both JSON and form parameters)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, "multipart/form-data;charset=UTF-8" })
    public ResponseEntity<ChapterResponseDTO> createChapter(
            @RequestParam("chapter") String chapterJson,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "fileTitles", required = false) String[] fileTitles,
            @RequestParam(value = "videoLinks", required = false) String[] videoLinks,
            @RequestParam(value = "videoTitles", required = false) String[] videoTitles) {
        try {
            ChapterRequestDTO chapterRequestDTO = objectMapper.readValue(chapterJson, ChapterRequestDTO.class);
            
            // Handle video links with titles (form parameters only)
            if (videoLinks != null && videoLinks.length > 0) {
                List<ChapterVideoDTO> videos = new ArrayList<>();
                for (int i = 0; i < videoLinks.length; i++) {
                    String title = (videoTitles != null && i < videoTitles.length) ? videoTitles[i] : "Video " + (i + 1);
                    ChapterVideoDTO videoDto = new ChapterVideoDTO();
                    videoDto.setYoutubeLink(videoLinks[i]);
                    videoDto.setVideoTitle(title);
                    videoDto.setDisplayOrder(i);
                    videos.add(videoDto);
                }
                chapterRequestDTO.setVideos(videos);
            }
            // Let ChapterFileService handle files completely - no DTO processing
            
            ChapterResponseDTO createdChapter = chapterService.createChapter(chapterRequestDTO);

            // Handle actual file uploads if provided
            if (createdChapter.getId() != null && files != null && files.length > 0) {
                List<String> titles = fileTitles != null ? Arrays.asList(fileTitles) : null;
                chapterFileService.uploadChapterFiles(createdChapter.getId(), Arrays.asList(files), titles);
                // Reload to include uploaded files
                Optional<ChapterResponseDTO> reloaded = chapterService.getChapterById(createdChapter.getId());
                return reloaded
                        .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                        .orElse(ResponseEntity.status(HttpStatus.CREATED).body(createdChapter));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(createdChapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update chapter
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ChapterResponseDTO> updateChapter(@PathVariable Long id, @Valid @RequestBody ChapterRequestDTO chapterRequestDTO) {
        try {
            ChapterResponseDTO updatedChapter = chapterService.updateChapter(id, chapterRequestDTO);
            return ResponseEntity.ok(updatedChapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update chapter with multipart data (supports both JSON and form parameters)
    @PutMapping(value = "/{id:\\d+}/multipart", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, "multipart/form-data;charset=UTF-8" })
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @PathVariable Long id,
            @RequestParam("chapter") String chapterJson,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "fileTitles", required = false) String[] fileTitles,
            @RequestParam(value = "videoLinks", required = false) String[] videoLinks,
            @RequestParam(value = "videoTitles", required = false) String[] videoTitles) {
        try {
            ChapterRequestDTO chapterRequestDTO = objectMapper.readValue(chapterJson, ChapterRequestDTO.class);
            
            // Handle video links with titles (form parameters only)
            // Only add videos if they are provided AND not already in the chapter JSON
            if (videoLinks != null && videoLinks.length > 0 && 
                (chapterRequestDTO.getVideos() == null || chapterRequestDTO.getVideos().isEmpty())) {
                List<ChapterVideoDTO> videos = new ArrayList<>();
                for (int i = 0; i < videoLinks.length; i++) {
                    String title = (videoTitles != null && i < videoTitles.length) ? videoTitles[i] : "Video " + (i + 1);
                    ChapterVideoDTO videoDto = new ChapterVideoDTO();
                    videoDto.setYoutubeLink(videoLinks[i]);
                    videoDto.setVideoTitle(title);
                    videoDto.setDisplayOrder(i);
                    videos.add(videoDto);
                }
                chapterRequestDTO.setVideos(videos);
            }
            // Let ChapterFileService handle files completely - no DTO processing
            
            ChapterResponseDTO updatedChapter = chapterService.updateChapter(id, chapterRequestDTO);

            // Handle actual file uploads if provided
            if (updatedChapter.getId() != null && files != null && files.length > 0) {
                List<String> titles = fileTitles != null ? Arrays.asList(fileTitles) : null;
                chapterFileService.uploadChapterFiles(updatedChapter.getId(), Arrays.asList(files), titles);
                // Reload to include uploaded files
                Optional<ChapterResponseDTO> reloaded = chapterService.getChapterById(updatedChapter.getId());
                return reloaded
                        .map(dto -> ResponseEntity.ok(dto))
                        .orElse(ResponseEntity.ok(updatedChapter));
            }

            return ResponseEntity.ok(updatedChapter);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    
    // Delete chapter
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Map<String, String>> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Chapter deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
