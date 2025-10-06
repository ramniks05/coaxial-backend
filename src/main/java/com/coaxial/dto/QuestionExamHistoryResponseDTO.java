package com.coaxial.dto;

import com.coaxial.entity.QuestionExamHistory;

public class QuestionExamHistoryResponseDTO {
    
    private Long id;
    private Long masterExamId;
    private String masterExamName;
    private Integer appearedYear;
    private String appearedSession;
    private Integer marksInExam;
    private String questionNumberInExam;
    private String difficultyInExam;
    private String notes;
    private String createdAt;
    private String createdByName;
    
    // Constructors
    public QuestionExamHistoryResponseDTO() {}
    
    public QuestionExamHistoryResponseDTO(QuestionExamHistory history) {
        this.id = history.getId();
        this.masterExamId = history.getMasterExam().getId();
        this.masterExamName = history.getMasterExam().getExamName();
        this.appearedYear = history.getAppearedYear() != null ? history.getAppearedYear().getYearValue() : null;
        this.appearedSession = history.getAppearedSession();
        this.marksInExam = history.getMarksInExam();
        this.questionNumberInExam = history.getQuestionNumberInExam();
        this.difficultyInExam = history.getDifficultyInExam();
        this.notes = history.getNotes();
        this.createdAt = history.getCreatedAt() != null ? history.getCreatedAt().toString() : null;
        this.createdByName = history.getCreatedBy() != null ? history.getCreatedBy().getFullName() : null;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMasterExamId() { return masterExamId; }
    public void setMasterExamId(Long masterExamId) { this.masterExamId = masterExamId; }
    
    public String getMasterExamName() { return masterExamName; }
    public void setMasterExamName(String masterExamName) { this.masterExamName = masterExamName; }
    
    public Integer getAppearedYear() { return appearedYear; }
    public void setAppearedYear(Integer appearedYear) { this.appearedYear = appearedYear; }
    
    public String getAppearedSession() { return appearedSession; }
    public void setAppearedSession(String appearedSession) { this.appearedSession = appearedSession; }
    
    public Integer getMarksInExam() { return marksInExam; }
    public void setMarksInExam(Integer marksInExam) { this.marksInExam = marksInExam; }
    
    public String getQuestionNumberInExam() { return questionNumberInExam; }
    public void setQuestionNumberInExam(String questionNumberInExam) { this.questionNumberInExam = questionNumberInExam; }
    
    public String getDifficultyInExam() { return difficultyInExam; }
    public void setDifficultyInExam(String difficultyInExam) { this.difficultyInExam = difficultyInExam; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
}
