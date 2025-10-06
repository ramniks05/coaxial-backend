package com.coaxial.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChapterRequestDTO {
    
    @NotBlank(message = "Chapter name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Module ID is required")
    private Long moduleId;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    private List<ChapterVideoDTO> videos;
    private List<ChapterDocumentDTO> documents;
    
    // Legacy fields for backward compatibility
    private List<String> youtubeLinks;
    private List<String> uploadedFiles;
    
    // Constructors
    public ChapterRequestDTO() {}
    
    public ChapterRequestDTO(String name, String description, Long moduleId) {
        this.name = name;
        this.description = description;
        this.moduleId = moduleId;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<ChapterVideoDTO> getVideos() { return videos; }
    public void setVideos(List<ChapterVideoDTO> videos) { this.videos = videos; }
    
    public List<ChapterDocumentDTO> getDocuments() { return documents; }
    public void setDocuments(List<ChapterDocumentDTO> documents) { this.documents = documents; }
    
    // Legacy getters and setters for backward compatibility
    public List<String> getYoutubeLinks() { return youtubeLinks; }
    public void setYoutubeLinks(List<String> youtubeLinks) { this.youtubeLinks = youtubeLinks; }
    
    public List<String> getUploadedFiles() { return uploadedFiles; }
    public void setUploadedFiles(List<String> uploadedFiles) { this.uploadedFiles = uploadedFiles; }
}
