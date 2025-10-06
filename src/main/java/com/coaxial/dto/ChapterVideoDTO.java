package com.coaxial.dto;

import jakarta.validation.constraints.NotBlank;

public class ChapterVideoDTO {
    
    @NotBlank(message = "YouTube link is required")
    private String youtubeLink;
    
    private String videoTitle;
    
    private Integer displayOrder = 0;
    
    // Constructors
    public ChapterVideoDTO() {}
    
    public ChapterVideoDTO(String youtubeLink, String videoTitle) {
        this.youtubeLink = youtubeLink;
        this.videoTitle = videoTitle;
    }
    
    public ChapterVideoDTO(String youtubeLink, String videoTitle, Integer displayOrder) {
        this.youtubeLink = youtubeLink;
        this.videoTitle = videoTitle;
        this.displayOrder = displayOrder;
    }
    
    // Getters and Setters
    public String getYoutubeLink() { return youtubeLink; }
    public void setYoutubeLink(String youtubeLink) { this.youtubeLink = youtubeLink; }
    
    public String getVideoTitle() { return videoTitle; }
    public void setVideoTitle(String videoTitle) { this.videoTitle = videoTitle; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
