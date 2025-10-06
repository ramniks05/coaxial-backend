package com.coaxial.dto;

import jakarta.validation.constraints.NotBlank;

public class ChapterDocumentDTO {
    
    @NotBlank(message = "File name is required")
    private String fileName;
    
    private String documentTitle;
    
    private String filePath;
    
    private Long fileSize;
    
    private String fileType;
    
    private Integer displayOrder = 0;
    
    // Constructors
    public ChapterDocumentDTO() {}
    
    public ChapterDocumentDTO(String fileName, String documentTitle) {
        this.fileName = fileName;
        this.documentTitle = documentTitle;
    }
    
    public ChapterDocumentDTO(String fileName, String documentTitle, String filePath) {
        this.fileName = fileName;
        this.documentTitle = documentTitle;
        this.filePath = filePath;
    }
    
    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getDocumentTitle() { return documentTitle; }
    public void setDocumentTitle(String documentTitle) { this.documentTitle = documentTitle; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
