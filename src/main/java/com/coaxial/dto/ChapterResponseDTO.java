package com.coaxial.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.coaxial.entity.Chapter;

public class ChapterResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Long moduleId;
    private String moduleName;
    private Long topicId;
    private String topicName;
    private Long subjectId;
    private String subjectName;
    private String subjectType;
    private Integer displayOrder;
    private Boolean isActive;
    private List<ChapterVideoDTO> videos;
    private List<ChapterDocumentDTO> documents;
    
    private String createdAt;
    private String updatedAt;
    private String createdByName;
    private String updatedByName;
    
    // Constructors
    public ChapterResponseDTO() {}
    
    public ChapterResponseDTO(Chapter chapter) {
        this.id = chapter.getId();
        this.name = chapter.getName();
        this.description = chapter.getDescription();
        this.moduleId = chapter.getModule().getId();
        this.moduleName = chapter.getModule().getName();
        // Topic ID is now composite - set to null or create composite ID if needed
        // this.topicId = Long.parseLong(chapter.getModule().getTopic().getCourseTypeId() + "" + chapter.getModule().getTopic().getRelationshipId());
        this.topicId = null; // Topic now uses composite key (courseTypeId + relationshipId)
        this.topicName = chapter.getModule().getTopic().getName();
        // Subject information is no longer directly accessible from Topic entity
        // These fields will need to be populated separately if needed
        this.displayOrder = chapter.getDisplayOrder();
        this.isActive = chapter.getIsActive();
        // Convert to new DTO format with titles
        this.videos = chapter.getYoutubeLinks().stream()
                .map(link -> new ChapterVideoDTO(link.getYoutubeLink(), link.getVideoTitle(), link.getDisplayOrder()))
                .collect(Collectors.toList());
        this.documents = chapter.getUploadedFiles().stream()
                .map(file -> {
                    String path = file.getFilePath();
                    if (path != null && !path.trim().isEmpty()) {
                        // Ensure leading slash for relative URL
                        path = path.startsWith("/") ? path : "/" + path;
                    } else {
                        // Fallback: build from chapter id and file name
                        path = "/uploads/chapters/" + chapter.getId() + "/" + file.getFileName();
                    }
                    ChapterDocumentDTO docDto = new ChapterDocumentDTO(file.getFileName(), file.getDocumentTitle());
                    docDto.setFilePath(path);
                    docDto.setFileSize(file.getFileSize());
                    docDto.setFileType(file.getFileType());
                    docDto.setDisplayOrder(file.getDisplayOrder());
                    return docDto;
                })
                .collect(Collectors.toList());
        
        // Legacy arrays not needed - information is in videos and documents arrays
        this.createdAt = chapter.getCreatedAt() != null ? chapter.getCreatedAt().toString() : null;
        this.updatedAt = chapter.getUpdatedAt() != null ? chapter.getUpdatedAt().toString() : null;
        
        if (chapter.getCreatedBy() != null) {
            this.createdByName = chapter.getCreatedBy().getFullName();
        }
        if (chapter.getUpdatedBy() != null) {
            this.updatedByName = chapter.getUpdatedBy().getFullName();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    
    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }
    
    // New DTO getters and setters
    public List<ChapterVideoDTO> getVideos() { return videos; }
    public void setVideos(List<ChapterVideoDTO> videos) { this.videos = videos; }
    
    public List<ChapterDocumentDTO> getDocuments() { return documents; }
    public void setDocuments(List<ChapterDocumentDTO> documents) { this.documents = documents; }
}
