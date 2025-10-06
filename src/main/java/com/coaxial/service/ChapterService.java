package com.coaxial.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.ChapterDocumentDTO;
import com.coaxial.dto.ChapterRequestDTO;
import com.coaxial.dto.ChapterResponseDTO;
import com.coaxial.dto.ChapterVideoDTO;
import com.coaxial.entity.Chapter;
import com.coaxial.entity.ChapterUploadedFile;
import com.coaxial.entity.ChapterYoutubeLink;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Module;
import com.coaxial.entity.Subject;
import com.coaxial.entity.Topic;
import com.coaxial.entity.User;
import com.coaxial.repository.ChapterRepository;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.ModuleRepository;

@Service
@Transactional
public class ChapterService {
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private ClassSubjectRepository classSubjectRepository;
    
    @Autowired
    private ExamSubjectRepository examSubjectRepository;
    
    @Autowired
    private CourseSubjectRepository courseSubjectRepository;
    
    public List<ChapterResponseDTO> getAllChapters() {
        return chapterRepository.findAll().stream()
                .map(this::createChapterResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public List<ChapterResponseDTO> getChaptersByModuleId(Long moduleId) {
        return chapterRepository.findByModuleIdAndIsActiveTrueOrderByDisplayOrderAsc(moduleId).stream()
                .map(this::createChapterResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public List<ChapterResponseDTO> getChaptersByModuleIds(List<Long> moduleIds) {
        return chapterRepository.findByModuleIdInAndIsActiveTrueOrderByDisplayOrderAsc(moduleIds).stream()
                .map(this::createChapterResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public List<ChapterResponseDTO> getChaptersWithFilters(Boolean isActive, String name, Long moduleId, Long topicId, Long subjectId, Long courseTypeId, LocalDateTime createdAfter) {
        List<Chapter> chapters;
        
        if (moduleId != null) {
            chapters = chapterRepository.findByModuleIdAndIsActiveOrderByDisplayOrderAsc(moduleId, isActive);
        } else if (subjectId != null) {
            // Note: Direct subject filtering is no longer supported due to Topic entity structure change
            // Subject information is resolved through courseTypeId and relationshipId
            // For now, return all chapters and filter by subject in the service layer if needed
            chapters = chapterRepository.findAll();
        } else if (name != null && !name.trim().isEmpty()) {
            chapters = chapterRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(name);
        } else if (createdAfter != null) {
            chapters = chapterRepository.findByCreatedAtAfterAndIsActiveTrueOrderByDisplayOrderAsc(createdAfter);
        } else {
            chapters = chapterRepository.findAll();
        }
        
        return chapters.stream()
                .map(this::createChapterResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public Page<ChapterResponseDTO> getChaptersPaginated(Pageable pageable) {
        return chapterRepository.findAll(pageable)
                .map(this::createChapterResponseDTOWithSubjectInfo);
    }
    
    public Page<ChapterResponseDTO> getChaptersWithFilters(Long moduleId, Boolean isActive, String search, Pageable pageable) {
        List<Chapter> allChapters = getAllChaptersList(moduleId, isActive, search);
        
        // Manual pagination since we're using simple JPA methods
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allChapters.size());
        List<Chapter> pageContent = allChapters.subList(start, end);
        
        Page<Chapter> chapters = new PageImpl<>(pageContent, pageable, allChapters.size());
        return chapters.map(this::createChapterResponseDTOWithSubjectInfo);
    }

    public List<ChapterResponseDTO> getChaptersWithCombinedFilter(
            Boolean active, Long courseTypeId, Long courseId, Long classId, Long examId,
            Long subjectId, Long topicId, Long moduleId, String search) {
        // Resolve modules like ModuleService combined filter
        List<Long> moduleIds;
        if (moduleId != null) {
            moduleIds = java.util.List.of(moduleId);
        } else {
            List<Module> modules;
            if (topicId != null) {
                modules = moduleRepository.findByTopicIdAndIsActiveTrueOrderByDisplayOrderAsc(topicId);
            } else {
                modules = moduleRepository.findAll();
            }
            moduleIds = modules.stream().map(Module::getId).collect(Collectors.toList());
        }

        if (moduleIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // Fetch chapters for these modules
        List<Chapter> chapters = chapterRepository.findByModuleIdInAndIsActiveTrueOrderByDisplayOrderAsc(moduleIds);
        if (active != null) {
            chapters = chapters.stream().filter(c -> active.equals(c.getIsActive())).collect(Collectors.toList());
        }
        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            chapters = chapters.stream()
                .filter(c -> (c.getName() != null && c.getName().toLowerCase().contains(s))
                          || (c.getDescription() != null && c.getDescription().toLowerCase().contains(s)))
                .collect(Collectors.toList());
        }

        return chapters.stream().map(this::createChapterResponseDTOWithSubjectInfo).collect(Collectors.toList());
    }
    
    // Helper method to get chapters list using simplified JPA methods
    private List<Chapter> getAllChaptersList(Long moduleId, Boolean isActive, String search) {
        if (moduleId != null && isActive != null && search != null && !search.isEmpty()) {
            // Use separate queries and filter in memory for complex conditions
            List<Chapter> chapters = chapterRepository.findByModuleIdAndIsActiveOrderByDisplayOrderAsc(moduleId, isActive);
            return chapters.stream()
                .filter(chapter -> chapter.getName().toLowerCase().contains(search.toLowerCase()) || 
                                 (chapter.getDescription() != null && chapter.getDescription().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
        } else if (moduleId != null && isActive != null) {
            return chapterRepository.findByModuleIdAndIsActiveOrderByDisplayOrderAsc(moduleId, isActive);
        } else if (moduleId != null) {
            return chapterRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId);
        } else if (isActive != null) {
            return chapterRepository.findByIsActiveOrderByDisplayOrderAsc(isActive);
        } else if (search != null && !search.isEmpty()) {
            return chapterRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else {
            return chapterRepository.findAll();
        }
    }
    
    public Optional<ChapterResponseDTO> getChapterById(Long id) {
        return chapterRepository.findById(id)
                .map(this::createChapterResponseDTOWithSubjectInfo);
    }
    
    public ChapterResponseDTO createChapter(ChapterRequestDTO chapterRequestDTO) {
        // Validate module exists
        Module module = moduleRepository.findById(chapterRequestDTO.getModuleId())
                .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Module not found with ID: " + chapterRequestDTO.getModuleId()));
        
        // Check for duplicate name within the same module
        if (chapterRepository.existsByNameAndModuleId(chapterRequestDTO.getName(), chapterRequestDTO.getModuleId())) {
            throw new IllegalArgumentException("Chapter with name '" + chapterRequestDTO.getName() + "' already exists for this module");
        }
        
        Chapter chapter = new Chapter();
        chapter.setName(chapterRequestDTO.getName());
        chapter.setDescription(chapterRequestDTO.getDescription());
        chapter.setModule(module);
        chapter.setDisplayOrder(chapterRequestDTO.getDisplayOrder());
        chapter.setIsActive(chapterRequestDTO.getIsActive());
        chapter.setCreatedBy(getCurrentUser());
        
        // Handle YouTube links - new format with titles
        if (chapterRequestDTO.getVideos() != null && !chapterRequestDTO.getVideos().isEmpty()) {
            List<ChapterYoutubeLink> youtubeLinks = new ArrayList<>();
            for (ChapterVideoDTO videoDto : chapterRequestDTO.getVideos()) {
                ChapterYoutubeLink link = new ChapterYoutubeLink();
                link.setChapter(chapter);
                link.setYoutubeLink(videoDto.getYoutubeLink());
                link.setVideoTitle(videoDto.getVideoTitle());
                link.setDisplayOrder(videoDto.getDisplayOrder() != null ? videoDto.getDisplayOrder() : youtubeLinks.size());
                youtubeLinks.add(link);
            }
            chapter.setYoutubeLinks(youtubeLinks);
        }
        // Legacy support for youtubeLinks field
        else if (chapterRequestDTO.getYoutubeLinks() != null && !chapterRequestDTO.getYoutubeLinks().isEmpty()) {
            List<ChapterYoutubeLink> youtubeLinks = new ArrayList<>();
            for (int i = 0; i < chapterRequestDTO.getYoutubeLinks().size(); i++) {
                ChapterYoutubeLink link = new ChapterYoutubeLink();
                link.setChapter(chapter);
                link.setYoutubeLink(chapterRequestDTO.getYoutubeLinks().get(i));
                link.setDisplayOrder(i);
                youtubeLinks.add(link);
            }
            chapter.setYoutubeLinks(youtubeLinks);
        }
        
        // Handle uploaded files - new format with titles
        if (chapterRequestDTO.getDocuments() != null && !chapterRequestDTO.getDocuments().isEmpty()) {
            System.out.println("Processing " + chapterRequestDTO.getDocuments().size() + " documents");
            List<ChapterUploadedFile> uploadedFiles = new ArrayList<>();
            for (ChapterDocumentDTO docDto : chapterRequestDTO.getDocuments()) {
                System.out.println("Processing document: " + docDto.getFileName() + " with title: " + docDto.getDocumentTitle());
                ChapterUploadedFile file = new ChapterUploadedFile();
                file.setChapter(chapter);
                file.setFileName(docDto.getFileName());
                file.setDocumentTitle(docDto.getDocumentTitle());
                
                // Set file path - use provided path or leave null for ChapterFileService to handle
                String filePath = docDto.getFilePath();
                if (filePath == null || filePath.trim().isEmpty()) {
                    // Don't set path here - let ChapterFileService handle it during actual file upload
                    filePath = null;
                }
                file.setFilePath(filePath);
                
                file.setFileSize(docDto.getFileSize());
                file.setFileType(docDto.getFileType());
                file.setDisplayOrder(docDto.getDisplayOrder() != null ? docDto.getDisplayOrder() : uploadedFiles.size());
                uploadedFiles.add(file);
            }
            chapter.setUploadedFiles(uploadedFiles);
        }
        // Legacy support for uploadedFiles field
        else if (chapterRequestDTO.getUploadedFiles() != null && !chapterRequestDTO.getUploadedFiles().isEmpty()) {
            List<ChapterUploadedFile> uploadedFiles = new ArrayList<>();
            for (int i = 0; i < chapterRequestDTO.getUploadedFiles().size(); i++) {
                ChapterUploadedFile file = new ChapterUploadedFile();
                file.setChapter(chapter);
                file.setFileName(chapterRequestDTO.getUploadedFiles().get(i));
                file.setDisplayOrder(i);
                uploadedFiles.add(file);
            }
            chapter.setUploadedFiles(uploadedFiles);
        }
        
        Chapter savedChapter = chapterRepository.save(chapter);
        System.out.println("Chapter saved with ID: " + savedChapter.getId());
        System.out.println("Number of uploaded files: " + (savedChapter.getUploadedFiles() != null ? savedChapter.getUploadedFiles().size() : 0));

        // Ensure filePath is set for name-only entries so frontend gets usable URLs
        if (savedChapter.getUploadedFiles() != null && !savedChapter.getUploadedFiles().isEmpty()) {
            boolean updatedAnyPath = false;
            for (ChapterUploadedFile uf : savedChapter.getUploadedFiles()) {
                if (uf.getFilePath() == null || uf.getFilePath().trim().isEmpty()) {
                    String relativePath = "uploads/chapters/" + savedChapter.getId() + "/" + uf.getFileName();
                    uf.setFilePath(relativePath);
                    updatedAnyPath = true;
                }
            }
            if (updatedAnyPath) {
                savedChapter = chapterRepository.save(savedChapter);
            }
        }

        return createChapterResponseDTOWithSubjectInfo(savedChapter);
    }
    
    public ChapterResponseDTO updateChapter(Long id, ChapterRequestDTO chapterRequestDTO) {
        Chapter existingChapter = chapterRepository.findById(id)
                .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Chapter not found with ID: " + id));
        
        // Validate module exists
        Module module = moduleRepository.findById(chapterRequestDTO.getModuleId())
                .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Module not found with ID: " + chapterRequestDTO.getModuleId()));
        
        // Check for duplicate name within the same module (excluding current chapter)
        if (chapterRepository.existsByNameAndModuleIdAndIdNot(chapterRequestDTO.getName(), chapterRequestDTO.getModuleId(), id)) {
            throw new IllegalArgumentException("Chapter with name '" + chapterRequestDTO.getName() + "' already exists for this module");
        }
        
        existingChapter.setName(chapterRequestDTO.getName());
        existingChapter.setDescription(chapterRequestDTO.getDescription());
        existingChapter.setModule(module);
        existingChapter.setDisplayOrder(chapterRequestDTO.getDisplayOrder());
        existingChapter.setIsActive(chapterRequestDTO.getIsActive());
        existingChapter.setUpdatedBy(getCurrentUser());
        
        // Update YouTube links - new format with titles
        if (chapterRequestDTO.getVideos() != null && !chapterRequestDTO.getVideos().isEmpty()) {
            // Only clear and replace if videos are being explicitly updated
            existingChapter.getYoutubeLinks().clear();
            for (ChapterVideoDTO videoDto : chapterRequestDTO.getVideos()) {
                ChapterYoutubeLink link = new ChapterYoutubeLink();
                link.setChapter(existingChapter);
                link.setYoutubeLink(videoDto.getYoutubeLink());
                link.setVideoTitle(videoDto.getVideoTitle());
                link.setDisplayOrder(videoDto.getDisplayOrder() != null ? videoDto.getDisplayOrder() : existingChapter.getYoutubeLinks().size());
                existingChapter.getYoutubeLinks().add(link);
            }
        }
        // Legacy support for youtubeLinks field
        else if (chapterRequestDTO.getYoutubeLinks() != null && !chapterRequestDTO.getYoutubeLinks().isEmpty()) {
            // Only clear and replace if videos are being explicitly updated
            existingChapter.getYoutubeLinks().clear();
            for (int i = 0; i < chapterRequestDTO.getYoutubeLinks().size(); i++) {
                ChapterYoutubeLink link = new ChapterYoutubeLink();
                link.setChapter(existingChapter);
                link.setYoutubeLink(chapterRequestDTO.getYoutubeLinks().get(i));
                link.setDisplayOrder(i);
                existingChapter.getYoutubeLinks().add(link);
            }
        }
        
        // Update uploaded files - new format with titles
        if (chapterRequestDTO.getDocuments() != null) {
            existingChapter.getUploadedFiles().clear();
            for (ChapterDocumentDTO docDto : chapterRequestDTO.getDocuments()) {
                ChapterUploadedFile file = new ChapterUploadedFile();
                file.setChapter(existingChapter);
                file.setFileName(docDto.getFileName());
                file.setDocumentTitle(docDto.getDocumentTitle());
                
                // Set file path - use provided path or leave null for ChapterFileService to handle
                String filePath = docDto.getFilePath();
                if (filePath == null || filePath.trim().isEmpty()) {
                    // Don't set path here - let ChapterFileService handle it during actual file upload
                    filePath = null;
                }
                file.setFilePath(filePath);
                
                file.setFileSize(docDto.getFileSize());
                file.setFileType(docDto.getFileType());
                file.setDisplayOrder(docDto.getDisplayOrder() != null ? docDto.getDisplayOrder() : existingChapter.getUploadedFiles().size());
                existingChapter.getUploadedFiles().add(file);
            }
        }
        // Legacy support for uploadedFiles field
        else if (chapterRequestDTO.getUploadedFiles() != null) {
            existingChapter.getUploadedFiles().clear();
            for (int i = 0; i < chapterRequestDTO.getUploadedFiles().size(); i++) {
                ChapterUploadedFile file = new ChapterUploadedFile();
                file.setChapter(existingChapter);
                file.setFileName(chapterRequestDTO.getUploadedFiles().get(i));
                file.setDisplayOrder(i);
                existingChapter.getUploadedFiles().add(file);
            }
        }
        
        Chapter updatedChapter = chapterRepository.save(existingChapter);

        // Ensure filePath is set for name-only entries after update as well
        if (updatedChapter.getUploadedFiles() != null && !updatedChapter.getUploadedFiles().isEmpty()) {
            boolean updatedAnyPath = false;
            for (ChapterUploadedFile uf : updatedChapter.getUploadedFiles()) {
                if (uf.getFilePath() == null || uf.getFilePath().trim().isEmpty()) {
                    String relativePath = "uploads/chapters/" + updatedChapter.getId() + "/" + uf.getFileName();
                    uf.setFilePath(relativePath);
                    updatedAnyPath = true;
                }
            }
            if (updatedAnyPath) {
                updatedChapter = chapterRepository.save(updatedChapter);
            }
        }

        return createChapterResponseDTOWithSubjectInfo(updatedChapter);
    }
    
    public void deleteChapter(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new IllegalArgumentException("Chapter not found with ID: " + id);
        }
        chapterRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return chapterRepository.existsById(id);
    }
    
    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * Creates a ChapterResponseDTO with resolved subject information
     * based on the Topic's courseTypeId and relationshipId
     */
    private ChapterResponseDTO createChapterResponseDTOWithSubjectInfo(Chapter chapter) {
        ChapterResponseDTO dto = new ChapterResponseDTO(chapter);
        
        // Keep uploadedFiles empty - use documents array instead
        // dto.setUploadedFiles() is not called - constructor already sets it to empty ArrayList

        Module module = chapter.getModule();
        if (module != null) {
            Topic topic = module.getTopic();
            if (topic != null) {
                Subject subject = resolveSubjectFromTopic(topic);
                if (subject != null) {
                    dto.setSubjectId(subject.getId());
                    dto.setSubjectName(subject.getName());
                    dto.setSubjectType(subject.getCourseType() != null ? subject.getCourseType().getName() : "Unknown");
                }
            }
        }
        
        return dto;
    }
    
    /**
     * Resolves the Subject entity from a Topic based on its courseTypeId and relationshipId
     * 
     * @param topic The Topic entity
     * @return The resolved Subject entity, or null if not found
     */
    private Subject resolveSubjectFromTopic(Topic topic) {
        Long courseTypeId = topic.getCourseTypeId();
        Long relationshipId = topic.getRelationshipId();
        
        if (courseTypeId == null || relationshipId == null) {
            return null;
        }
        
        try {
            // CourseTypeId 1 = Academic (ClassSubject)
            if (courseTypeId == 1) {
                return classSubjectRepository.findById(relationshipId)
                        .map(ClassSubject::getSubject)
                        .orElse(null);
            }
            // CourseTypeId 2 = Competitive (ExamSubject)
            else if (courseTypeId == 2) {
                return examSubjectRepository.findById(relationshipId)
                        .map(ExamSubject::getSubject)
                        .orElse(null);
            }
            // CourseTypeId 3 = Professional (CourseSubject)
            else if (courseTypeId == 3) {
                return courseSubjectRepository.findById(relationshipId)
                        .map(CourseSubject::getSubject)
                        .orElse(null);
            }
        } catch (Exception e) {
            // Log error if needed, but don't fail the entire operation
            System.err.println("Error resolving subject for topic (courseTypeId=" + topic.getCourseTypeId() + ", relationshipId=" + topic.getRelationshipId() + "): " + e.getMessage());
        }
        
        return null;
    }
    
    // Paginated method for standardized endpoints
    @Transactional(readOnly = true)
    public Page<ChapterResponseDTO> getChaptersCombinedFilter(Long courseTypeId, Long courseId, Long classId, 
            Long examId, Long subjectId, Long topicId, Long moduleId, Boolean active, String search, Pageable pageable) {
        // For now, use the existing filtered method with moduleId
        Page<ChapterResponseDTO> result = getChaptersWithFilters(moduleId, active, search, pageable);
        return result;
    }
}
